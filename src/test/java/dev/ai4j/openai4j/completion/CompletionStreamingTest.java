package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class CompletionStreamingTest extends RateLimitAwareTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
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
}
