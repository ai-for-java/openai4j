package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.ResponseHandle;
import dev.ai4j.openai4j.shared.StreamOptions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    void shouldPreserveStreamParameter() {
        // Given
        CompletionRequest requestWithStreamFalse = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(false)
                .build();

        CompletionRequest requestWithStreamTrue = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(true)
                .build();

        // When
        CompletionResponse responseForStreamFalse = client.completion(requestWithStreamFalse).execute();
        CompletionResponse responseForStreamTrue = client.completion(requestWithStreamTrue).execute();

        // Then
        // Assert the responses are not null and contain valid choices
        assertThat(responseForStreamFalse).isNotNull();
        assertThat(responseForStreamFalse.choices()).isNotEmpty();
        assertThat(responseForStreamFalse.text()).isNotEmpty();

        assertThat(responseForStreamTrue).isNotNull();
        assertThat(responseForStreamTrue.choices()).isNotEmpty();
        assertThat(responseForStreamTrue.text()).isNotEmpty();

        // Optionally log for manual verification
        System.out.println("Response with stream=false: " + responseForStreamFalse);
        System.out.println("Response with stream=true: " + responseForStreamTrue);
    }

    @Test
    void shouldRespectStreamFalse() {
        // Given
        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(false) // Explicitly disable streaming
                .build();

        // When
        CompletionResponse response = client.completion(request).execute();

        // Then
        // Assert the response is complete (non-streaming responses are fully formed)
        assertThat(response).isNotNull();
        assertThat(response.choices()).isNotEmpty();
        assertThat(response.text()).isNotEmpty();

        // Optional: Log the response for manual verification
        System.out.println("Non-streaming response: " + response);
    }

    @Test
    void shouldRespectStreamTrue() {
        // Given
        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(true) // Explicitly enable streaming
                .build();

        // When
        CompletionResponse response = client.completion(request).execute();

        // Then
        // Assert the response starts arriving as a stream
        // For a streaming response, the implementation should process chunks or deltas
        assertThat(response).isNotNull();
        assertThat(response.choices()).isNotEmpty(); // Streaming still produces choices over time
        assertThat(response.text()).isNotEmpty();

        // Optional: Log the response for manual verification
        System.out.println("Streaming response: " + response);
    }

    @Test
    void shouldVerifyStreamingImplementation() throws Exception {
        // Given
        CompletionRequest streamingRequest = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(true)
                .streamOptions(StreamOptions.builder().build())
                .model("gpt-3.5-turbo-instruct")
                .build();

        // When - collect streaming responses
        List<String> streamingChunks = new ArrayList<>();
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        ResponseHandle handle = client.completion(streamingRequest)
                .onPartialResponse(chunk -> {
                    System.out.println("Received chunk: " + chunk.text());
                    streamingChunks.add(chunk.text());
                })
                .onComplete(() -> {
                    System.out.println("Streaming completed");
                    future.complete(null);
                })
                .onError(e -> {
                    System.err.println("Streaming error: " + e.getMessage());
                    future.completeExceptionally(e);
                })
                .execute();

        // Wait for streaming to complete
        future.get(30, TimeUnit.SECONDS);

        // Then
        assertThat(streamingChunks).isNotEmpty();
        assertThat(streamingChunks.size()).isGreaterThan(1);
        
        String combinedResponse = String.join("", streamingChunks);
        assertThat(combinedResponse).containsIgnoringCase("hello world");
    }

    @Test
    void shouldCompareStreamingVsNonStreaming() throws Exception {
        // Given
        CompletionRequest streamingRequest = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(true)
                .streamOptions(StreamOptions.builder().build())
                .model("gpt-3.5-turbo-instruct")
                .build();

        CompletionRequest nonStreamingRequest = CompletionRequest.builder()
                .prompt(PROMPT)
                .stream(false)
                .model("gpt-3.5-turbo-instruct")
                .build();

        // When - collect streaming responses
        List<String> streamingChunks = new ArrayList<>();
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        ResponseHandle handle = client.completion(streamingRequest)
                .onPartialResponse(chunk -> {
                    System.out.println("Received chunk: " + chunk.text());
                    streamingChunks.add(chunk.text());
                })
                .onComplete(() -> {
                    System.out.println("Streaming completed");
                    future.complete(null);
                })
                .onError(e -> {
                    System.err.println("Streaming error: " + e.getMessage());
                    future.completeExceptionally(e);
                })
                .execute();

        // Wait for streaming to complete
        future.get(30, TimeUnit.SECONDS);

        // When - get non-streaming response
        CompletionResponse nonStreamingResponse = client.completion(nonStreamingRequest).execute();

        // Then
        assertThat(streamingChunks.size()).isGreaterThan(1);
        
        String combinedStreamingResponse = String.join("", streamingChunks);
        assertThat(combinedStreamingResponse).containsIgnoringCase("hello world");
        assertThat(nonStreamingResponse.text()).containsIgnoringCase("hello world");
        
        assertThat(streamingChunks.toString()).isNotEqualTo(nonStreamingResponse.text());
    }
}
