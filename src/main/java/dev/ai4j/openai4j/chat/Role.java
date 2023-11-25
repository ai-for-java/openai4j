package dev.ai4j.openai4j.chat;

public enum Role {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool"),
    @Deprecated
    FUNCTION("function");

    private final String stringValue;

    Role(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public static Role from(String stringValue) {
        for (Role role : Role.values()) {
            if (role.stringValue.equals(stringValue)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: '" + stringValue + "'");
    }
}
