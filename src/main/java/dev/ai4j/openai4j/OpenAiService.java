package dev.ai4j.openai4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.openai4j.completion.CompletionResponse;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
import dev.ai4j.openai4j.embedding.EmbeddingResponse;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class OpenAiService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);

    private final String url;
    private final OkHttpClient okHttpClient;
    private final OpenAiApi openAiApi;

    public OpenAiService(String apiKey) {
        this(builder().apiKey(apiKey));
    }

    private OpenAiService(Builder builder) {

        this.url = builder.url;

        this.okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInsertingInterceptor(builder.apiKey))
                .addInterceptor(new RequestLoggingInterceptor())
                .addInterceptor(new ResponseLoggingInterceptor())
                .callTimeout(builder.timeout)
                .build();

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(builder.url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        this.openAiApi = retrofit.create(OpenAiApi.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Builder() {
        }

        private String url = "https://api.openai.com/";
        private String apiKey;
        private Duration timeout = Duration.ofSeconds(60);

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

        public Builder timeout(Duration timeout) {
            if (timeout == null) {
                throw new IllegalArgumentException("Timeout cannot be null");
            }
            this.timeout = timeout;
            return this;
        }

        public OpenAiService build() {
            return new OpenAiService(this);
        }
    }


    // completion

    public CompletionResponse getCompletions(CompletionRequest request) {
        if (request.stream() != null && request.stream()) {
            throw new IllegalArgumentException("Request parameter 'stream' with value 'true' is not compatible with getCompletion(...) method. If you need streaming, use one of streamCompletion(...) methods. If you do NOT need streaming, do not set 'stream' parameter in CompletionRequest, or set it to 'false'.");
        }

        return execute(openAiApi.completion(request));
    }

    @Experimental
    public String getCompletion(String prompt) {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .build();

        CompletionResponse response = getCompletions(request);

        return response.text();
    }

    @Experimental
    public OnResponseStep<CompletionResponse> getCompletionsAsync(CompletionRequest request) {

        return new OnResponseStep<CompletionResponse>() {

            @Override
            public OnFailureStep onResponse(Consumer<CompletionResponse> onResponse) {
                return new OnFailureStep() {

                    @Override
                    public ExecutionStep onFailure(Consumer<Throwable> onFailure) {
                        return new ExecutionStep() {

                            @Override
                            public void execute() {
                                getCompletionsAsync(request, new ResponseHandler<CompletionResponse>() {

                                    @Override
                                    public void onResponse(CompletionResponse response) {
                                        onResponse.accept(response);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        onFailure.accept(t);
                                    }
                                });
                            }
                        };
                    }
                };
            }
        };
    }

    @Experimental
    public OnResponseStep<String> getCompletionAsync(String prompt) {

        return new OnResponseStep<String>() {

            @Override
            public OnFailureStep onResponse(Consumer<String> onResponse) {
                return new OnFailureStep() {

                    @Override
                    public ExecutionStep onFailure(Consumer<Throwable> onFailure) {
                        return new ExecutionStep() {

                            @Override
                            public void execute() {
                                getCompletionAsync(prompt, new ResponseHandler<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        onResponse.accept(response);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        onFailure.accept(t);
                                    }
                                });
                            }
                        };
                    }
                };
            }
        };
    }

    @Experimental
    public void getCompletionsAsync(CompletionRequest request, ResponseHandler<CompletionResponse> handler) {

        if (request.stream() != null && request.stream()) {
            throw new IllegalArgumentException("Request parameter 'stream' with value 'true' is not compatible with getCompletionAsync(...) method. If you need streaming, use one of streamCompletion(...) methods. If you do NOT need streaming, do not set 'stream' parameter in CompletionRequest, or set it to 'false'.");
        }

        openAiApi.completion(request)
                .enqueue(new Callback<CompletionResponse>() {

                    @Override
                    public void onResponse(Call<CompletionResponse> call, retrofit2.Response<CompletionResponse> response) {
                        if (response.isSuccessful()) {
                            handler.onResponse(response.body());
                        } else {
                            try {
                                handler.onFailure(new RuntimeException(response.errorBody().string()));
                            } catch (IOException e) {
                                handler.onFailure(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CompletionResponse> call, Throwable t) {
                        handler.onFailure(t);
                    }
                });
    }

    @Experimental
    public void getCompletionAsync(String prompt, ResponseHandler<String> handler) {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .build();

        getCompletionsAsync(request, new ResponseHandler<CompletionResponse>() {

            @Override
            public void onResponse(CompletionResponse response) {
                handler.onResponse(response.text());
            }

            @Override
            public void onFailure(Throwable t) {
                handler.onFailure(t);
            }
        });
    }

    @Experimental
    public void streamCompletions(CompletionRequest request, StreamingResponseHandler handler) {

        if (request.stream() != null && !request.stream()) {
            throw new IllegalArgumentException("Request parameter 'stream' with value 'false' is not compatible with streamCompletion(...) method. If you do not need streaming, use one of getCompletion(...) or getCompletionAsync(...) methods. If you need streaming, do not set 'stream' parameter in CompletionRequest, or set it to 'true'.");
        }

        stream(
                CompletionRequest.builder()
                        .from(request)
                        .stream(true)
                        .build(),
                "v1/completions",
                CompletionResponse.class,
                CompletionResponse::text,
                handler
        );
    }

    @Experimental
    public void streamCompletion(String prompt, StreamingResponseHandler handler) {

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .build();

        streamCompletions(request, handler);
    }


    // chat completion

    public ChatCompletionResponse getChatCompletions(ChatCompletionRequest request) {
        if (request.stream() != null && request.stream()) {
            throw new IllegalArgumentException("Request parameter 'stream' with value 'true' is not compatible with getChatCompletion(...) method. If you need streaming, use one of streamChatCompletion(...) methods. If you do NOT need streaming, do not set 'stream' parameter in ChatCompletionRequest, or set it to 'false'.");
        }

        return execute(openAiApi.chatCompletion(request));
    }

    @Experimental
    public String getChatCompletion(String userMessage) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addUserMessage(userMessage)
                .build();

        ChatCompletionResponse response = getChatCompletions(request);

        return response.content();
    }

    @Experimental
    public OnResponseStep<ChatCompletionResponse> getChatCompletionsAsync(ChatCompletionRequest request) {

        return new OnResponseStep<ChatCompletionResponse>() {

            @Override
            public OnFailureStep onResponse(Consumer<ChatCompletionResponse> onResponse) {
                return new OnFailureStep() {

                    @Override
                    public ExecutionStep onFailure(Consumer<Throwable> onFailure) {
                        return new ExecutionStep() {

                            @Override
                            public void execute() {
                                getChatCompletionsAsync(request, new ResponseHandler<ChatCompletionResponse>() {

                                    @Override
                                    public void onResponse(ChatCompletionResponse response) {
                                        onResponse.accept(response);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        onFailure.accept(t);
                                    }
                                });
                            }
                        };
                    }
                };
            }
        };
    }

    @Experimental
    public OnResponseStep<String> getChatCompletionAsync(String userMessage) {

        return new OnResponseStep<String>() {

            @Override
            public OnFailureStep onResponse(Consumer<String> onResponse) {
                return new OnFailureStep() {

                    @Override
                    public ExecutionStep onFailure(Consumer<Throwable> onFailure) {
                        return new ExecutionStep() {

                            @Override
                            public void execute() {
                                getChatCompletionAsync(userMessage, new ResponseHandler<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        onResponse.accept(response);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        onFailure.accept(t);
                                    }
                                });
                            }
                        };
                    }
                };
            }
        };
    }

    @Experimental
    public void getChatCompletionsAsync(ChatCompletionRequest request, ResponseHandler<ChatCompletionResponse> handler) {

        if (request.stream() != null && request.stream()) {
            throw new IllegalArgumentException("Request parameter 'stream' with value 'true' is not compatible with getChatCompletionAsync(...) method. If you need streaming, use one of streamChatCompletion(...) methods. If you do NOT need streaming, do not set 'stream' parameter in ChatCompletionRequest, or set it to 'false'.");
        }

        openAiApi.chatCompletion(request)
                .enqueue(new Callback<ChatCompletionResponse>() {

                    @Override
                    public void onResponse(Call<ChatCompletionResponse> call, retrofit2.Response<ChatCompletionResponse> response) {
                        // TODO
                        if (response.isSuccessful()) {
                            handler.onResponse(response.body());
                        } else {
                            try {
                                handler.onFailure(new RuntimeException(response.errorBody().string()));
                            } catch (IOException e) {
                                handler.onFailure(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
                        handler.onFailure(t);
                    }
                });
    }

    @Experimental
    public void getChatCompletionAsync(String userMessage, ResponseHandler<String> handler) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addUserMessage(userMessage)
                .build();

        getChatCompletionsAsync(request, new ResponseHandler<ChatCompletionResponse>() {

            @Override
            public void onResponse(ChatCompletionResponse response) {
                handler.onResponse(response.content());
            }

            @Override
            public void onFailure(Throwable t) {
                handler.onFailure(t);
            }
        });
    }

    @Experimental
    public void streamChatCompletions(ChatCompletionRequest request, StreamingResponseHandler handler) {

        if (request.stream() != null && !request.stream()) {
            throw new IllegalArgumentException("Request parameter 'stream' with value 'false' is not compatible with streamChatCompletion(...) method. If you do not need streaming, use one of getChatCompletion(...) or getChatCompletionAsync(...) methods. If you need streaming, do not set 'stream' parameter in ChatCompletionRequest, or set it to 'true'.");
        }

        stream(
                ChatCompletionRequest.builder()
                        .from(request)
                        .stream(true)
                        .build(),
                "v1/chat/completions",
                ChatCompletionResponse.class,
                response -> response.choices().get(0).delta().content(),
                handler
        );
    }

    @Experimental
    public void streamChatCompletion(String userMessage, StreamingResponseHandler handler) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .addUserMessage(userMessage)
                .stream(true)
                .build();

        stream(
                request,
                "v1/chat/completions",
                ChatCompletionResponse.class,
                response -> response.choices().get(0).delta().content(),
                handler
        );
    }


    // embeddings

    public EmbeddingResponse getEmbeddings(EmbeddingRequest request) {
        return execute(openAiApi.embedding(request));
    }

    @Experimental
    public List<Float> getEmbedding(String input) {

        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(input)
                .build();

        EmbeddingResponse response = getEmbeddings(request);

        return response.embedding();
    }

    @Experimental
    public OnResponseStep<EmbeddingResponse> getEmbeddingsAsync(EmbeddingRequest request) {

        return new OnResponseStep<EmbeddingResponse>() {

            @Override
            public OnFailureStep onResponse(Consumer<EmbeddingResponse> onResponse) {
                return new OnFailureStep() {

                    @Override
                    public ExecutionStep onFailure(Consumer<Throwable> onFailure) {
                        return new ExecutionStep() {

                            @Override
                            public void execute() {
                                getEmbeddingsAsync(request, new ResponseHandler<EmbeddingResponse>() {

                                    @Override
                                    public void onResponse(EmbeddingResponse response) {
                                        onResponse.accept(response);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        onFailure.accept(t);
                                    }
                                });
                            }
                        };
                    }
                };
            }
        };
    }

    @Experimental
    public OnResponseStep<List<Float>> getEmbeddingAsync(String input) {

        return new OnResponseStep<List<Float>>() {

            @Override
            public OnFailureStep onResponse(Consumer<List<Float>> onResponse) {
                return new OnFailureStep() {

                    @Override
                    public ExecutionStep onFailure(Consumer<Throwable> onFailure) {
                        return new ExecutionStep() {

                            @Override
                            public void execute() {
                                getEmbeddingAsync(input, new ResponseHandler<List<Float>>() {

                                    @Override
                                    public void onResponse(List<Float> response) {
                                        onResponse.accept(response);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        onFailure.accept(t);
                                    }
                                });
                            }
                        };
                    }
                };
            }
        };
    }

    @Experimental
    public void getEmbeddingsAsync(EmbeddingRequest request, ResponseHandler<EmbeddingResponse> handler) {

        openAiApi.embedding(request)
                .enqueue(new Callback<EmbeddingResponse>() {

                    @Override
                    public void onResponse(Call<EmbeddingResponse> call, retrofit2.Response<EmbeddingResponse> response) {
                        handler.onResponse(response.body());
                    }

                    @Override
                    public void onFailure(Call<EmbeddingResponse> call, Throwable t) {
                        handler.onFailure(t);
                    }
                });
    }

    @Experimental
    public void getEmbeddingAsync(String input, ResponseHandler<List<Float>> handler) {

        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(input)
                .build();

        openAiApi.embedding(request)
                .enqueue(new Callback<EmbeddingResponse>() {
                    @Override
                    public void onResponse(Call<EmbeddingResponse> call, retrofit2.Response<EmbeddingResponse> response) {
                        handler.onResponse(response.body().embedding());
                    }

                    @Override
                    public void onFailure(Call<EmbeddingResponse> call, Throwable t) {
                        handler.onFailure(t);
                    }
                });
    }

    private <Res> Res execute(Call<Res> call) {
        try {
            retrofit2.Response<Res> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new RuntimeException(response.errorBody().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <Req, Res> void stream(Req request, String endpoint, Class<Res> responseClass, Function<Res, String> partialResponseStringExtractor, StreamingResponseHandler handler) {

        String requestJson = Json.toJson(request);

        Request okHttpRequest = new Request.Builder()
                .url(url + endpoint)
                .post(RequestBody.create(requestJson, MediaType.get("application/json; charset=utf-8")))
                .build();

        EventSourceListener eventSourceListener = new EventSourceListener() {

            private final StringBuilder completeResponseBuilder = new StringBuilder();

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                log.trace("onOpen() {}", response);
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                log.trace("onEvent() data: {}", data);

                if ("[DONE]".equals(data)) {
                    handler.onCompleteResponse(completeResponseBuilder.toString());
                    return;
                }

                try {
                    Res response = Json.fromJson(data, responseClass);
                    String partialResponseString = partialResponseStringExtractor.apply(response);
                    if (partialResponseString != null) {
                        completeResponseBuilder.append(partialResponseString);
                        handler.onPartialResponse(partialResponseString); // fail-fast
                    }
                } catch (Exception e) {
                    handler.onFailure(e);
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.trace("onClosed()");
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                log.trace("onFailure()\nThrowable: {}\nResponse: {}", t, response);
                // TODO
                if (t != null) {
                    handler.onFailure(t);
                } else {
                    try {
                        handler.onFailure(new RuntimeException(response.body().string()));
                    } catch (IOException e) {
                        handler.onFailure(e);
                    }
                }
            }
        };

        EventSources.createFactory(okHttpClient)
                .newEventSource(okHttpRequest, eventSourceListener);
    }
}
