package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiService;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.StreamingResponseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static dev.ai4j.openai4j.chat.Message.userMessage;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class ChatCompletionStreamingTest extends RateLimitAwareTest {

    private static final String USER_MESSAGE = "write exactly the following 2 words: 'hello world'";

    private final OpenAiService openAiService = new OpenAiService(System.getenv("OPENAI_API_KEY"));

    static Stream<Arguments> testWithBuilder() {
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

    @MethodSource
    @ParameterizedTest
    void testWithBuilder(ChatCompletionRequest request) throws ExecutionException, InterruptedException, TimeoutException {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> partialResponseFuture = new CompletableFuture<>();
        CompletableFuture<String> completeResponseFuture = new CompletableFuture<>();

        openAiService.streamChatCompletions(request, new StreamingResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                responseBuilder.append(partialResponse);
            }

            @Override
            public void onCompleteResponse(String completeResponse) {
                partialResponseFuture.complete(responseBuilder.toString());
                completeResponseFuture.complete(completeResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                partialResponseFuture.completeExceptionally(t);
            }
        });

        String completeResponseBuiltFromPartialResponses = partialResponseFuture.get(30, SECONDS);
        assertThat(completeResponseBuiltFromPartialResponses).containsIgnoringCase("hello world");

        String providedCompleteResponse = completeResponseFuture.get(30, SECONDS);
        assertThat(providedCompleteResponse).isEqualTo(completeResponseBuiltFromPartialResponses);
    }

    @Test
    void testWithUserMessage() throws ExecutionException, InterruptedException, TimeoutException {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> partialResponseFuture = new CompletableFuture<>();
        CompletableFuture<String> completeResponseFuture = new CompletableFuture<>();

        openAiService.streamChatCompletion(USER_MESSAGE, new StreamingResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                responseBuilder.append(partialResponse);
            }

            @Override
            public void onCompleteResponse(String completeResponse) {
                partialResponseFuture.complete(responseBuilder.toString());
                completeResponseFuture.complete(completeResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                partialResponseFuture.completeExceptionally(t);
            }
        });

        String completeResponseBuiltFromPartialResponses = partialResponseFuture.get(30, SECONDS);
        assertThat(completeResponseBuiltFromPartialResponses).containsIgnoringCase("hello world");

        String providedCompleteResponse = completeResponseFuture.get(30, SECONDS);
        assertThat(providedCompleteResponse).isEqualTo(completeResponseBuiltFromPartialResponses);
    }
}
