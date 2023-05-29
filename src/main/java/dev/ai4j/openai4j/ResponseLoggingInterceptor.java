package dev.ai4j.openai4j;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

class ResponseLoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(ResponseLoggingInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);

        log.debug("Response code: {}", response.code());
        log.debug("Response headers: {}", inOneLine(response));

        if (isEventStream(response)) {
            log.debug("Response body: [skipping response body due to streaming]");
        } else {
            log.debug("Response body: {}", response.peekBody(Long.MAX_VALUE).string());
        }

        return response;
    }

    private static boolean isEventStream(Response response) {
        String contentType = response.header("content-type");
        return contentType != null && contentType.contains("event-stream");
    }

    private static String inOneLine(Response response) {
        return stream(response.headers().toString().split("\n"))
                .filter(header -> header != null && !header.isEmpty())
                .map(header -> "[" + header + "]")
                .collect(Collectors.joining(", "));
    }
}
