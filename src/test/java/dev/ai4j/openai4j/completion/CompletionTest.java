package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionTest extends RateLimitAwareTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiClient client = OpenAiClient.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
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

    @Test
    void shouldVerifyStreamParameterBehavior() throws Exception {
        // Test with stream=true
        CompletionRequest streamingRequest = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(true)
                .model("gpt-3.5-turbo-instruct")
                .build();

        List<CompletionResponse> streamingChunks = new ArrayList<>();
        CompletableFuture<Void> streamingFuture = new CompletableFuture<>();

        client.completion(streamingRequest)
                .onPartialResponse(streamingChunks::add)
                .onComplete(() -> streamingFuture.complete(null))
                .onError(streamingFuture::completeExceptionally)
                .execute();

        // Wait for streaming to complete
        streamingFuture.get(30, TimeUnit.SECONDS);

        // Verify streaming response characteristics
        assertThat(streamingChunks.size()).isGreaterThan(1); // Multiple chunks indicate streaming

        // Test with stream=false
        CompletionRequest nonStreamingRequest = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(false) // Explicitly set to false
                .model("gpt-3.5-turbo-instruct")
                .build();

        // Non-streaming should return a single, complete response
        CompletionResponse nonStreamingResponse = client.completion(nonStreamingRequest).execute();

        // Verify non-streaming response characteristics
        assertThat(nonStreamingResponse.choices())
                .hasSize(1); // Single, complete response
        assertThat(nonStreamingResponse.text())
                .isNotEmpty()
                .containsIgnoringCase("hello world");

        // Compare responses to verify different behavior
        String streamedContent = streamingChunks.stream()
                .map(CompletionResponse::text)
                .collect(Collectors.joining());

        // Both should contain valid content despite different delivery methods
        assertThat(streamedContent).containsIgnoringCase("hello world");
        assertThat(streamedContent).isNotEqualTo(nonStreamingResponse.text()); // Raw responses should differ due to streaming chunks
    }
}
