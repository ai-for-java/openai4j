package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;

public class Constraint {

    private final String key;
    private final Object value;

    public Constraint(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return key;
    }

    public Object value() {
        return value;
    }

    // TODO eq hash tos

    public static Constraint from(String key, Object value) {
        return new Constraint(key, value);
    }

    public static Constraint constraint(String key, Object value) {
        return from(key, value);
    }

    @Experimental
    public static Constraint type(String value) {
        return from("type", value);
    }

    @Experimental
    public static Constraint description(String value) {
        return from("description", value);
    }

    @Experimental
    public static Constraint enums(String... enums) {
        return from("enum", enums);
    }
}
