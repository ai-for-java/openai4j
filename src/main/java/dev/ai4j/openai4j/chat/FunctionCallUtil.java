package dev.ai4j.openai4j.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class FunctionCallUtil {

    public static final Gson GSON = new Gson();
    public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    public static <T> T argument(String name, FunctionCall function) {
        Map<String, Object> arguments = argumentsAsMap(function.arguments()); // TODO cache?
        return (T) arguments.get(name);
    }

    public static Map<String, Object> argumentsAsMap(String arguments) {
        return GSON.fromJson(arguments, MAP_TYPE);
    }
}
