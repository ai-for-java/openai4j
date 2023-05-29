package dev.ai4j.openai4j;

public interface ResponseHandler<R> {

    void onResponse(R response);

    void onFailure(Throwable t);
}
