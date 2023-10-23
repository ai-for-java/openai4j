package dev.ai4j.openai4j;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.ai4j.openai4j.chat.Role;
import java.io.IOException;

public class RoleAdapter extends TypeAdapter<Role> {

    @Override
    public void write(final JsonWriter jsonWriter, final Role role) throws IOException {
        jsonWriter.value(role.toString());
    }

    @Override
    public Role read(JsonReader jsonReader) throws IOException {
        return Role.from(jsonReader.nextString());
    }
}
