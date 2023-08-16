package dev.ai4j.openai4j;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

class ApiKeyHeaderInjector implements Interceptor {

    private final String apiKey;

    ApiKeyHeaderInjector(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request()
                .newBuilder()
                .addHeader("api-key", apiKey)
                .build();

        return chain.proceed(request);
    }
}
