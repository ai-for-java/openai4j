package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.OpenAiService;
import dev.ai4j.openai4j.RateLimitAwareTest;
import dev.ai4j.openai4j.StreamingResponseHandler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class CompletionStreamingTest extends RateLimitAwareTest {

    private static final String PROMPT = "write exactly the following 2 words: 'hello world'";

    private final OpenAiService service = new OpenAiService(System.getenv("OPENAI_API_KEY"));

    @Test
    void testWithBuilder() throws ExecutionException, InterruptedException, TimeoutException {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(PROMPT)
                .build();

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> partialResponseFuture = new CompletableFuture<>();
        CompletableFuture<String> completeResponseFuture = new CompletableFuture<>();


        service.streamCompletions(request, new StreamingResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                responseBuilder.append(partialResponse);
            }

            @Override
            public void onCompleteResponse(String completeResponse) {
                partialResponseFuture.complete(responseBuilder.toString());
                completeResponseFuture.complete(completeResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                partialResponseFuture.completeExceptionally(t);
            }
        });


        String completeResponseBuiltFromPartialResponses = partialResponseFuture.get(30, SECONDS);
        assertThat(completeResponseBuiltFromPartialResponses).containsIgnoringCase("hello world");

        String providedCompleteResponse = completeResponseFuture.get(30, SECONDS);
        assertThat(providedCompleteResponse).isEqualTo(completeResponseBuiltFromPartialResponses);
    }

    @Test
    void testWithPrompt() throws ExecutionException, InterruptedException, TimeoutException {

        StringBuilder responseBuilder = new StringBuilder();
        CompletableFuture<String> partialResponseFuture = new CompletableFuture<>();
        CompletableFuture<String> completeResponseFuture = new CompletableFuture<>();


        service.streamCompletion(PROMPT, new StreamingResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                responseBuilder.append(partialResponse);
            }

            @Override
            public void onCompleteResponse(String completeResponse) {
                partialResponseFuture.complete(responseBuilder.toString());
                completeResponseFuture.complete(completeResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                partialResponseFuture.completeExceptionally(t);
            }
        });


        String completeResponseBuiltFromPartialResponses = partialResponseFuture.get(30, SECONDS);
        assertThat(completeResponseBuiltFromPartialResponses).containsIgnoringCase("hello world");

        String providedCompleteResponse = completeResponseFuture.get(30, SECONDS);
        assertThat(providedCompleteResponse).isEqualTo(completeResponseBuiltFromPartialResponses);
    }
}
