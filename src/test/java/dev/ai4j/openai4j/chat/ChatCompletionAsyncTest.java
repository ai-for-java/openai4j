package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_4O;
import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_4O_AUDIO_PREVIEW;
import static dev.ai4j.openai4j.chat.ChatCompletionTest.*;
import static dev.ai4j.openai4j.chat.FunctionCallUtil.argument;
import static dev.ai4j.openai4j.chat.FunctionCallUtil.argumentsAsMap;
import static dev.ai4j.openai4j.chat.ResponseFormatType.JSON_OBJECT;
import static dev.ai4j.openai4j.chat.ResponseFormatType.TEXT;
import static dev.ai4j.openai4j.chat.ToolType.FUNCTION;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class ChatCompletionAsyncTest extends RateLimitAwareTest {

    private final OpenAiClient client = OpenAiClient.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() throws Exception {

        // when
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(USER_MESSAGE)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        String response = future.get(30, SECONDS);

        // then
        assertThat(response).containsIgnoringCase("hello world");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testCustomizableApi(ChatCompletionModel model) throws Exception {

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
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

        // then
        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().content()).containsIgnoringCase("hello world");

        assertThat(response.content()).containsIgnoringCase("hello world");
    }

    @ParameterizedTest
    @EnumSource(value = ChatCompletionModel.class, mode = EXCLUDE, names = {
            "GPT_4_32K", "GPT_4_32K_0314", "GPT_4_32K_0613", // I don't have access to these models
    })
    void testTools(ChatCompletionModel model) throws Exception {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

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
    void testFunctions(ChatCompletionModel model) throws Exception {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

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
    void testToolChoice(ChatCompletionModel model) throws Exception {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .toolChoice(WEATHER_TOOL_NAME)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

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
    void testFunctionChoice(ChatCompletionModel model) throws Exception {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather in Boston in Celsius?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .functions(WEATHER_FUNCTION)
                .functionCall(WEATHER_TOOL_NAME)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

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
    void testParallelTools(ChatCompletionModel model) throws Exception {

        // given
        UserMessage userMessage = UserMessage.from("What is the weather like in Boston and Madrid?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(userMessage)
                .tools(WEATHER_TOOL)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

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

    private static String currentWeather(String location, String unit) {
        if (unit != null) {
            ChatCompletionTest.Unit.valueOf(unit.toUpperCase());
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
    void testJsonResponseFormat(ChatCompletionModel model) throws Exception {

        // given
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .addUserMessage("Extract information from the following text:\n" +
                        "Who is Klaus Heisler?\n" +
                        "Respond in JSON format with two fields: 'name' and 'surname'")
                .responseFormat(JSON_OBJECT)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

        // then
        assertThat(response.content()).isEqualToIgnoringWhitespace("{\"name\":\"Klaus\",\"surname\":\"Heisler\"}");
    }

    @Test
    void testGpt4Vision() throws Exception {

        // given
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e9/Felis_silvestris_silvestris_small_gradual_decrease_of_quality.png";

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4O)
                .messages(UserMessage.from("What is in this image?", imageUrl))
                .maxCompletionTokens(100)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

        // then
        assertThat(response.content()).containsIgnoringCase("cat");
    }
    
    @Test
    void testGpt4Audio() throws ExecutionException, InterruptedException, TimeoutException, IOException, URISyntaxException {
        // given
        URL resource = getClass().getClassLoader().getResource("sample.b64");;
        final byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4O_AUDIO_PREVIEW)
                .messages(UserMessage.builder()
                        .addText("Give a summary of the audio")
                        .addInputAudio(InputAudio.builder()
                                .format("wav")
                                .data(new String(bytes))
                                .build())
                        .build())
                .maxCompletionTokens(100)
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        // when
        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

        // then
        assertThat(response.choices()).isNotEmpty();
    } 
}
