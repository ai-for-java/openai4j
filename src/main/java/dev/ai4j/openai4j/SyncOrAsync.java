package dev.ai4j.openai4j;

import java.util.function.Consumer;

public interface SyncOrAsync<ResponseContent> {

    ResponseContent execute();

    AsyncResponseHandling onResponse(Consumer<ResponseContent> responseHandler);
}