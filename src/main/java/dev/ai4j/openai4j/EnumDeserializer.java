package dev.ai4j.openai4j;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public class EnumDeserializer implements JsonDeserializer<Enum<?>> {

    @Override
    public Enum<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        Class<Enum<?>> enumType = (Class<Enum<?>>) type;
        return Enum.valueOf(enumType.asSubclass(Enum.class), json.getAsString().toUpperCase());
    }
}
