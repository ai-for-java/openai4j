package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.ResponseHandle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static dev.ai4j.openai4j.Model.GPT_4_1106_PREVIEW;
import static dev.ai4j.openai4j.chat.JsonSchemaProperty.*;
import static dev.ai4j.openai4j.chat.Message.userMessage;
import static java.net.Proxy.Type.HTTP;
import static java.util.Collections.singletonList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
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

        Message userMessage = userMessage(USER_MESSAGE);
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4_1106_PREVIEW)
                .messages(userMessage)
                .build();


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

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    Delta delta = partialResponse.choices().get(0).delta();

                    assertThat(delta.content()).isNull();

                    List<ToolCalls> toolCalls = delta.toolCalls();

                    if (partialResponse.choices().get(0).finishReason() == null) {
                        toolCalls.stream().forEach(toolCall ->{
                            if (toolCall.function().name() != null) {
                                responseBuilder.append(toolCall.function().name());
                            } else if (toolCall.function().arguments() != null) {
                                responseBuilder.append(toolCall.function().arguments());
                            }
                        });
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

    @Test
    void testCancelStreamingAfterStreamingStarted() throws InterruptedException {

        AtomicBoolean streamingStarted = new AtomicBoolean(false);
        AtomicBoolean streamingCancelled = new AtomicBoolean(false);
        AtomicBoolean cancellationSucceeded = new AtomicBoolean(true);

        ResponseHandle responseHandle = client.chatCompletion("Write a poem about AI in 10 words")
                .onPartialResponse(partialResponse -> {
                    streamingStarted.set(true);
                    if (streamingCancelled.get()) {
                        cancellationSucceeded.set(false);
                    }
                })
                .onComplete(() -> cancellationSucceeded.set(false))
                .onError(e -> cancellationSucceeded.set(false))
                .execute();

        while (!streamingStarted.get()) {
            Thread.sleep(200);
        }

        newSingleThreadExecutor().execute(() -> {
            responseHandle.cancel();
            streamingCancelled.set(true);
        });

        while (!streamingCancelled.get()) {
            Thread.sleep(200);
        }
        Thread.sleep(5000);

        assertThat(cancellationSucceeded).isTrue();
    }

    @Test
    void testCancelStreamingBeforeStreamingStarted() throws InterruptedException {

        AtomicBoolean cancellationSucceeded = new AtomicBoolean(true);

        ResponseHandle responseHandle = client.chatCompletion("Write a poem about AI in 10 words")
                .onPartialResponse(partialResponse -> cancellationSucceeded.set(false))
                .onComplete(() -> cancellationSucceeded.set(false))
                .onError(e -> cancellationSucceeded.set(false))
                .execute();

        AtomicBoolean streamingCancelled = new AtomicBoolean(false);

        newSingleThreadExecutor().execute(() -> {
            responseHandle.cancel();
            streamingCancelled.set(true);
        });

        while (!streamingCancelled.get()) {
            Thread.sleep(200);
        }
        Thread.sleep(5000);

        assertThat(cancellationSucceeded).isTrue();
    }
}
