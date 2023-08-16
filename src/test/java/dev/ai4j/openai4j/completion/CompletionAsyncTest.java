package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class CompletionAsyncTest extends RateLimitAwareTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() throws Exception {

        CompletableFuture<String> future = new CompletableFuture<>();


        client.completion(PROMPT)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        String response = future.get(30, SECONDS);

        assertThat(response).containsIgnoringCase("hello world");
    }

    @Test
    void testCustomizableApi() throws Exception {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();

        CompletableFuture<CompletionResponse> future = new CompletableFuture<>();


        client.completion(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        CompletionResponse response = future.get(30, SECONDS);

        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).text()).containsIgnoringCase("hello world");

        assertThat(response.text()).containsIgnoringCase("hello world");
    }
}
