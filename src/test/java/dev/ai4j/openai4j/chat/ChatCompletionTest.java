package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

import static dev.ai4j.openai4j.chat.FunctionCallUtil.argument;
import static dev.ai4j.openai4j.chat.FunctionCallUtil.argumentsAsMap;
import static dev.ai4j.openai4j.chat.JsonSchemaProperty.*;
import static dev.ai4j.openai4j.chat.ResponseFormatType.JSON_OBJECT;
import static dev.ai4j.openai4j.chat.ResponseFormatType.TEXT;
import static dev.ai4j.openai4j.chat.ToolType.FUNCTION;
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
            .addParameter("location", STRING, description("The city and state, e.g. San Francisco, CA"))
            .addOptionalParameter("unit", STRING, enums(Unit.class))
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
            "GPT_3_5_TURBO_0125", // don't have access to it yet
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including logit_bias and response_format
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
                .maxTokens(3)
                .presencePenalty(0.0)
                .frequencyPenalty(0.0)
                .logitBias(singletonMap("50256", -100))
                .user("Klaus")
                .responseFormat(TEXT)
                .seed(42)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().content()).containsIgnoringCase("hello world");

        assertThat(response.content()).containsIgnoringCase("hello world");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_3_5_TURBO_0125", // don't have access to it yet
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including logit_bias and response_format
    })
    void testTools(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston?");

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
            "GPT_3_5_TURBO_0125", // don't have access to it yet
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including logit_bias and response_format
    })
    void testFunctions(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston?");

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
            "GPT_3_5_TURBO_0125", // don't have access to it yet
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including logit_bias and response_format
    })
    void testToolChoice(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston?");

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
            "GPT_3_5_TURBO_0125", // don't have access to it yet
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_0314", // Does not support tools/functions
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including logit_bias and response_format
    })
    void testFunctionChoice(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston?");

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
    @EnumSource(value = ChatCompletionModel.class, mode = INCLUDE, names = {
            // "GPT_3_5_TURBO", // still points to the old model
            "GPT_3_5_TURBO_1106",
            // "GPT_3_5_TURBO_0125", // don't have access to it yet

            "GPT_4_TURBO_PREVIEW",
            "GPT_4_1106_PREVIEW",
            "GPT_4_0125_PREVIEW",
            "GPT_4O"
    })
    void testParallelTools(ChatCompletionModel model) {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston and Madrid?");

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
    @EnumSource(value = ChatCompletionModel.class, mode = INCLUDE, names = {
            // "GPT_3_5_TURBO", // still points to the old model
            "GPT_3_5_TURBO_1106",
            // "GPT_3_5_TURBO_0125", // don't have access to it yet

            "GPT_4_TURBO_PREVIEW",
            "GPT_4_1106_PREVIEW",
            "GPT_4_0125_PREVIEW"
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

    @Test
    void testGpt4Vision() {

        // given
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(ChatCompletionModel.GPT_4O)
                .messages(UserMessage.from("What is in this image?", imageUrl))
                .maxTokens(100)
                .build();

        // when
        ChatCompletionResponse response = client.chatCompletion(request).execute();

        // then
        assertThat(response.content()).containsIgnoringCase("green");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_3_5_TURBO_0125", // don't have access to it yet
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
            "GPT_4_VISION_PREVIEW" // Does not support many things now, including logit_bias and response_format
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
