package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.FunctionCallUtil;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static dev.ai4j.openai4j.Model.GPT_4_1106_PREVIEW;
import static dev.ai4j.openai4j.chat.JsonSchemaProperty.*;
import static dev.ai4j.openai4j.chat.Message.userMessage;
import static dev.ai4j.openai4j.chat.Role.ASSISTANT;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class ChatCompletionAsyncTest extends RateLimitAwareTest {

    private static final String USER_MESSAGE = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() throws Exception {

        CompletableFuture<String> future = new CompletableFuture<>();


        client.chatCompletion(USER_MESSAGE)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        String response = future.get(30, SECONDS);

        assertThat(response).containsIgnoringCase("hello world");
    }

    @MethodSource
    @ParameterizedTest
    void testCustomizableApi(ChatCompletionRequest request) throws Exception {

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        ChatCompletionResponse response = future.get(30, SECONDS);

        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().role()).isEqualTo(ASSISTANT);
        assertThat(response.choices().get(0).message().content().get(0).text()).containsIgnoringCase("hello world");

        assertThat(response.content()).containsIgnoringCase("hello world");
    }

    static Stream<Arguments> testCustomizableApi() {
        return Stream.of(
                Arguments.of(
                        ChatCompletionRequest.builder()
                                .messages(singletonList(userMessage(USER_MESSAGE)))
                                .build()
                ),
                Arguments.of(
                        ChatCompletionRequest.builder()
                                .messages(userMessage(USER_MESSAGE))
                                .build()
                ),
                Arguments.of(
                        ChatCompletionRequest.builder()
                                .addUserMessage(USER_MESSAGE)
                                .build()
                )
        );
    }

    @Test
    void testFunctions() throws Exception {

        Message userMessage = userMessage("What is the weather like in Boston?");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4_1106_PREVIEW)
                .messages(userMessage)
                .tools(Tool.builder()
                        .type(ToolType.FUNCTION.stringValue())
                        .function(Function.builder()
                                .name("get_current_weather")
                                .description("Get the current weather in a given location")
                                .addParameter("location", STRING, description("The city and state, e.g. San Francisco, CA"))
                                .addOptionalParameter("unit", STRING, enums(ChatCompletionTest.Unit.class))
                                .build())
                        .build())
                .build();

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        ChatCompletionResponse response = future.get(30, SECONDS);

        Message assistantMessage = response.choices().get(0).message();
        assertThat(assistantMessage.role()).isEqualTo(ASSISTANT);
        assertThat(assistantMessage.content()).isNull();

        FunctionCall functionCall = assistantMessage.functionCall();
        assertThat(functionCall.name()).isEqualTo("get_current_weather");
        assertThat(functionCall.arguments()).isNotBlank();

        Map<String, Object> arguments = FunctionCallUtil.argumentsAsMap(functionCall.arguments());
        assertThat(arguments).hasSize(1);
        assertThat(arguments.get("location").toString()).contains("Boston");
    }
}
