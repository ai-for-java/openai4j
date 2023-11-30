package dev.ai4j.openai4j;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class EnumSerializer implements JsonSerializer<Enum<?>> {

    @Override
    public JsonElement serialize(Enum<?> e, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(e.name().toLowerCase());
    }
}
