package dev.ai4j.openai4j;

import dev.ai4j.openai4j.chat.ChatCompletionRequest;

import static java.time.Duration.ofSeconds;

public class Test {

    public static void main(String[] args) {

        String apiKey = System.getenv("OPENAI_API_KEY");

        OpenAiClient client = OpenAiClient.builder()
                .openAiApiKey(apiKey)
//                .baseUrl("https://resource-name.openai.azure.com/openai/deployments/deployment-id/")
//                .apiVersion("2023-05-15")
//                .azureApiKey("...")
                .callTimeout(ofSeconds(60))
                .connectTimeout(ofSeconds(60))
                .readTimeout(ofSeconds(60))
                .writeTimeout(ofSeconds(60))
//                .proxy(HTTP, "103.154.230.129", 8080)
                .logRequests()
                .logResponses()
                .logStreamingResponses()
                .build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addUserMessage("hello")
                .build();

        System.out.println(client.chatCompletion(request).execute().content());
    }
}
