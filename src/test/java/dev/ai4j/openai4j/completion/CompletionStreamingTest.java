package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.ResponseHandle;
import dev.ai4j.openai4j.shared.StreamOptions;
import dev.ai4j.openai4j.shared.Usage;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

        // given
        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<Usage> usageReference = new AtomicReference<>();

        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .streamOptions(StreamOptions.builder()
                        .includeUsage(true)
                        .build())
                .build();

        // when
        client.completion(request)
                .onPartialResponse(response -> {
                    for (CompletionChoice choice : response.choices()) {
                        String text = choice.text();
                        if (text != null) {
                            responseBuilder.append(text);
                        }
                    }
                    Usage usage = response.usage();
                    if (usage != null) {
                        usageReference.set(usage);
                    }
                })
                .onComplete(() -> future.complete(responseBuilder.toString()))
                .onError(Throwable::printStackTrace)
                .execute();

        String response = future.get(30, SECONDS);

        // then
        assertThat(response).containsIgnoringCase("hello world");

        Usage usage = usageReference.get();
        assertThat(usage.promptTokens()).isGreaterThan(0);
        assertThat(usage.completionTokens()).isGreaterThan(0);
        assertThat(usage.completionTokensDetails()).isNull();
        assertThat(usage.totalTokens()).isEqualTo(usage.promptTokens() + usage.completionTokens());
    }

    @Test
    void testCancelStreamingAfterStreamingStarted() throws InterruptedException {

        OpenAiClient client = OpenAiClient.builder()
                // without caching
                .openAiApiKey(System.getenv("OPENAI_API_KEY"))
                .logRequests()
                .logResponses()
                .logStreamingResponses()
                .build();

        AtomicBoolean streamingStarted = new AtomicBoolean(false);
        AtomicBoolean streamingCancelled = new AtomicBoolean(false);
        AtomicBoolean cancellationSucceeded = new AtomicBoolean(true);

        ResponseHandle responseHandle = client.completion("Write a poem about AI in 10 words")
                .onPartialResponse(partialResponse -> {
                    streamingStarted.set(true);
                    System.out.println("[[streaming started]]");
                    if (streamingCancelled.get()) {
                        cancellationSucceeded.set(false);
                        System.out.println("[[cancellation failed]]");
                    }
                })
                .onComplete(() -> {
                    cancellationSucceeded.set(false);
                    System.out.println("[[cancellation failed]]");
                })
                .onError(e -> {
                    cancellationSucceeded.set(false);
                    System.out.println("[[cancellation failed]]");
                })
                .execute();

        while (!streamingStarted.get()) {
            Thread.sleep(10);
        }

        newSingleThreadExecutor().execute(() -> {
            responseHandle.cancel();
            streamingCancelled.set(true);
            System.out.println("[[streaming cancelled]]");
        });

        while (!streamingCancelled.get()) {
            Thread.sleep(10);
        }
        Thread.sleep(2000);

        assertThat(cancellationSucceeded).isTrue();
    }

    @Test
    void testCancelStreamingBeforeStreamingStarted() throws InterruptedException {

        AtomicBoolean cancellationSucceeded = new AtomicBoolean(true);

        ResponseHandle responseHandle = client.completion("Write a poem about AI in 10 words")
                .onPartialResponse(partialResponse -> {
                    cancellationSucceeded.set(false);
                    System.out.println("[[cancellation failed]]");
                })
                .onComplete(() -> {
                    cancellationSucceeded.set(false);
                    System.out.println("[[cancellation failed]]");
                })
                .onError(e -> {
                    cancellationSucceeded.set(false);
                    System.out.println("[[cancellation failed]]");
                })
                .execute();

        AtomicBoolean streamingCancelled = new AtomicBoolean(false);

        newSingleThreadExecutor().execute(() -> {
            responseHandle.cancel();
            streamingCancelled.set(true);
            System.out.println("[[streaming cancelled]]");
        });

        while (!streamingCancelled.get()) {
            Thread.sleep(10);
        }
        Thread.sleep(2000);

        assertThat(cancellationSucceeded).isTrue();
    }
}
