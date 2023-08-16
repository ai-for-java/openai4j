package dev.ai4j.openai4j.moderation;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static dev.ai4j.openai4j.moderation.ModerationTest.assertAllFields;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class ModerationAsyncTest extends RateLimitAwareTest {

    private static final String INPUT = "hello";

    private final OpenAiClient client = OpenAiClient.builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() throws Exception {

        CompletableFuture<ModerationResult> future = new CompletableFuture<>();


        client.moderation(INPUT)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        ModerationResult response = future.get(30, SECONDS);

        assertAllFields(response);
    }

    @Test
    void testCustomizableApi() throws Exception {

        ModerationRequest request = ModerationRequest.builder()
                .input(INPUT)
                .build();

        CompletableFuture<ModerationResponse> future = new CompletableFuture<>();


        client.moderation(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        ModerationResponse response = future.get(30, SECONDS);

        assertThat(response.results()).isNotEmpty();
        assertThat(response.results()).hasSize(1);

        ModerationResult moderationResult = response.results().get(0);

        assertAllFields(moderationResult);
    }
}
