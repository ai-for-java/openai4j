package dev.ai4j.openai4j;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dev.ai4j.openai4j.chat.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dev.ai4j.openai4j.Json.GSON;

class MessageTypeAdapter extends TypeAdapter<Message> {



    static final TypeAdapterFactory MESSAGE_TYPE_ADAPTER_FACTORY = new TypeAdapterFactory() {

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != Message.class) {
                return null;
            }
            TypeAdapter<Message> delegate = (TypeAdapter<Message>) gson.getDelegateAdapter(this, type);
            return (TypeAdapter<T>) new MessageTypeAdapter(delegate);
        }
    };

    private final TypeAdapter<Message> delegate;

    private MessageTypeAdapter(TypeAdapter<Message> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(JsonWriter out, Message message) throws IOException {
        out.beginObject();

        out.name("role");
        out.value(message.role().toString());

        out.name("content");
        if (message.content() == null) {
            boolean serializeNulls = out.getSerializeNulls();
            out.setSerializeNulls(true);
            out.nullValue();
            out.setSerializeNulls(serializeNulls);
        } else {
            if (message.content().get(0).type() != null){
                TypeAdapter<List> contentTypeAdapter = GSON.getAdapter(List.class);
                contentTypeAdapter.write(out,message.content());
            }else {
                out.value(message.content().get(0).text());
            }
        }

        if (message.name() != null) {
            out.name("name");
            out.value(message.name());
        }

        if (message.functionCall() != null) {
            out.name("function_call");
            TypeAdapter<FunctionCall> functionCallTypeAdapter = GSON.getAdapter(FunctionCall.class);
            functionCallTypeAdapter.write(out, message.functionCall());
        }

        if (message.toolCalls() != null){
            out.name("tool_calls");
            TypeAdapter<List> toolCallsTypeAdapter = GSON.getAdapter(List.class);
            toolCallsTypeAdapter.write(out, message.toolCalls());
        }

        out.endObject();
    }

    @Override
    public Message read(JsonReader in) throws IOException {
//        return delegate.read(in);
        in.beginObject();

        Message.Builder builder = Message.builder();

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case "role":
                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+in.nextString());
                    if (in.peek() == JsonToken.STRING) {
                        builder.role(Role.valueOf(in.nextString()));
                    } else {
                        // 错误情况：非字符串的 JSON 令牌。可以抛出异常或者跳过这个字段。
                        in.skipValue();
                        // 如果这个字段是必须的，你可能需要抛出异常，例如：
                        // throw new JsonSyntaxException("Expected a string for role field but got: " + in.peek());
                    }
//                    builder.role(Role.valueOf(in.nextString())); // 假设 Role 是一个枚举
                    break;
                case "content":
                    if (in.peek() == JsonToken.STRING) {
                        // 如果 content 是一个字符串，将其转化成 Content 对象
                        String contentString = in.nextString();
//                        Content content = //... 创建 Content 对象;
                        Content content = Content.builder().text(contentString).type(ContentType.TEXT.stringValue()).build();
                        builder.content(Collections.singletonList(content));
                    } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                        // 如果 content 是一个数组，使用标准方法来解析
                        Type listOfContent = new TypeToken<List<Content>>(){}.getType();
                        List<Content> contentList = GSON.fromJson(in, listOfContent);
                        builder.content(contentList);
                    }
                    break;
                case "name":
                    builder.name(in.nextString());
                    break;
                case "function_call":
                    // 解析 function_call
                    FunctionCall functionCall = GSON.fromJson(in, FunctionCall.class);
                    builder.functionCall(functionCall);
                    break;
                // 处理其他字段...
            }
        }

        in.endObject();
        return builder.build();
    }
}