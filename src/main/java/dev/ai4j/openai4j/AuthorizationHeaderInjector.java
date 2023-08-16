package dev.ai4j.openai4j;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

class AuthorizationHeaderInjector implements Interceptor {

    private final String apiKey;

    AuthorizationHeaderInjector(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        return chain.proceed(request);
    }
}
