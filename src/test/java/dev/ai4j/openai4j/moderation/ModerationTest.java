package dev.ai4j.openai4j.moderation;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ModerationTest extends RateLimitAwareTest {

    private static final String INPUT = "hello";

    private final OpenAiClient client = OpenAiClient.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {

        ModerationResult moderationResult = client.moderation(INPUT).execute();


        assertAllFields(moderationResult);
    }

    @Test
    void testCustomizableApi() {

        ModerationRequest request = ModerationRequest.builder()
                .input(INPUT)
                .build();


        ModerationResponse response = client.moderation(request).execute();


        assertThat(response.results()).isNotEmpty();
        assertThat(response.results()).hasSize(1);

        ModerationResult moderationResult = response.results().get(0);

        assertAllFields(moderationResult);
    }

    static void assertAllFields(ModerationResult moderationResult) {
        assertThat(moderationResult.categories().hate()).isFalse();
        assertThat(moderationResult.categories().hateThreatening()).isFalse();
        assertThat(moderationResult.categories().selfHarm()).isFalse();
        assertThat(moderationResult.categories().sexual()).isFalse();
        assertThat(moderationResult.categories().sexualMinors()).isFalse();
        assertThat(moderationResult.categories().violence()).isFalse();
        assertThat(moderationResult.categories().violenceGraphic()).isFalse();

        assertThat(moderationResult.categoryScores().hate()).isNotNull();
        assertThat(moderationResult.categoryScores().hateThreatening()).isNotNull();
        assertThat(moderationResult.categoryScores().selfHarm()).isNotNull();
        assertThat(moderationResult.categoryScores().sexual()).isNotNull();
        assertThat(moderationResult.categoryScores().sexualMinors()).isNotNull();
        assertThat(moderationResult.categoryScores().violence()).isNotNull();
        assertThat(moderationResult.categoryScores().violenceGraphic()).isNotNull();
    }
}
