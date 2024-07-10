package dev.ai4j.openai4j.chat;

public enum ChatCompletionModel {

    GPT_3_5_TURBO("gpt-3.5-turbo"), // alias
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
    GPT_3_5_TURBO_0125("gpt-3.5-turbo-0125"),

    GPT_4("gpt-4"), // alias
    GPT_4_0314("gpt-4-0314"),
    GPT_4_0613("gpt-4-0613"),

    GPT_4_TURBO("gpt-4-turbo"), // alias
    GPT_4_TURBO_2024_04_09("gpt-4-turbo-2024-04-09"), // With vision support
    GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview"), // alias
    GPT_4_1106_PREVIEW("gpt-4-1106-preview"),
    GPT_4_0125_PREVIEW("gpt-4-0125-preview"),

    GPT_4_32K("gpt-4-32k"), // alias
    GPT_4_32K_0314("gpt-4-32k-0314"),
    GPT_4_32K_0613("gpt-4-32k-0613"),

    @Deprecated
    GPT_4_VISION_PREVIEW("gpt-4-vision-preview"),
    GPT_4O("gpt-4o"),
    GPT_4O_2024_05_13("gpt-4o-2024-05-13");

    private final String value;

    ChatCompletionModel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
