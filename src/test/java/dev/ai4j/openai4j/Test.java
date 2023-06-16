package dev.ai4j.openai4j;

import dev.ai4j.openai4j.completion.CompletionRequest;

import static java.net.Proxy.Type.HTTP;
import static java.time.Duration.ofSeconds;

public class Test {

    public static void main(String[] args) {

        String apiKey = System.getenv("OPENAI_API_KEY");

        OpenAiClient client = OpenAiClient.builder()
                .apiKey(apiKey)
                .callTimeout(ofSeconds(60))
                .connectTimeout(ofSeconds(60))
                .readTimeout(ofSeconds(60))
                .writeTimeout(ofSeconds(60))
                .proxy(HTTP, "103.154.230.129", 8080)
                .logRequests()
                .logResponses()
                .logStreamingResponses()
                .build();

        CompletionRequest request = CompletionRequest.builder()
                .prompt("hello")
                .build();

        System.out.println(client.completion(request).execute().text());
    }
}