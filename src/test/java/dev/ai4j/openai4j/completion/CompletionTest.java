package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionTest extends RateLimitAwareTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .openAiApiKey(System.getenv("OPENAI_API_KEY"))
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {

        String response = client.completion(PROMPT).execute();

        assertThat(response).containsIgnoringCase("hello world");
    }

    @Test
    void testCustomizableApi() {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();


        CompletionResponse response = client.completion(request).execute();


        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).text()).containsIgnoringCase("hello world");

        assertThat(response.text()).containsIgnoringCase("hello world");
    }
}
