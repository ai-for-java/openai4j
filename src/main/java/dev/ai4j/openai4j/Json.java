package dev.ai4j.openai4j;

import com.google.gson.Gson;

class Json {

    private static final Gson GSON = new Gson();

    static String toJson(Object o) {
        return GSON.toJson(o);
    }

    static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
}
