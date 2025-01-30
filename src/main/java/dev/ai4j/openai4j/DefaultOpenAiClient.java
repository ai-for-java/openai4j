package dev.ai4j.openai4j;

import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import dev.ai4j.openai4j.image.GenerateImagesRequest;
import dev.ai4j.openai4j.image.GenerateImagesResponse;
import dev.ai4j.openai4j.moderation.ModerationRequest;
import dev.ai4j.openai4j.moderation.ModerationResponse;
import dev.ai4j.openai4j.moderation.ModerationResult;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultOpenAiClient extends OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultOpenAiClient.class);

    private final String baseUrl;
    private final String apiVersion;
    private final OkHttpClient okHttpClient;
    private final OpenAiApi openAiApi;
    private final boolean logStreamingResponses;

    public DefaultOpenAiClient(String apiKey) {
        this(new Builder().openAiApiKey(apiKey));
    }

    private DefaultOpenAiClient(Builder serviceBuilder) {
        this.baseUrl = serviceBuilder.baseUrl;
        this.apiVersion = serviceBuilder.apiVersion;

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .callTimeout(serviceBuilder.callTimeout)
                .connectTimeout(serviceBuilder.connectTimeout)
                .readTimeout(serviceBuilder.readTimeout)
                .writeTimeout(serviceBuilder.writeTimeout);

        if (serviceBuilder.dispatcher != null) {
            okHttpClientBuilder.dispatcher(serviceBuilder.dispatcher);
        }

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

        Map<String, String> headers = new HashMap<>();
        if (serviceBuilder.organizationId != null) {
            headers.put("OpenAI-Organization", serviceBuilder.organizationId);
        }
        if (serviceBuilder.userAgent != null) {
            headers.put("User-Agent", serviceBuilder.userAgent);
        }
        if (serviceBuilder.customHeaders != null) {
            headers.putAll(serviceBuilder.customHeaders);
        }
        if (!headers.isEmpty()) {
            okHttpClientBuilder.addInterceptor(new GenericHeaderInjector(headers));
        }

        if (serviceBuilder.proxy != null) {
            okHttpClientBuilder.proxy(serviceBuilder.proxy);
        }

        if (serviceBuilder.logRequests) {
            okHttpClientBuilder.addInterceptor(new RequestLoggingInterceptor(serviceBuilder.logLevel));
        }

        if (serviceBuilder.logResponses) {
            okHttpClientBuilder.addInterceptor(new ResponseLoggingInterceptor(serviceBuilder.logLevel));
        }
        this.logStreamingResponses = serviceBuilder.logStreamingResponses;

        this.okHttpClient = okHttpClientBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(serviceBuilder.baseUrl).client(okHttpClient);

        if (serviceBuilder.persistTo != null) {
            retrofitBuilder.addConverterFactory(new PersistorConverterFactory(serviceBuilder.persistTo));
        }

        retrofitBuilder.addConverterFactory(JacksonConverterFactory.create(Json.OBJECT_MAPPER));

        this.openAiApi = retrofitBuilder.build().create(OpenAiApi.class);
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

    public static class Builder extends OpenAiClient.Builder<DefaultOpenAiClient, Builder> {

        public DefaultOpenAiClient build() {
            return new DefaultOpenAiClient(this);
        }
    }

    @Override
    public SyncOrAsyncOrStreaming<CompletionResponse> completion(OpenAiClientContext context,
        CompletionRequest request) {
        CompletionRequest syncRequest = CompletionRequest.builder().from(request).stream(false).build();

        return new RequestExecutor<>(
                openAiApi.completions(context.headers(), syncRequest, apiVersion),
                r -> r,
                okHttpClient,
                formatUrl("completions"),
                () -> CompletionRequest.builder().from(request).stream(true).build(),
                CompletionResponse.class,
                r -> r,
                logStreamingResponses
        );
    }

    @Override
    public SyncOrAsyncOrStreaming<String> completion(OpenAiClientContext context, String prompt) {
        CompletionRequest request = CompletionRequest.builder().prompt(prompt).build();

        CompletionRequest syncRequest = CompletionRequest.builder().from(request).stream(false).build();

        return new RequestExecutor<>(
                openAiApi.completions(context.headers(), syncRequest, apiVersion),
                CompletionResponse::text,
                okHttpClient,
                formatUrl("completions"),
                () -> CompletionRequest.builder().from(request).stream(true).build(),
                CompletionResponse.class,
                CompletionResponse::text,
                logStreamingResponses
        );
    }

    @Override
    public SyncOrAsyncOrStreaming<ChatCompletionResponse> chatCompletion(OpenAiClientContext context,
        ChatCompletionRequest request) {
        ChatCompletionRequest syncRequest = ChatCompletionRequest.builder().from(request).stream(false).build();

        return new RequestExecutor<>(
                openAiApi.chatCompletions(context.headers(), syncRequest, apiVersion),
                r -> r,
                okHttpClient,
                formatUrl("chat/completions"),
                () -> ChatCompletionRequest.builder().from(request).stream(true).build(),
                ChatCompletionResponse.class,
                r -> r,
                logStreamingResponses
        );
    }

    @Override
    public SyncOrAsyncOrStreaming<String> chatCompletion(OpenAiClientContext context, String userMessage) {
        ChatCompletionRequest request = ChatCompletionRequest.builder().addUserMessage(userMessage).build();

        ChatCompletionRequest syncRequest = ChatCompletionRequest.builder().from(request).stream(false).build();

        return new RequestExecutor<>(
                openAiApi.chatCompletions(context.headers(), syncRequest, apiVersion),
                ChatCompletionResponse::content,
                okHttpClient,
                formatUrl("chat/completions"),
                () -> ChatCompletionRequest.builder().from(request).stream(true).build(),
                ChatCompletionResponse.class,
                r -> r.choices().get(0).delta().content(),
                logStreamingResponses
        );
    }

    @Override
    public SyncOrAsync<EmbeddingResponse> embedding(OpenAiClientContext context, EmbeddingRequest request) {
        return new RequestExecutor<>(openAiApi.embeddings(context.headers(), request, apiVersion), r -> r);
    }

    @Override
    public SyncOrAsync<List<Float>> embedding(OpenAiClientContext context, String input) {
        EmbeddingRequest request = EmbeddingRequest.builder().input(input).build();

        return new RequestExecutor<>(openAiApi.embeddings(context.headers(), request, apiVersion),
            EmbeddingResponse::embedding);
    }

    @Override
    public SyncOrAsync<ModerationResponse> moderation(OpenAiClientContext context,
        ModerationRequest request) {
        return new RequestExecutor<>(openAiApi.moderations(context.headers(), request, apiVersion),
            r -> r);
    }

    @Override
    public SyncOrAsync<ModerationResult> moderation(OpenAiClientContext context, String input) {
        ModerationRequest request = ModerationRequest.builder().input(input).build();

        return new RequestExecutor<>(openAiApi.moderations(context.headers(), request, apiVersion),
            r -> r.results().get(0));
    }

    @Override
    public SyncOrAsync<GenerateImagesResponse> imagesGeneration(OpenAiClientContext context,
        GenerateImagesRequest request) {
        return new RequestExecutor<>(openAiApi.imagesGenerations(context.headers(), request, apiVersion),
            r -> r);
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
