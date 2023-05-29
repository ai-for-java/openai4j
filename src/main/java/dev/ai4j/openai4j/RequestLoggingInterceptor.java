package dev.ai4j.openai4j;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RequestLoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    private static final Pattern BEARER_PATTERN = Pattern.compile("(Bearer\\s*sk-)(\\w{2})(\\w+)(\\w{2})");

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        log.debug("Request: {}", maskApiToken(request.toString()));

        return chain.proceed(request);
    }

    private static String maskApiToken(String request) {
        try {

            Matcher matcher = BEARER_PATTERN.matcher(request);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1) + matcher.group(2) + "..." + matcher.group(4));
            }
            matcher.appendTail(sb);

            return sb.toString();
        } catch (Exception e) {
            return "Failed to mask the API key. Therefore, avoid logging the entire request.";
        }
    }
}
