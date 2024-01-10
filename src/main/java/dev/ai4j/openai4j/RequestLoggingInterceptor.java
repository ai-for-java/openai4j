package dev.ai4j.openai4j;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

class RequestLoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private LogLevel logLevel = LogLevel.DEBUG;

    private static final Pattern BEARER_PATTERN = Pattern.compile("(Bearer\\s*sk-)(\\w{2})(\\w+)(\\w{2})");

    public RequestLoggingInterceptor() {
    }

    public RequestLoggingInterceptor(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        log(request);

        return chain.proceed(request);
    }

    private void log(Request request) {
        String message = "Request:\n- method: {}\n- url: {}\n- headers: {}\n- body: {}";

        try {
            switch (logLevel) {
                case INFO:
                    logInfo(request, message);
                    break;
                case WARN:
                    logWarn(request, message);
                    break;
                case ERROR:
                    logError(request, message);
                    break;
                default:
                    logDebug(request, message);
            }
        } catch (Exception e) {
            log.warn("Failed to log request", e);
        }
    }

    private void logInfo(Request request, String message) {
        log.info(
                message,
                request.method(),
                request.url(),
                inOneLine(request.headers()),
                getBody(request)
        );
    }

    private void logWarn(Request request, String message) {
        log.warn(
                message,
                request.method(),
                request.url(),
                inOneLine(request.headers()),
                getBody(request)
        );
    }

    private void logError(Request request, String message) {
        log.error(
                message,
                request.method(),
                request.url(),
                inOneLine(request.headers()),
                getBody(request)
        );
    }

    private void logDebug(Request request, String message) {
        log.debug(
                message,
                request.method(),
                request.url(),
                inOneLine(request.headers()),
                getBody(request)
        );
    }

    static String inOneLine(Headers headers) {

        return stream(headers.spliterator(), false)
                .map(header -> {
                    String headerKey = header.component1();
                    String headerValue = header.component2();
                    if (headerKey.equals("Authorization")) {
                        headerValue = maskAuthorizationHeaderValue(headerValue);
                    } else if (headerKey.equals("api-key")) {
                        headerValue = maskApiKeyHeaderValue(headerValue);
                    }
                    return String.format("[%s: %s]", headerKey, headerValue);
                })
                .collect(joining(", "));
    }

    private static String maskAuthorizationHeaderValue(String authorizationHeaderValue) {
        try {
            Matcher matcher = BEARER_PATTERN.matcher(authorizationHeaderValue);

            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1) + matcher.group(2) + "..." + matcher.group(4));
            }
            matcher.appendTail(sb);

            return sb.toString();
        } catch (Exception e) {
            return "Failed to mask the API key.";
        }
    }

    private static String maskApiKeyHeaderValue(String apiKeyHeaderValue) {
        try {
            if (apiKeyHeaderValue.length() <= 4) {
                return apiKeyHeaderValue;
            }
            return apiKeyHeaderValue.substring(0, 2)
                    + "..."
                    + apiKeyHeaderValue.substring(apiKeyHeaderValue.length() - 2);
        } catch (Exception e) {
            return "Failed to mask the API key.";
        }
    }

    private static String getBody(Request request) {
        try {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            log.warn("Exception happened while reading request body", e);
            return "[Exception happened while reading request body. Check logs for more details.]";
        }
    }
}
