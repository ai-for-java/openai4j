package dev.ai4j.openai4j;

import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import dev.ai4j.openai4j.moderation.ModerationRequest;
import dev.ai4j.openai4j.moderation.ModerationResponse;
import dev.ai4j.openai4j.moderation.ModerationResult;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.List;

import static dev.ai4j.openai4j.Json.GSON;

public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final String baseUrl;
    private final String apiVersion;
    private final OkHttpClient okHttpClient;
    private final OpenAiApi openAiApi;
    private final boolean logStreamingResponses;

    public OpenAiClient(String apiKey) {
        this(builder().openAiApiKey(apiKey));
    }

    private OpenAiClient(Builder serviceBuilder) {

        this.baseUrl = serviceBuilder.baseUrl;
        this.apiVersion = serviceBuilder.apiVersion;

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .callTimeout(serviceBuilder.callTimeout)
                .connectTimeout(serviceBuilder.connectTimeout)
                .readTimeout(serviceBuilder.readTimeout)
                .writeTimeout(serviceBuilder.writeTimeout);

        if (serviceBuilder.openAiApiKey == null && serviceBuilder.azureApiKey == null) {
            throw new IllegalArgumentException("openAiApiKey OR azureApiKey must be defined");
        }
        if (serviceBuilder.openAiApiKey != null && serviceBuilder.azureApiKey != null) {
            throw new IllegalArgumentException("openAiApiKey AND azureApiKey cannot both be defined at the same time");
        }
        if (serviceBuilder.openAiApiKey != null) {
            okHttpClientBuilder.addInterceptor(new AuthorizationHeaderInjector(serviceBuilder.openAiApiKey));
        } else {
            okHttpClientBuilder.addInterceptor(new ApiKeyHeaderInjector(serviceBuilder.azureApiKey));
        }

        if (serviceBuilder.proxy != null) {
            okHttpClientBuilder.proxy(serviceBuilder.proxy);
        }

        if (serviceBuilder.logRequests) {
            okHttpClientBuilder.addInterceptor(new RequestLoggingInterceptor());
        }
        if (serviceBuilder.logResponses) {
            okHttpClientBuilder.addInterceptor(new ResponseLoggingInterceptor());
        }
        this.logStreamingResponses = serviceBuilder.logStreamingResponses;

        this.okHttpClient = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serviceBuilder.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .build();

        this.openAiApi = retrofit.create(OpenAiApi.class);
    }

    public void shutdown() {

        okHttpClient.dispatcher().executorService().shutdown();

        okHttpClient.connectionPool().evictAll();

        Cache cache = okHttpClient.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                log.error("Failed to close cache", e);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String baseUrl = "https://api.openai.com/v1/";
        private String apiVersion;
        private String openAiApiKey;
        private String azureApiKey;
        private Duration callTimeout = Duration.ofSeconds(60);
        private Duration connectTimeout = Duration.ofSeconds(60);
        private Duration readTimeout = Duration.ofSeconds(60);
        private Duration writeTimeout = Duration.ofSeconds(60);
        private Proxy proxy;
        private boolean logRequests;
        private boolean logResponses;
        private boolean logStreamingResponses;

        private Builder() {
        }

        /**
         * @param baseUrl Base URL of OpenAI API.
         *                For OpenAI (default): "https://api.openai.com/v1/"
         *                For Azure OpenAI: "https://{resource-name}.openai.azure.com/openai/deployments/{deployment-id}/"
         * @return builder
         */
        public Builder baseUrl(String baseUrl) {
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("baseUrl cannot be null or empty");
            }
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
            return this;
        }

        /**
         * @param apiVersion Version of the API in the YYYY-MM-DD format. Applicable only for Azure OpenAI.
         * @return builder
         */
        public Builder apiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        /**
         * @param openAiApiKey OpenAI API key.
         *                     Will be injected in HTTP headers like this: "Authorization: Bearer ${openAiApiKey}"
         * @return builder
         */
        public Builder openAiApiKey(String openAiApiKey) {
            if (openAiApiKey == null || openAiApiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("openAiApiKey cannot be null or empty. API keys can be generated here: https://platform.openai.com/account/api-keys");
            }
            this.openAiApiKey = openAiApiKey;
            return this;
        }

        /**
         * @param azureApiKey Azure API key.
         *                    Will be injected in HTTP headers like this: "api-key: ${azureApiKey}"
         * @return builder
         */
        public Builder azureApiKey(String azureApiKey) {
            if (azureApiKey == null || azureApiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("azureApiKey cannot be null or empty");
            }
            this.azureApiKey = azureApiKey;
            return this;
        }

        public Builder callTimeout(Duration callTimeout) {
            if (callTimeout == null) {
                throw new IllegalArgumentException("callTimeout cannot be null");
            }
            this.callTimeout = callTimeout;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            if (connectTimeout == null) {
                throw new IllegalArgumentException("connectTimeout cannot be null");
            }
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(Duration readTimeout) {
            if (readTimeout == null) {
                throw new IllegalArgumentException("readTimeout cannot be null");
            }
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder writeTimeout(Duration writeTimeout) {
            if (writeTimeout == null) {
                throw new IllegalArgumentException("writeTimeout cannot be null");
            }
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder proxy(Proxy.Type type, String ip, int port) {
            this.proxy = new Proxy(type, new InetSocketAddress(ip, port));
            return this;
        }

        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder logRequests() {
            return logRequests(true);
        }

        public Builder logRequests(Boolean logRequests) {
            if (logRequests == null) {
                logRequests = false;
            }
            this.logRequests = logRequests;
            return this;
        }

        public Builder logResponses() {
            return logResponses(true);
        }

        public Builder logResponses(Boolean logResponses) {
            if (logResponses == null) {
                logResponses = false;
            }
            this.logResponses = logResponses;
            return this;
        }

        public Builder logStreamingResponses() {
            return logStreamingResponses(true);
        }

        public Builder logStreamingResponses(Boolean logStreamingResponses) {
            if (logStreamingResponses == null) {
                logStreamingResponses = false;
            }
            this.logStreamingResponses = logStreamingResponses;
            return this;
        }

        public OpenAiClient build() {
            return new OpenAiClient(this);
        }
    }

    public SyncOrAsyncOrStreaming<CompletionResponse> completion(CompletionRequest request) {

        CompletionRequest syncRequest = CompletionRequest.builder()
                .from(request)
                .stream(null)
                .build();

        return new RequestExecutor<>(
                openAiApi.completions(syncRequest, apiVersion),
                (r) -> r,
                okHttpClient,
                formatUrl("completions"),
                () -> CompletionRequest.builder().from(request).stream(true).build(),
                CompletionResponse.class,
                (r) -> r,
                logStreamingResponses
        );
    }

    @Experimental
    public SyncOrAsyncOrStreaming<String> completion(String prompt) {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .build();

        CompletionRequest syncRequest = CompletionRequest.builder()
                .from(request)
                .stream(null)
                .build();

        return new RequestExecutor<>(
                openAiApi.completions(syncRequest, apiVersion),
                CompletionResponse::text,
                okHttpClient,
                formatUrl("completions"),
                () -> CompletionRequest.builder().from(request).stream(true).build(),
                CompletionResponse.class,
                CompletionResponse::text,
                logStreamingResponses
        );
    }

    public SyncOrAsyncOrStreaming<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request) {

        ChatCompletionRequest syncRequest = ChatCompletionRequest.builder()
                .from(request)
                .stream(null)
                .build();

        return new RequestExecutor<>(
                openAiApi.chatCompletions(syncRequest, apiVersion),
                (r) -> r,
                okHttpClient,
                formatUrl("chat/completions"),
                () -> ChatCompletionRequest.builder().from(request).stream(true).build(),
                ChatCompletionResponse.class,
                (r) -> r,
                logStreamingResponses
        );
    }

    @Experimental
    public SyncOrAsyncOrStreaming<String> chatCompletion(String userMessage) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addUserMessage(userMessage)
                .build();

        ChatCompletionRequest syncRequest = ChatCompletionRequest.builder()
                .from(request)
                .stream(null)
                .build();

        return new RequestExecutor<>(
                openAiApi.chatCompletions(syncRequest, apiVersion),
                ChatCompletionResponse::content,
                okHttpClient,
                formatUrl("chat/completions"),
                () -> ChatCompletionRequest.builder().from(request).stream(true).build(),
                ChatCompletionResponse.class,
                (r) -> r.choices().get(0).delta().content(),
                logStreamingResponses
        );
    }

    public SyncOrAsync<EmbeddingResponse> embedding(EmbeddingRequest request) {

        return new RequestExecutor<>(openAiApi.embeddings(request, apiVersion), (r) -> r);
    }

    @Experimental
    public SyncOrAsync<List<Float>> embedding(String input) {

        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(input)
                .build();

        return new RequestExecutor<>(openAiApi.embeddings(request, apiVersion), EmbeddingResponse::embedding);
    }

    public SyncOrAsync<ModerationResponse> moderation(ModerationRequest request) {

        return new RequestExecutor<>(openAiApi.moderations(request, apiVersion), (r) -> r);
    }

    @Experimental
    public SyncOrAsync<ModerationResult> moderation(String input) {

        ModerationRequest request = ModerationRequest.builder()
                .input(input)
                .build();

        return new RequestExecutor<>(openAiApi.moderations(request, apiVersion), (r) -> r.results().get(0));
    }

    private String formatUrl(String endpoint) {
        return baseUrl + endpoint + apiVersionQueryParam();
    }

    private String apiVersionQueryParam() {
        if (apiVersion == null || apiVersion.trim().isEmpty()) {
            return "";
        }
        return "?api-version=" + apiVersion;
    }
}