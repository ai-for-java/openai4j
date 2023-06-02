package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static dev.ai4j.openai4j.chat.Message.userMessage;
import static dev.ai4j.openai4j.chat.Role.ASSISTANT;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class ChatCompletionAsyncTest extends RateLimitAwareTest {

    private static final String USER_MESSAGE = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() throws ExecutionException, InterruptedException, TimeoutException {

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
    void testCustomizableApi(ChatCompletionRequest request) throws ExecutionException, InterruptedException, TimeoutException {

        CompletableFuture<ChatCompletionResponse> future = new CompletableFuture<>();

        client.chatCompletion(request)
                .onResponse(future::complete)
                .ignoreErrors()
                .execute();

        client.chatCompletion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        ChatCompletionResponse response = future.get(30, SECONDS);

        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).message().role()).isEqualTo(ASSISTANT);
        assertThat(response.choices().get(0).message().content()).containsIgnoringCase("hello world");

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
}