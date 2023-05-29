package dev.ai4j.openai4j.chat;

import dev.ai4j.openai4j.Experimental;
import dev.ai4j.openai4j.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.ai4j.openai4j.Model.GPT_3_5_TURBO;
import static dev.ai4j.openai4j.chat.Message.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public final class ChatCompletionRequest {

    private final String model;
    private final List<Message> messages;
    private final Double temperature;
    private final Double topP;
    private final Integer n;
    private final Boolean stream;
    private final List<String> stop;
    private final Integer maxTokens;
    private final Double presencePenalty;
    private final Double frequencyPenalty;
    private final Map<String, Integer> logitBias;
    private final String user;

    private ChatCompletionRequest(Builder builder) {
        this.model = builder.model;
        this.messages = builder.messages;
        this.temperature = builder.temperature;
        this.topP = builder.topP;
        this.n = builder.n;
        this.stream = builder.stream;
        this.stop = builder.stop;
        this.maxTokens = builder.maxTokens;
        this.presencePenalty = builder.presencePenalty;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.logitBias = builder.logitBias;
        this.user = builder.user;
    }

    public String model() {
        return model;
    }

    public List<Message> messages() {
        return messages;
    }

    public Double temperature() {
        return temperature;
    }

    public Double topP() {
        return topP;
    }

    public Integer n() {
        return n;
    }

    public Boolean stream() {
        return stream;
    }

    public List<String> stop() {
        return stop;
    }

    public Integer maxTokens() {
        return maxTokens;
    }

    public Double presencePenalty() {
        return presencePenalty;
    }

    public Double frequencyPenalty() {
        return frequencyPenalty;
    }

    public Map<String, Integer> logitBias() {
        return logitBias;
    }

    public String user() {
        return user;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ChatCompletionRequest
                && equalTo((ChatCompletionRequest) another);
    }

    private boolean equalTo(ChatCompletionRequest another) {
        return model.equals(another.model)
                && messages.equals(another.messages)
                && temperature.equals(another.temperature)
                && topP.equals(another.topP)
                && n.equals(another.n)
                && stream.equals(another.stream)
                && stop.equals(another.stop)
                && maxTokens.equals(another.maxTokens)
                && presencePenalty.equals(another.presencePenalty)
                && frequencyPenalty.equals(another.frequencyPenalty)
                && logitBias.equals(another.logitBias)
                && user.equals(another.user);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + model.hashCode();
        h += (h << 5) + messages.hashCode();
        h += (h << 5) + temperature.hashCode();
        h += (h << 5) + topP.hashCode();
        h += (h << 5) + n.hashCode();
        h += (h << 5) + stream.hashCode();
        h += (h << 5) + stop.hashCode();
        h += (h << 5) + maxTokens.hashCode();
        h += (h << 5) + presencePenalty.hashCode();
        h += (h << 5) + frequencyPenalty.hashCode();
        h += (h << 5) + logitBias.hashCode();
        h += (h << 5) + user.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "ChatCompletionRequest{"
                + "model=" + model
                + ", messages=" + messages
                + ", temperature=" + temperature
                + ", topP=" + topP
                + ", n=" + n
                + ", stream=" + stream
                + ", stop=" + stop
                + ", maxTokens=" + maxTokens
                + ", presencePenalty=" + presencePenalty
                + ", frequencyPenalty=" + frequencyPenalty
                + ", logitBias=" + logitBias
                + ", user=" + user
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String model = GPT_3_5_TURBO.stringValue();
        private List<Message> messages;
        private Double temperature;
        private Double topP;
        private Integer n;
        private Boolean stream;
        private List<String> stop;
        private Integer maxTokens;
        private Double presencePenalty;
        private Double frequencyPenalty;
        private Map<String, Integer> logitBias;
        private String user;

        private Builder() {
        }

        public Builder from(ChatCompletionRequest instance) {
            model(instance.model);
            messages(instance.messages);
            temperature(instance.temperature);
            topP(instance.topP);
            n(instance.n);
            stream(instance.stream);
            stop(instance.stop);
            maxTokens(instance.maxTokens);
            presencePenalty(instance.presencePenalty);
            frequencyPenalty(instance.frequencyPenalty);
            logitBias(instance.logitBias);
            user(instance.user);
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        @Experimental
        public Builder model(Model model) {
            return model(model.stringValue());
        }

        public Builder messages(List<Message> messages) {
            if (messages == null) {
                return this;
            }
            this.messages = unmodifiableList(messages);
            return this;
        }

        @Experimental
        public Builder messages(Message... messages) {
            return messages(asList(messages));
        }

        @Experimental
        public Builder addSystemMessage(String systemMessage) {
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }
            this.messages.add(systemMessage(systemMessage));
            return this;
        }

        @Experimental
        public Builder addUserMessage(String userMessage) {
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }
            this.messages.add(userMessage(userMessage));
            return this;
        }

        @Experimental
        public Builder addAssistantMessage(String assistantMessage) {
            if (this.messages == null) {
                this.messages = new ArrayList<>();
            }
            this.messages.add(assistantMessage(assistantMessage));
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder topP(Double topP) {
            this.topP = topP;
            return this;
        }

        public Builder n(Integer n) {
            this.n = n;
            return this;
        }

        public Builder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        public Builder stop(List<String> stop) {
            if (stop == null) {
                return this;
            }
            this.stop = unmodifiableList(stop);
            return this;
        }

        public Builder stop(String... stop) {
            return stop(asList(stop));
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder presencePenalty(Double presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public Builder frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder logitBias(Map<String, Integer> logitBias) {
            if (logitBias == null) {
                return this;
            }
            this.logitBias = unmodifiableMap(logitBias);
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public ChatCompletionRequest build() {
            return new ChatCompletionRequest(this);
        }
    }
}
