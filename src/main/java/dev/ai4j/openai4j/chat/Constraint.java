package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;

import java.util.Objects;

@Experimental
public class Constraint {

    public static final Constraint STRING = type("string");
    public static final Constraint NUMBER = type("number");
    public static final Constraint OBJECT = type("object");
    public static final Constraint ARRAY = type("array");
    public static final Constraint BOOLEAN = type("boolean");
    public static final Constraint NULL = type("null");

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

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Constraint
                && equalTo((Constraint) another);
    }

    private boolean equalTo(Constraint another) {
        return Objects.equals(key, another.key)
                && Objects.equals(value, another.value);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(key);
        h += (h << 5) + Objects.hashCode(value);
        return h;
    }

    @Override
    public String toString() {
        return "Constraint{"
                + "key=" + key
                + ", value=" + value
                + "}";
    }

    @Experimental
    public static Constraint from(String key, Object value) {
        return new Constraint(key, value);
    }

    @Experimental
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
