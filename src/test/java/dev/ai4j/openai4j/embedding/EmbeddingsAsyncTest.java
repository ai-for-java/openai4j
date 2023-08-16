package dev.ai4j.openai4j.embedding;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddingsAsyncTest extends RateLimitAwareTest {

    private static final String INPUT = "hello";

    private final OpenAiClient client = OpenAiClient.builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() throws Exception {

        CompletableFuture<List<Float>> future = new CompletableFuture<>();


        client.embedding(INPUT)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        List<Float> response = future.get(30, SECONDS);

        assertThat(response).hasSize(1536);
    }

    @Test
    void testCustomizableApi() throws Exception {

        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(INPUT)
                .build();

        CompletableFuture<EmbeddingResponse> future = new CompletableFuture<>();


        client.embedding(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        EmbeddingResponse response = future.get(30, SECONDS);

        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).embedding()).hasSize(1536);

        assertThat(response.embedding()).hasSize(1536);
    }
}
