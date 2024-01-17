package dev.ai4j.openai4j.completion;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

public class RequestDisabledCompletionTest {
    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {
        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.completion(PROMPT).execute())
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void testCustomizableApi() {
        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();

        assertThatExceptionOfType(OpenAiHttpException.class)
            .isThrownBy(() -> client.completion(request).execute())
            .withMessage("Requests to the live system are disabled");
    }
}
