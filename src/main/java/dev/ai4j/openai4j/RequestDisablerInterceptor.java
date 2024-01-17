package dev.ai4j.openai4j;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestDisablerInterceptor implements Interceptor {
    private static final String MESSAGE = "Requests to the live system are disabled";

    @Override
    public Response intercept(Chain chain) {
        return new Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(400)
            .message(MESSAGE)
            .body(ResponseBody.create(MESSAGE, null))
            .build();
    }
}
