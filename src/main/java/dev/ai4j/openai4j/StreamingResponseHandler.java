package dev.ai4j.openai4j;

@Experimental
public interface StreamingResponseHandler {

    void onPartialResponse(String partialResponse);

    default void onCompleteResponse(String completeResponse) {
    }

    void onFailure(Throwable t);
}
