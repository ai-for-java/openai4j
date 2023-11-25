package dev.ai4j.openai4j.chat;

import java.util.Objects;

public class Tool {

    private final String type;

    private final Function function;

    public Tool(Builder builder) {
        this.type = builder.type;
        this.function = builder.function;
    }

    public String type(){
        return this.type;
    }

    public Function function(){
        return this.function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tool tool = (Tool) o;
        return Objects.equals(type, tool.type) && Objects.equals(function, tool.function);
    }

    private boolean equalTo(Tool another) {
        return Objects.equals(type, another.type)
                && Objects.equals(function, another.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, function);
    }

    @Override
    public String toString() {
        return "Tool{" +
                "type='" + type + '\'' +
                ", function=" + function +
                '}';
    }

    public static Tool.Builder builder() {
        return new Tool.Builder();
    }

    public static final class Builder {

        private String type;

        private Function function;

        private Builder() {
        }

        public Tool.Builder type(String type) {
            this.type = type;
            return this;
        }


        public Tool.Builder function(Function function) {
            this.function = function;
            return this;
        }

        public Tool build() {
            return new Tool(this);
        }

    }

}
