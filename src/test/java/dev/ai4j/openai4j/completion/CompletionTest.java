package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiService;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionTest extends RateLimitAwareTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiService service = new OpenAiService(System.getenv("OPENAI_API_KEY"));

    @Test
    void testSimpleApi() {

        String response = service.getCompletion(PROMPT);

        assertThat(response).containsIgnoringCase("hello world");
    }

    @Test
    void testCustomizableApi() {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();


        CompletionResponse response = service.getCompletions(request);


        assertThat(response.choices()).hasSize(1);
        assertThat(response.choices().get(0).text()).containsIgnoringCase("hello world");

        assertThat(response.text()).containsIgnoringCase("hello world");
    }
}
