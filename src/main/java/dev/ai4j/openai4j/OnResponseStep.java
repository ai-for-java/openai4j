package dev.ai4j.openai4j;

import java.util.function.Consumer;

public interface OnResponseStep<Response> {

    OnFailureStep onResponse(Consumer<Response> onResponse);
}