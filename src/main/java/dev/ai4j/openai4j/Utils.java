package dev.ai4j.openai4j;

import java.io.IOException;

class Utils {

    static RuntimeException toException(retrofit2.Response<?> response) throws IOException {

        int code = response.code();
        String body = response.errorBody().string();

        String errorMessage = String.format("status code: %s; body: %s", code, body);
        return new RuntimeException(errorMessage);
    }

    static RuntimeException toException(okhttp3.Response response) throws IOException {

        int code = response.code();
        String body = response.body().string();

        String errorMessage = String.format("status code: %s; body: %s", code, body);
        return new RuntimeException(errorMessage);
    }
}
