package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.shared.Usage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_4O_MINI;
import static dev.ai4j.openai4j.chat.FunctionCallUtil.argument;
import static dev.ai4j.openai4j.chat.FunctionCallUtil.argumentsAsMap;
import static dev.ai4j.openai4j.chat.ResponseFormatType.JSON_OBJECT;
import static dev.ai4j.openai4j.chat.ResponseFormatType.JSON_SCHEMA;
import static dev.ai4j.openai4j.chat.ResponseFormatType.TEXT;
import static dev.ai4j.openai4j.chat.ToolType.FUNCTION;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

class ChatCompletionTest extends RateLimitAwareTest {

    static final String SYSTEM_MESSAGE = "Be concise";
    static final String USER_MESSAGE = "Write exactly the following 2 words: 'hello world'";

    static final String WEATHER_TOOL_NAME = "get_current_weather";
    static final Function WEATHER_FUNCTION = Function.builder()
            .name(WEATHER_TOOL_NAME)
            .description("Get the current weather in a given location")
            .parameters(JsonObjectSchema.builder()
                    .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                        put("location", JsonStringSchema.builder()
                                .description("The city and state, e.g. San Francisco, CA")
                                .build());
                        put("unit", JsonEnumSchema.builder()
                                .enumValues(Unit.class)
                                .build());
                    }})
                    .required(asList("location", "unit"))
                    .build())
            .build();
    static final Tool WEATHER_TOOL = Tool.from(WEATHER_FUNCTION);

    private final OpenAiClient client = OpenAiClient.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {

        // when
        String response = client.chatCompletion(USER_MESSAGE).execute();

        // then
        assertThat(response).containsIgnoringCase("hello world");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613" // I don't have access to these models
    })
    void testCustomizableApi(ChatCompletionModel model) {

        // given
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .addSystemMessage(SYSTEM_MESSAGE)
                .addUserMessage(USER_MESSAGE)
                .temperature(1.0)
                .topP(0.1)
                .n(1)
                .stream(false)
                .stop("one", "two")
                .maxCompletionTokens(3)
                .presencePenalty(0.0)
                .frequencyPenalty(0.0)
                .logitBias(singletonMap("50256", -100))
                .user("Klaus")
                .responseFormat(TEXT)
                .seed(42)
                .store(true)
                .metadata(new HashMap<String, String>(){{
                    put("one", "1");
                    put("two", "2");
                }})
                .serviceTier("default")
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.id()).isNotBlank();
        assertThat(response.created()).isPositive();
        assertThat(response.model()).isNotBlank();
        // TODO assertThat(response.systemFingerprint()).isNotBlank();
        assertThat(response.serviceTier()).isNotBlank();

        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().content()).containsIgnoringCase("hello world");

        assertThat(response.content()).containsIgnoringCase("hello world");

        Usage usage = response.usage();
        assertThat(usage.promptTokens()).isGreaterThan(0);
        assertThat(usage.promptTokensDetails().cachedTokens()).isEqualTo(0);

        assertThat(usage.completionTokens()).isGreaterThan(0);
        assertThat(usage.completionTokensDetails().reasoningTokens()).isEqualTo(0);

        assertThat(usage.totalTokens()).isEqualTo(usage.promptTokens() + usage.completionTokens());
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testTools(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        AssistantMessage assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).isNull();
        assertThat(assistantMessage.functionCall()).isNull();
        assertThat(assistantMessage.toolCalls()).isNotNull().hasSize(1);

        ToolCall toolCall = assistantMessage.toolCalls().get(0);
        assertThat(toolCall.id()).isNotBlank();
        assertThat(toolCall.type()).isEqualTo(FUNCTION);
        assertThat(toolCall.function()).isNotNull();

        FunctionCall functionCall = toolCall.function();
        assertThat(functionCall.name()).isEqualTo(WEATHER_TOOL_NAME);
        assertThat(functionCall.arguments()).isNotBlank();

        Map<String, Object> arguments = argumentsAsMap(functionCall.arguments());
        assertThat(arguments).hasSizeBetween(1, 2);
        assertThat(arguments.get("location").toString()).contains("Boston");

        // given
        String location = argument("location", functionCall);
        String unit = argument("unit", functionCall);
        String currentWeather = currentWeather(location, unit);
        ToolMessage toolMessage = ToolMessage.from(toolCall.id(), currentWeather);

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage, assistantMessage, toolMessage)
                .build();

        // when
        ChatCompletionResponse secondResponse = client.chatCompletion(secondRequest).execute();

        // then
        assertThat(secondResponse.content()).contains("11");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testStrictTools(ChatCompletionModel model) {

        // given
        boolean strict = true;
        boolean additionalProperties = false; // must be set explicitly to false when strict=true

        Function weatherFunction = Function.builder()
                .strict(strict)
                .name(WEATHER_TOOL_NAME)
                .description("Get the current weather in a given location")
                .parameters(JsonObjectSchema.builder()
                        .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                            put("location", JsonObjectSchema.builder()
                                    .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                                        put("city", JsonStringSchema.builder()
                                                .description("The city, e.g. San Francisco")
                                                .build());
                                        put("country", JsonStringSchema.builder()
                                                .description("The country, e.g. USA")
                                                .build());
                                    }})
                                    .additionalProperties(additionalProperties)
                                    .required(asList("city", "country")) // all properties must be required when strict=true
                                    .build());
                            put("unit", JsonEnumSchema.builder()
                                    .enumValues(Unit.class)
                                    .build());
                        }})
                        .additionalProperties(additionalProperties)
                        .required(asList("location", "unit")) // all properties must be required when strict=true
                        .build())
                .build();

        UserMessage userMessage = UserMessage.from("What is the temperature in Madrid in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(Tool.from(weatherFunction))
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        AssistantMessage assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).isNull();
        assertThat(assistantMessage.functionCall()).isNull();
        assertThat(assistantMessage.toolCalls()).isNotNull().hasSize(1);

        ToolCall toolCall = assistantMessage.toolCalls().get(0);
        assertThat(toolCall.id()).isNotBlank();
        assertThat(toolCall.type()).isEqualTo(FUNCTION);
        assertThat(toolCall.function()).isNotNull();

        FunctionCall functionCall = toolCall.function();
        assertThat(functionCall.name()).isEqualTo(WEATHER_TOOL_NAME);
        assertThat(functionCall.arguments()).isNotBlank();

        Map<String, Object> arguments = argumentsAsMap(functionCall.arguments());
        assertThat(arguments).hasSize(2);

        Map<String, String> location = argument("location", functionCall);
        String city = location.get("city");
        assertThat(city).isEqualTo("Madrid");
        assertThat(location.get("country")).isEqualTo("Spain");

        String unit = argument("unit", functionCall);
        assertThat(unit).isEqualTo("CELSIUS");

        // given
        String currentWeather = currentWeather(city, unit);
        ToolMessage toolMessage = ToolMessage.from(toolCall.id(), currentWeather);

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage, assistantMessage, toolMessage)
                .build();

        // when
        ChatCompletionResponse secondResponse = client.chatCompletion(secondRequest).execute();

        // then
        assertThat(secondResponse.content()).contains("22");
    }

    @Test
    void testToolWithoutParameters() {

        // given
        Function function = Function.builder()
                .name("current_time")
                .build();

        UserMessage userMessage = UserMessage.from("What is the time now?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4O_MINI)
                .messages(userMessage)
                .tools(Tool.from(function))
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        AssistantMessage assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).isNull();
        assertThat(assistantMessage.functionCall()).isNull();
        assertThat(assistantMessage.toolCalls()).isNotNull().hasSize(1);

        ToolCall toolCall = assistantMessage.toolCalls().get(0);
        assertThat(toolCall.id()).isNotBlank();
        assertThat(toolCall.type()).isEqualTo(FUNCTION);
        assertThat(toolCall.function()).isNotNull();

        FunctionCall functionCall = toolCall.function();
        assertThat(functionCall.name()).isEqualTo("current_time");
        assertThat(functionCall.arguments()).isEqualTo("{}");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testFunctions(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        AssistantMessage assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).isNull();
        assertThat(assistantMessage.toolCalls()).isNull();

        FunctionCall functionCall = assistantMessage.functionCall();
        assertThat(functionCall.name()).isEqualTo(WEATHER_TOOL_NAME);
        assertThat(functionCall.arguments()).isNotBlank();

        Map<String, Object> arguments = argumentsAsMap(functionCall.arguments());
        assertThat(arguments).hasSizeBetween(1, 2);
        assertThat(arguments.get("location").toString()).contains("Boston");

        // given
        String location = argument("location", functionCall);
        String unit = argument("unit", functionCall);
        String currentWeather = currentWeather(location, unit);
        FunctionMessage functionMessage = FunctionMessage.from(functionCall.name(), currentWeather);

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage, assistantMessage, functionMessage)
                .build();

        // when
        ChatCompletionResponse secondResponse = client.chatCompletion(secondRequest).execute();

        // then
        assertThat(secondResponse.content()).contains("11");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testToolChoice(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .toolChoice(WEATHER_TOOL_NAME)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        AssistantMessage assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).isNull();
        assertThat(assistantMessage.toolCalls()).isNotNull().hasSize(1);

        ToolCall toolCall = assistantMessage.toolCalls().get(0);
        assertThat(toolCall.id()).isNotBlank();
        assertThat(toolCall.type()).isEqualTo(FUNCTION);
        assertThat(toolCall.function()).isNotNull();

        FunctionCall functionCall = toolCall.function();
        assertThat(functionCall.name()).isEqualTo(WEATHER_TOOL_NAME);
        assertThat(functionCall.arguments()).isNotBlank();

        Map<String, Object> arguments = argumentsAsMap(functionCall.arguments());
        assertThat(arguments).hasSizeBetween(1, 2);
        assertThat(arguments.get("location").toString()).contains("Boston");

        // given
        String location = argument("location", functionCall);
        String unit = argument("unit", functionCall);
        String currentWeather = currentWeather(location, unit);
        ToolMessage toolMessage = ToolMessage.from(toolCall.id(), currentWeather);

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage, assistantMessage, toolMessage)
                .build();

        // when
        ChatCompletionResponse secondResponse = client.chatCompletion(secondRequest).execute();

        // then
        assertThat(secondResponse.content()).contains("11");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testFunctionChoice(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .functionCall(WEATHER_TOOL_NAME)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        AssistantMessage assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).isNull();
        assertThat(assistantMessage.toolCalls()).isNull();

        FunctionCall functionCall = assistantMessage.functionCall();
        assertThat(functionCall.name()).isEqualTo(WEATHER_TOOL_NAME);
        assertThat(functionCall.arguments()).isNotBlank();

        Map<String, Object> arguments = argumentsAsMap(functionCall.arguments());
        assertThat(arguments).hasSizeBetween(1, 2);
        assertThat(arguments.get("location").toString()).contains("Boston");

        // given
        String location = argument("location", functionCall);
        String unit = argument("unit", functionCall);
        String currentWeather = currentWeather(location, unit);
        FunctionMessage functionMessage = FunctionMessage.from(functionCall.name(), currentWeather);

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage, assistantMessage, functionMessage)
                .build();

        // when
        ChatCompletionResponse secondResponse = client.chatCompletion(secondRequest).execute();

        // then
        assertThat(secondResponse.content()).contains("11");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4", "GPT_4_0613", // Does not support parallel tools
    })
    void testParallelTools(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston and Madrid?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .parallelToolCalls(true)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        AssistantMessage assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.content()).isNull();
        assertThat(assistantMessage.toolCalls()).isNotNull().hasSize(2);

        ToolCall toolCall1 = assistantMessage.toolCalls().get(0);
        assertThat(toolCall1.id()).isNotBlank();
        assertThat(toolCall1.type()).isEqualTo(FUNCTION);
        assertThat(toolCall1.function()).isNotNull();

        FunctionCall functionCall1 = toolCall1.function();
        assertThat(functionCall1.name()).isEqualTo(WEATHER_TOOL_NAME);
        assertThat(functionCall1.arguments()).isNotBlank();

        Map<String, Object> arguments1 = argumentsAsMap(functionCall1.arguments());
        assertThat(arguments1).hasSizeBetween(1, 2);
        assertThat(arguments1.get("location").toString()).contains("Boston");

        ToolCall toolCall2 = assistantMessage.toolCalls().get(1);
        assertThat(toolCall2.id()).isNotBlank();
        assertThat(toolCall2.type()).isEqualTo(FUNCTION);
        assertThat(toolCall2.function()).isNotNull();

        FunctionCall functionCall2 = toolCall2.function();
        assertThat(functionCall2.name()).isEqualTo(WEATHER_TOOL_NAME);
        assertThat(functionCall2.arguments()).isNotBlank();

        Map<String, Object> arguments2 = argumentsAsMap(functionCall2.arguments());
        assertThat(arguments2).hasSizeBetween(1, 2);
        assertThat(arguments2.get("location").toString()).contains("Madrid");

        // given
        String location1 = argument("location", functionCall1);
        String unit1 = argument("unit", functionCall1);
        String currentWeather1 = currentWeather(location1, unit1);
        ToolMessage toolMessage1 = ToolMessage.from(toolCall1.id(), currentWeather1);

        String location2 = argument("location", functionCall2);
        String unit2 = argument("unit", functionCall2);
        String currentWeather2 = currentWeather(location2, unit2);
        ToolMessage toolMessage2 = ToolMessage.from(toolCall2.id(), currentWeather2);

        ChatCompletionRequest secondRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage, assistantMessage, toolMessage1, toolMessage2)
                .build();

        // when
        ChatCompletionResponse secondResponse = client.chatCompletion(secondRequest).execute();

        // then
        assertThat(secondResponse.content()).contains("11", "22");
    }

    static String currentWeather(String location, String unit) {
        if (unit != null) {
            Unit.valueOf(unit.toUpperCase());
        }

        if (location.contains("Boston")) {
            return "{\"temperature\": 11,\"unit\":\"celsius\",\"description\":\"Foggy\"}";
        } else if (location.contains("Madrid")) {
            return "{\"temperature\": 22,\"unit\":\"celsius\",\"description\":\"Sunny\"}";
        } else {
            throw new IllegalArgumentException("Unexpected location: " + location);
        }
    }

    enum Unit {
        CELSIUS, FAHRENHEIT
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4", "GPT_4_0613", // Does not support response_format
    })
    void testJsonResponseFormat(ChatCompletionModel model) {

        // given
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .addUserMessage("Extract information from the following text:\n" +
                        "Who is Klaus Heisler?\n" +
                        "Respond in JSON format with two fields: 'name' and 'surname'")
                .responseFormat(JSON_OBJECT)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).isEqualToIgnoringWhitespace("{\"name\":\"Klaus\",\"surname\":\"Heisler\"}");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = INCLUDE, names = {
            "GPT_4O_2024_08_06", "GPT_4O_MINI", "GPT_4O_MINI_2024_07_18"
    })
    void testStrictJsonResponseFormat(ChatCompletionModel model) {

        // given
        boolean strict = true;

        JsonSchema jsonSchema = JsonSchema.builder()
                .name("person")
                .schema(JsonObjectSchema.builder()
                        .description("a person")
                        .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                            put("name", JsonObjectSchema.builder()
                                    .description("name of a person")
                                    .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                                        put("first_name", JsonStringSchema.builder()
                                                .description("first name of a person")
                                                .build());
                                        put("last_name", JsonStringSchema.builder()
                                                .description("last name of a person")
                                                .build());
                                    }})
                                    .required(asList("first_name", "last_name"))
                                    .additionalProperties(false)
                                    .build());
                            put("age", JsonIntegerSchema.builder()
                                    .description("age of a person")
                                    .build());
                        }})
                        .required(asList("name", "age"))
                        .additionalProperties(false)
                        .build())
                .strict(strict)
                .build();

        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(JSON_SCHEMA)
                .jsonSchema(jsonSchema)
                .build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .addUserMessage("Klaus Heisler is 37 years old")
                .responseFormat(responseFormat)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).isEqualToIgnoringWhitespace(
                "{\"name\":{\"first_name\":\"Klaus\",\"last_name\":\"Heisler\"},\"age\":37}");
    }


    @Test
    void testJsonResponseFormatWithExplicitRecursion() {

        // given
        boolean strict = true;

        JsonSchema jsonSchema = JsonSchema.builder()
                .name("person")
                .schema(JsonObjectSchema.builder()
                        .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                            put("name", JsonStringSchema.builder().build());
                            put("children", JsonArraySchema.builder()
                                    .items(JsonReferenceSchema.builder()
                                            .reference("#/$defs/person") // explicit recursion
                                            .build())
                                    .build());
                        }})
                        .required(asList("name", "children"))
                        .additionalProperties(false)
                        .definitions(new LinkedHashMap<String, JsonSchemaElement>() {{
                            put("person", JsonObjectSchema.builder()
                                    .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                                        put("name", JsonStringSchema.builder().build());
                                        put("children", JsonArraySchema.builder()
                                                .items(JsonReferenceSchema.builder()
                                                        .reference("#/$defs/person") // explicit recursion
                                                        .build())
                                                .build());
                                    }})
                                    .required(asList("name", "children"))
                                    .additionalProperties(false)
                                    .build());
                        }})
                        .build())
                .strict(strict)
                .build();

        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(JSON_SCHEMA)
                .jsonSchema(jsonSchema)
                .build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4O_MINI)
                .addUserMessage("Extract information from the following text: Anna has 2 children: David and Kate")
                .responseFormat(responseFormat)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).isEqualToIgnoringWhitespace(
                "{\"name\":\"Anna\",\"children\":[" +
                        "{\"name\":\"David\",\"children\":[]}," +
                        "{\"name\":\"Kate\",\"children\":[]}" +
                        "]}");
    }

    @Test
    void testJsonResponseFormatWithRootRecursion() {

        // given
        boolean strict = true;

        JsonSchema jsonSchema = JsonSchema.builder()
                .name("person")
                .schema(JsonObjectSchema.builder()
                        .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                            put("name", JsonStringSchema.builder().build());
                            put("children", JsonArraySchema.builder()
                                    .items(JsonReferenceSchema.builder()
                                            .reference("#") // root recursion
                                            .build())
                                    .build());
                        }})
                        .required(asList("name", "children"))
                        .additionalProperties(false)
                        .build())
                .strict(strict)
                .build();

        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(JSON_SCHEMA)
                .jsonSchema(jsonSchema)
                .build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4O_MINI)
                .addUserMessage("Extract information from the following text: Anna has 2 children: David and Kate")
                .responseFormat(responseFormat)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).isEqualToIgnoringWhitespace(
                "{\"name\":\"Anna\",\"children\":[" +
                        "{\"name\":\"David\",\"children\":[]}," +
                        "{\"name\":\"Kate\",\"children\":[]}" +
                        "]}");
    }

    @Test
    void testJsonResponseFormatWithAnyOf() {

        // given
        boolean strict = true;

        final JsonObjectSchema circle = JsonObjectSchema.builder()
                .description("Circle")
                .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                    put("radius", JsonNumberSchema.builder().build());
                }})
                .required(singletonList("radius"))
                .additionalProperties(false)
                .build();
        final JsonObjectSchema rectangle = JsonObjectSchema.builder()
                .description("Rectangle")
                .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                    put("width", JsonNumberSchema.builder().build());
                    put("height", JsonNumberSchema.builder().build());
                }})
                .required(asList("width", "height"))
                .additionalProperties(false)
                .build();
        final JsonSchema jsonSchema = JsonSchema.builder()
                .name("shapes")
                .schema(JsonObjectSchema.builder()
                        .description("Shapes")
                        .properties(new LinkedHashMap<String, JsonSchemaElement>() {{
                            put("shapes", JsonArraySchema.builder()
                                    .items(JsonAnyOfSchema.builder()
                                            .description("Shape")
                                            .anyOf(asList(circle, rectangle))
                                            .build())
                                    .build()
                            );
                        }})
                        .required(singletonList("shapes"))
                        .additionalProperties(false)
                        .build())
                .strict(strict)
                .build();

        final ResponseFormat responseFormat = ResponseFormat.builder()
                .type(JSON_SCHEMA)
                .jsonSchema(jsonSchema)
                .build();

        final ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4O_MINI)
                .addUserMessage("Extract information from the following text:\n" +
                        "1. A circle with a radius of 5\n" +
                        "2. A rectangle with a width of 10 and a height of 20")
                .responseFormat(responseFormat)
                .build();

        // when
        final ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).isEqualToIgnoringWhitespace(
                "{\"shapes\":[{\"radius\":5},{\"width\":10,\"height\":20}]}");
    }

    @Test
    void testGpt4Vision() {

        // given
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e9/Felis_silvestris_silvestris_small_gradual_decrease_of_quality.png";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(ChatCompletionModel.GPT_4O)
                .messages(UserMessage.from("What is in this image?", imageUrl))
                .maxCompletionTokens(100)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).containsIgnoringCase("cat");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testUserMessageWithStringContent(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.builder()
                .content("What is the capital of Germany?")
                .build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).contains("Berlin");
    }
}
