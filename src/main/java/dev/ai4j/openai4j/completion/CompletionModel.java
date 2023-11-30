package dev.ai4j.openai4j.completion;

public enum CompletionModel {

    TEXT_DAVINCI_003("text-davinci-003");

    private final String value;

    CompletionModel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
