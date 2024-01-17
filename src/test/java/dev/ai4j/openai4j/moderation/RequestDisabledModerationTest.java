package dev.ai4j.openai4j.moderation;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

public class RequestDisabledModerationTest {

    private static final String INPUT = "hello";

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {
        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.moderation(INPUT).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void testCustomizableApi() {

        ModerationRequest request = ModerationRequest.builder()
                .input(INPUT)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.moderation(request).execute())
            .withMessage("Requests to the live system are disabled");
    }
}
