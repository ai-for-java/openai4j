package dev.ai4j.openai4j;

import java.util.function.Consumer;

public interface OnFailureStep {

    ExecutionStep onFailure(Consumer<Throwable> onFailure);
}