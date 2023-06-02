package dev.ai4j.openai4j;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static dev.ai4j.openai4j.RequestLoggingInterceptor.inOneLine;

class ResponseLoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(ResponseLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);

        log(response);

        return response;
    }

    static void log(Response response) {
        try {
            log.debug(
                    "Response:\n- status code: {}\n- headers: {}\n- body: {}",
                    response.code(),
                    inOneLine(response.headers()),
                    getBody(response)
            );
        } catch (IOException e) {
            log.warn("Failed to log response", e);
        }
    }

    private static String getBody(Response response) throws IOException {
        if (isEventStream(response)) {
            return "[skipping response body due to streaming]";
        } else {
            return response.peekBody(Long.MAX_VALUE).string();
        }
    }

    private static boolean isEventStream(Response response) {
        String contentType = response.header("content-type");
        return contentType != null && contentType.contains("event-stream");
    }
}
