package dev.ai4j.openai4j;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.ai4j.openai4j.chat.FunctionCall;
import dev.ai4j.openai4j.chat.Message;

import java.io.IOException;

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
            out.value(message.content());
        }

        if (message.name() != null) {
            out.name("name");
            out.value(message.name());
        }

        if (message.functionCall() != null) {
            out.name("function_call");
            TypeAdapter<FunctionCall> functionCallTypeAdapter = Json.GSON.getAdapter(FunctionCall.class);
            functionCallTypeAdapter.write(out, message.functionCall());
        }

        out.endObject();
    }

    @Override
    public Message read(JsonReader in) throws IOException {
        return delegate.read(in);
    }
}