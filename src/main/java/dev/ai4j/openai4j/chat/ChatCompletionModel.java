package dev.ai4j.openai4j.chat;

public enum ChatCompletionModel {

    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_3_5_TURBO_0613("gpt-3.5-turbo-0613"),
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),

    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
    GPT_3_5_TURBO_16K_0613("gpt-3.5-turbo-16k-0613"),

    GPT_4("gpt-4"),
    GPT_4_0314("gpt-4-0314"),
    GPT_4_0613("gpt-4-0613"),

    GPT_4_32K("gpt-4-32k"),
    GPT_4_32K_0314("gpt-4-32k-0314"),
    GPT_4_32K_0613("gpt-4-32k-0613"),

    GPT_4_1106_PREVIEW("gpt-4-1106-preview"),
    GPT_4_VISION_PREVIEW("gpt-4-vision-preview");

    private final String value;

    ChatCompletionModel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
