package dev.ai4j.openai4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

class Json {

    static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeHierarchyAdapter(Enum.class, new EnumSerializer())
            .registerTypeHierarchyAdapter(Enum.class, new EnumDeserializer())
            .setPrettyPrinting()
            .create();

    static String toJson(Object o) {
        return GSON.toJson(o);
    }

    static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
}
