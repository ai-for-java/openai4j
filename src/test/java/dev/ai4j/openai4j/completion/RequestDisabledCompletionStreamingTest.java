package dev.ai4j.openai4j.completion;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;
import dev.ai4j.openai4j.ResponseHandle;

class RequestDisabledCompletionStreamingTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .logStreamingResponses()
            .build();

    @Test
    void testSimpleApi() {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();


        client.completion(PROMPT)
                .onPartialResponse(responseBuilder::append)
                .onComplete(() -> future.complete(responseBuilder.toString()))
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

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();


        client.completion(request)
                .onPartialResponse(response -> responseBuilder.append(response.text()))
                .onComplete(() -> future.complete(responseBuilder.toString()))
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
    void testCancelStreaming() throws InterruptedException {

        OpenAiClient client = OpenAiClient.builder()
                // without caching
                .disableRequests()
                .logRequests()
                .logResponses()
                .logStreamingResponses()
                .build();

        AtomicBoolean streamingStarted = new AtomicBoolean(false);
        AtomicBoolean completed = new AtomicBoolean(false);
        AtomicBoolean errorSucceeded = new AtomicBoolean(false);

        ResponseHandle responseHandle = client.completion("Write a poem about AI in 10 words")
                .onPartialResponse(partialResponse -> {
                    streamingStarted.set(true);
                    System.out.println("[[streaming started]]");
                })
                .onComplete(() -> {
                    completed.set(true);
                    System.out.println("[[completed]]");
                })
                .onError(e -> {
                    errorSucceeded.set(true);
                    System.out.println("[[failed]]");
                })
                .execute();

        Thread.sleep(2000);

        newSingleThreadExecutor().execute(() -> {
            responseHandle.cancel();
            System.out.println("[[streaming cancelled]]");
        });

        assertThat(streamingStarted).isFalse();
        assertThat(errorSucceeded).isTrue();
        assertThat(completed).isFalse();
    }
}
