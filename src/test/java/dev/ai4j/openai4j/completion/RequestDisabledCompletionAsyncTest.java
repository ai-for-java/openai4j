package dev.ai4j.openai4j.completion;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

class RequestDisabledCompletionAsyncTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .logRequests()
            .logResponses()
            .disableRequests()
            .build();

    @Test
    void testSimpleApi() {
        CompletableFuture<String> future = new CompletableFuture<>();

        client.completion(PROMPT)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void testCustomizableApi() {
        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();

        CompletableFuture<CompletionResponse> future = new CompletableFuture<>();

        client.completion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }
}
