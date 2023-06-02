package dev.ai4j.openai4j;

public interface StreamingResponseHandling extends AsyncResponseHandling {

    StreamingCompletionHandling onComplete(Runnable streamingCompletionCallback);
}
