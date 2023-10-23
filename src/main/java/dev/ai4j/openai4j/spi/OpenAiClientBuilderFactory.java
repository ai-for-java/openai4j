package dev.ai4j.openai4j.spi;

import dev.ai4j.openai4j.OpenAiClient;

import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public interface OpenAiClientBuilderFactory extends Supplier<OpenAiClient.Builder> {
}
