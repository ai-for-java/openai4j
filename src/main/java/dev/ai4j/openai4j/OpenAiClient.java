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

    private final String url;
    private final OkHttpClient okHttpClient;
    private final OpenAiApi openAiApi;
    private final boolean logStreamingResponses;

    public OpenAiClient(String apiKey) {
        this(builder().apiKey(apiKey));
    }

    private OpenAiClient(Builder serviceBuilder) {

        this.url = serviceBuilder.url;

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInsertingInterceptor(serviceBuilder.apiKey))
                .callTimeout(serviceBuilder.callTimeout)
                .connectTimeout(serviceBuilder.connectTimeout)
                .readTimeout(serviceBuilder.readTimeout)
                .writeTimeout(serviceBuilder.writeTimeout);

        if (serviceBuilder.proxy != null) {
            okHttpClientBuilder = okHttpClientBuilder.proxy(serviceBuilder.proxy);
        }

        if (serviceBuilder.logRequests) {
            okHttpClientBuilder = okHttpClientBuilder.addInterceptor(new RequestLoggingInterceptor());
        }
        if (serviceBuilder.logResponses) {
            okHttpClientBuilder = okHttpClientBuilder.addInterceptor(new ResponseLoggingInterceptor());
        }
        this.logStreamingResponses = serviceBuilder.logStreamingResponses;

        this.okHttpClient = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serviceBuilder.url)
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

        private String url = "https://api.openai.com/";
        private String apiKey;
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

        public Builder url(String url) {
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be null or empty");
            }
            this.url = url.endsWith("/") ? url : url + "/";
            return this;
        }

        public Builder apiKey(String apiKey) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API key cannot be null or empty. API keys can be generated here: https://platform.openai.com/account/api-keys");
            }
            this.apiKey = apiKey;
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
            if (proxy == null) {
                throw new IllegalArgumentException("proxy cannot be null");
            }
            this.proxy = proxy;
            return this;
        }

        public Builder logRequests() {
            return logRequests(true);
        }

        public Builder logRequests(boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public Builder logResponses() {
            return logResponses(true);
        }

        public Builder logResponses(boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        public Builder logStreamingResponses() {
            return logStreamingResponses(true);
        }

        public Builder logStreamingResponses(boolean logStreamingResponses) {
            this.logStreamingResponses = logStreamingResponses;
            return this;
        }

        public OpenAiClient build() {
            return new OpenAiClient(this);
        }
    }

    public SyncOrAsyncOrStreaming<CompletionResponse> completion(CompletionRequest request) {

        return new RequestExecutor<>(
                openAiApi.completions(CompletionRequest.builder().from(request).stream(null).build()),
                (r) -> r,
                okHttpClient,
                url + "v1/completions",
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

        return new RequestExecutor<>(
                openAiApi.completions(CompletionRequest.builder().from(request).stream(null).build()),
                CompletionResponse::text,
                okHttpClient,
                url + "v1/completions",
                () -> CompletionRequest.builder().from(request).stream(true).build(),
                CompletionResponse.class,
                CompletionResponse::text,
                logStreamingResponses
        );
    }

    public SyncOrAsyncOrStreaming<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request) {

        return new RequestExecutor<>(
                openAiApi.chatCompletions(ChatCompletionRequest.builder().from(request).stream(null).build()),
                (r) -> r,
                okHttpClient,
                url + "v1/chat/completions",
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

        return new RequestExecutor<>(
                openAiApi.chatCompletions(ChatCompletionRequest.builder().from(request).stream(null).build()),
                ChatCompletionResponse::content,
                okHttpClient,
                url + "v1/chat/completions",
                () -> ChatCompletionRequest.builder().from(request).stream(true).build(),
                ChatCompletionResponse.class,
                (r) -> r.choices().get(0).delta().content(),
                logStreamingResponses
        );
    }

    public SyncOrAsync<EmbeddingResponse> embedding(EmbeddingRequest request) {

        return new RequestExecutor<>(openAiApi.embeddings(request), (r) -> r);
    }

    @Experimental
    public SyncOrAsync<List<Float>> embedding(String input) {

        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(input)
                .build();

        return new RequestExecutor<>(openAiApi.embeddings(request), EmbeddingResponse::embedding);
    }

    public SyncOrAsync<ModerationResponse> moderation(ModerationRequest request) {

        return new RequestExecutor<>(openAiApi.moderations(request), (r) -> r);
    }

    @Experimental
    public SyncOrAsync<ModerationResult> moderation(String input) {

        ModerationRequest request = ModerationRequest.builder()
                .input(input)
                .build();

        return new RequestExecutor<>(openAiApi.moderations(request), (r) -> r.results().get(0));
    }
}