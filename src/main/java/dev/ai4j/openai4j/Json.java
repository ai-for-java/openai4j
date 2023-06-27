package dev.ai4j.openai4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
import static dev.ai4j.openai4j.MessageTypeAdapter.MESSAGE_TYPE_ADAPTER_FACTORY;

class Json {

    static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapterFactory(MESSAGE_TYPE_ADAPTER_FACTORY)
            .create();

    static String toJson(Object o) {
        return GSON.toJson(o);
    }

    static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
}
