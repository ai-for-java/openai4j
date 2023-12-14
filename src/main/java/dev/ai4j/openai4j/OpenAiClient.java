package dev.ai4j.openai4j;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.List;

import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import dev.ai4j.openai4j.moderation.ModerationRequest;
import dev.ai4j.openai4j.moderation.ModerationResponse;
import dev.ai4j.openai4j.moderation.ModerationResult;
import dev.ai4j.openai4j.spi.OpenAiClientBuilderFactory;
import dev.ai4j.openai4j.spi.ServiceHelper;

public abstract class OpenAiClient {

    public abstract SyncOrAsyncOrStreaming<CompletionResponse> completion(CompletionRequest request);

    public abstract SyncOrAsyncOrStreaming<String> completion(String prompt);

    public abstract SyncOrAsyncOrStreaming<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request);

    public abstract SyncOrAsyncOrStreaming<String> chatCompletion(String userMessage);

    public abstract SyncOrAsync<EmbeddingResponse> embedding(EmbeddingRequest request);

    public abstract SyncOrAsync<List<Float>> embedding(String input);

    public abstract SyncOrAsync<ModerationResponse> moderation(ModerationRequest request);

    public abstract SyncOrAsync<ModerationResult> moderation(String input);

    public abstract void shutdown();

    @SuppressWarnings("rawtypes")
    public static OpenAiClient.Builder builder() {
        for (OpenAiClientBuilderFactory factory : ServiceHelper.loadFactories(OpenAiClientBuilderFactory.class)) {
            return factory.get();
        }
        // fallback to the default
        return DefaultOpenAiClient.builder();
    }

    @SuppressWarnings("unchecked")
    public abstract static class Builder<T extends OpenAiClient, B extends Builder<T, B>> {

        public String baseUrl = "https://api.openai.com/v1/";
        public String organizationId;
        public String apiVersion;
        public String openAiApiKey;
        public String azureApiKey;
        public Duration callTimeout = Duration.ofSeconds(60);
        public Duration connectTimeout = Duration.ofSeconds(60);
        public Duration readTimeout = Duration.ofSeconds(60);
        public Duration writeTimeout = Duration.ofSeconds(60);
        public Proxy proxy;
        public boolean logRequests;
        public boolean logResponses;
        public boolean logStreamingResponses;

        public abstract T build();

        /**
         * @param baseUrl Base URL of OpenAI API.
         *                For OpenAI (default): "https://api.openai.com/v1/"
         *                For Azure OpenAI: "https://{resource-name}.openai.azure.com/openai/deployments/{deployment-id}/"
         * @return builder
         */
        public B baseUrl(String baseUrl) {
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("baseUrl cannot be null or empty");
            }
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
            return (B) this;
        }

        /**
         *
         * @param organizationId The organizationId for OpenAI: https://platform.openai.com/docs/api-reference/organization-optional
         * @return builder
         */
        public B organizationId(String organizationId) {
            this.organizationId = organizationId;
            return (B) this;
        }

        /**
         * @param apiVersion Version of the API in the YYYY-MM-DD format. Applicable only for Azure OpenAI.
         * @return builder
         */
        public B apiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return (B) this;
        }

        /**
         * @param openAiApiKey OpenAI API key.
         *                     Will be injected in HTTP headers like this: "Authorization: Bearer ${openAiApiKey}"
         * @return builder
         */
        public B openAiApiKey(String openAiApiKey) {
            if (openAiApiKey == null || openAiApiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("openAiApiKey cannot be null or empty. API keys can be generated here: https://platform.openai.com/account/api-keys");
            }
            this.openAiApiKey = openAiApiKey;
            return (B) this;
        }

        /**
         * @param azureApiKey Azure API key.
         *                    Will be injected in HTTP headers like this: "api-key: ${azureApiKey}"
         * @return builder
         */
        public B azureApiKey(String azureApiKey) {
            if (azureApiKey == null || azureApiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("azureApiKey cannot be null or empty");
            }
            this.azureApiKey = azureApiKey;
            return (B) this;
        }

        public B callTimeout(Duration callTimeout) {
            if (callTimeout == null) {
                throw new IllegalArgumentException("callTimeout cannot be null");
            }
            this.callTimeout = callTimeout;
            return (B) this;
        }

        public B connectTimeout(Duration connectTimeout) {
            if (connectTimeout == null) {
                throw new IllegalArgumentException("connectTimeout cannot be null");
            }
            this.connectTimeout = connectTimeout;
            return (B) this;
        }

        public B readTimeout(Duration readTimeout) {
            if (readTimeout == null) {
                throw new IllegalArgumentException("readTimeout cannot be null");
            }
            this.readTimeout = readTimeout;
            return (B) this;
        }

        public B writeTimeout(Duration writeTimeout) {
            if (writeTimeout == null) {
                throw new IllegalArgumentException("writeTimeout cannot be null");
            }
            this.writeTimeout = writeTimeout;
            return (B) this;
        }

        public B proxy(Proxy.Type type, String ip, int port) {
            this.proxy = new Proxy(type, new InetSocketAddress(ip, port));
            return (B) this;
        }

        public B proxy(Proxy proxy) {
            this.proxy = proxy;
            return (B) this;
        }

        public B logRequests() {
            return logRequests(true);
        }

        public B logRequests(Boolean logRequests) {
            if (logRequests == null) {
                logRequests = false;
            }
            this.logRequests = logRequests;
            return (B) this;
        }

        public B logResponses() {
            return logResponses(true);
        }

        public B logResponses(Boolean logResponses) {
            if (logResponses == null) {
                logResponses = false;
            }
            this.logResponses = logResponses;
            return (B) this;
        }

        public B logStreamingResponses() {
            return logStreamingResponses(true);
        }

        public B logStreamingResponses(Boolean logStreamingResponses) {
            if (logStreamingResponses == null) {
                logStreamingResponses = false;
            }
            this.logStreamingResponses = logStreamingResponses;
            return (B) this;
        }
    }
}
