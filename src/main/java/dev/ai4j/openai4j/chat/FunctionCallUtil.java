package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Json;

import java.util.Map;

public class FunctionCallUtil {

    public static <T> T argument(String name, FunctionCall function) {
        Map<String, Object> arguments = argumentsAsMap(function.arguments()); // TODO cache?
        return (T) arguments.get(name);
    }

    public static Map<String, Object> argumentsAsMap(String arguments) {
        return Json.fromJson(arguments, Map.class);
    }
}
