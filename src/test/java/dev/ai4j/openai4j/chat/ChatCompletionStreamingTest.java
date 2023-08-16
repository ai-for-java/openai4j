package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static dev.ai4j.openai4j.chat.JsonSchemaProperty.*;
import static dev.ai4j.openai4j.chat.Message.userMessage;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class ChatCompletionStreamingTest extends RateLimitAwareTest {

    private static final String USER_MESSAGE = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .logStreamingResponses()
            .build();

    @Test
    void testSimpleApi() throws Exception {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();


        client.chatCompletion(USER_MESSAGE)
                .onPartialResponse(responseBuilder::append)
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();


        String response = future.get(30, SECONDS);
        assertThat(response).containsIgnoringCase("hello world");
    }

    @MethodSource
    @ParameterizedTest
    void testCustomizableApi(ChatCompletionRequest request) throws Exception {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();


        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    String content = partialResponse.choices().get(0).delta().content();
                    if (content != null) {
                        responseBuilder.append(content);
                    }
                })
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();


        String response = future.get(30, SECONDS);
        assertThat(response).containsIgnoringCase("hello world");
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
                .model("gpt-3.5-turbo-0613")
                .messages(userMessage)
                .functions(Function.builder()
                        .name("get_current_weather")
                        .description("Get the current weather in a given location")
                        .addParameter("location", STRING, description("The city and state, e.g. San Francisco, CA"))
                        .addOptionalParameter("unit", STRING, enums(ChatCompletionTest.Unit.class))
                        .build())
                .build();

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();

                    assertThat(delta.content()).isNull();

                    FunctionCall functionCall = delta.functionCall();
                    if (partialResponse.choices().get(0).finishReason() == null) {
                        if (functionCall.name() != null) {
                            responseBuilder.append(functionCall.name());
                        } else if (functionCall.arguments() != null) {
                            responseBuilder.append(functionCall.arguments());
                        }
                    }
                })
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(future::completeExceptionally)
                .execute();

        String response = future.get(30, SECONDS);

        assertThat(response).contains("get_current_weather");
        assertThat(response).contains("location");
        assertThat(response).contains("Boston");
    }
}
