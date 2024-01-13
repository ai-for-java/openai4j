package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.ResponseHandle;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class CompletionStreamingTest extends RateLimitAwareTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .logStreamingResponses()
            .build();

    @Test
    void testSimpleApi() throws Exception {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();


        client.completion(PROMPT)
                .onPartialResponse(responseBuilder::append)
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(Throwable::printStackTrace)
                .execute();


        String response = future.get(30, SECONDS);
        assertThat(response).containsIgnoringCase("hello world");
    }

    @Test
    void testCustomizableApi() throws Exception {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();

        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();


        client.completion(request)
                .onPartialResponse(response -> responseBuilder.append(response.text()))
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(Throwable::printStackTrace)
                .execute();


        String response = future.get(30, SECONDS);
        assertThat(response).containsIgnoringCase("hello world");
    }

    @Test
    void testCancelStreamingAfterStreamingStarted() throws InterruptedException {

        AtomicBoolean streamingStarted = new AtomicBoolean(false);
        AtomicBoolean streamingCancelled = new AtomicBoolean(false);
        AtomicBoolean cancellationSucceeded = new AtomicBoolean(true);

        ResponseHandle responseHandle = client.completion("Write a poem about AI in 10 words")
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

        ResponseHandle responseHandle = client.completion("Write a poem about AI in 10 words")
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
