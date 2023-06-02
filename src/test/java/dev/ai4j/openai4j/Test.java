package dev.ai4j.openai4j;

import dev.ai4j.openai4j.completion.CompletionRequest;

public class Test {

    public static void main(String[] args) {

        String apiKey = System.getenv("OPENAI_API_KEY");

        OpenAiClient client = OpenAiClient.builder()
                .apiKey(apiKey)
                .logRequests()
                .logResponses()
                .logStreamingResponses()
                .build();

        CompletionRequest request = CompletionRequest.builder().maxTokens(5000).build();

        client.completion(request)
                .onPartialResponse(System.out::println)
                .onComplete(() -> System.out.println("done"))
                .onError(Throwable::printStackTrace)
                .execute();

        client.shutdown();
    }
}