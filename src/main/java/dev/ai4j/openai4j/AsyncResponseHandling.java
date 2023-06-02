package dev.ai4j.openai4j;

import java.util.function.Consumer;

public interface AsyncResponseHandling {

    ErrorHandling onError(Consumer<Throwable> errorHandler);

    ErrorHandling ignoreErrors();
}