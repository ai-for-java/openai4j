package dev.ai4j.openai4j;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.ai4j.openai4j.chat.FunctionCall;

import java.lang.reflect.Type;
import java.util.Map;

public class FunctionCallUtil {

    public static final Gson GSON = new Gson();
    public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    public static <T> T argument(FunctionCall functionCall, String name) {
        Map<String, Object> arguments = argumentsAsMap(functionCall.arguments()); // TODO cache
        return (T) arguments.get(name);
    }

    public static Map<String, Object> argumentsAsMap(String arguments) {
        return GSON.fromJson(arguments, MAP_TYPE);
    }
}
