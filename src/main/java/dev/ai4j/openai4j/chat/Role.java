package dev.ai4j.openai4j.chat;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

@JsonAdapter(Role.RoleAdapter.class)
public enum Role {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String stringValue;

    Role(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return String.valueOf(stringValue);
    }

    static Role from(String stringValue) {
        for (Role role : Role.values()) {
            if (role.stringValue.equals(stringValue)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: '" + stringValue + "'");
    }

    static class RoleAdapter extends TypeAdapter<Role> {

        @Override
        public void write(final JsonWriter jsonWriter, final Role role) throws IOException {
            jsonWriter.value(role.toString());
        }

        @Override
        public Role read(JsonReader jsonReader) throws IOException {
            return Role.from(jsonReader.nextString());
        }
    }
}