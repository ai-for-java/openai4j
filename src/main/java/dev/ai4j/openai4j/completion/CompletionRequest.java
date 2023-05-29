package dev.ai4j.openai4j.completion;

import dev.ai4j.openai4j.Experimental;
import dev.ai4j.openai4j.Model;

import java.util.List;
import java.util.Map;

import static dev.ai4j.openai4j.Model.TEXT_DAVINCI_003;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public final class CompletionRequest {

    private final String model;
    private final String prompt;
    private final String suffix;
    private final Integer maxTokens;
    private final Double temperature;
    private final Double topP;
    private final Integer n;
    private final Boolean stream;
    private final Integer logprobs;
    private final Boolean echo;
    private final List<String> stop;
    private final Double presencePenalty;
    private final Double frequencyPenalty;
    private final Integer bestOf;
    private final Map<String, Integer> logitBias;
    private final String user;

    private CompletionRequest(Builder builder) {
        this.model = builder.model;
        this.prompt = builder.prompt;
        this.suffix = builder.suffix;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
        this.topP = builder.topP;
        this.n = builder.n;
        this.stream = builder.stream;
        this.logprobs = builder.logprobs;
        this.echo = builder.echo;
        this.stop = builder.stop;
        this.presencePenalty = builder.presencePenalty;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.bestOf = builder.bestOf;
        this.logitBias = builder.logitBias;
        this.user = builder.user;
    }

    public String model() {
        return model;
    }

    public String prompt() {
        return prompt;
    }

    public String suffix() {
        return suffix;
    }

    public Integer maxTokens() {
        return maxTokens;
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

    public Integer logprobs() {
        return logprobs;
    }

    public Boolean echo() {
        return echo;
    }

    public List<String> stop() {
        return stop;
    }

    public Double presencePenalty() {
        return presencePenalty;
    }

    public Double frequencyPenalty() {
        return frequencyPenalty;
    }

    public Integer bestOf() {
        return bestOf;
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
        return another instanceof CompletionRequest
                && equalTo((CompletionRequest) another);
    }

    private boolean equalTo(CompletionRequest another) {
        return model.equals(another.model)
                && prompt.equals(another.prompt)
                && suffix.equals(another.suffix)
                && maxTokens.equals(another.maxTokens)
                && temperature.equals(another.temperature)
                && topP.equals(another.topP)
                && n.equals(another.n)
                && stream.equals(another.stream)
                && logprobs.equals(another.logprobs)
                && echo.equals(another.echo)
                && stop.equals(another.stop)
                && presencePenalty.equals(another.presencePenalty)
                && frequencyPenalty.equals(another.frequencyPenalty)
                && bestOf.equals(another.bestOf)
                && logitBias.equals(another.logitBias)
                && user.equals(another.user);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + model.hashCode();
        h += (h << 5) + prompt.hashCode();
        h += (h << 5) + suffix.hashCode();
        h += (h << 5) + maxTokens.hashCode();
        h += (h << 5) + temperature.hashCode();
        h += (h << 5) + topP.hashCode();
        h += (h << 5) + n.hashCode();
        h += (h << 5) + stream.hashCode();
        h += (h << 5) + logprobs.hashCode();
        h += (h << 5) + echo.hashCode();
        h += (h << 5) + stop.hashCode();
        h += (h << 5) + presencePenalty.hashCode();
        h += (h << 5) + frequencyPenalty.hashCode();
        h += (h << 5) + bestOf.hashCode();
        h += (h << 5) + logitBias.hashCode();
        h += (h << 5) + user.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "CompletionRequest{"
                + "model=" + model
                + ", prompt=" + prompt
                + ", suffix=" + suffix
                + ", maxTokens=" + maxTokens
                + ", temperature=" + temperature
                + ", topP=" + topP
                + ", n=" + n
                + ", stream=" + stream
                + ", logprobs=" + logprobs
                + ", echo=" + echo
                + ", stop=" + stop
                + ", presencePenalty=" + presencePenalty
                + ", frequencyPenalty=" + frequencyPenalty
                + ", bestOf=" + bestOf
                + ", logitBias=" + logitBias
                + ", user=" + user
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String model = TEXT_DAVINCI_003.stringValue();
        private String prompt;
        private String suffix;
        private Integer maxTokens;
        private Double temperature;
        private Double topP;
        private Integer n;
        private Boolean stream;
        private Integer logprobs;
        private Boolean echo;
        private List<String> stop;
        private Double presencePenalty;
        private Double frequencyPenalty;
        private Integer bestOf;
        private Map<String, Integer> logitBias;
        private String user;

        private Builder() {
        }

        @Experimental
        public Builder from(CompletionRequest request) {
            model(request.model);
            prompt(request.prompt);
            suffix(request.suffix);
            maxTokens(request.maxTokens);
            temperature(request.temperature);
            topP(request.topP);
            n(request.n);
            stream(request.stream);
            logprobs(request.logprobs);
            echo(request.echo);
            stop(request.stop);
            presencePenalty(request.presencePenalty);
            frequencyPenalty(request.frequencyPenalty);
            bestOf(request.bestOf);
            logitBias(request.logitBias);
            user(request.user);
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

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
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

        public Builder logprobs(Integer logprobs) {
            this.logprobs = logprobs;
            return this;
        }

        public Builder echo(Boolean echo) {
            this.echo = echo;
            return this;
        }

        public Builder stop(List<String> stop) {
            if (stop == null) {
                return this;
            }
            this.stop = unmodifiableList(stop);
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

        public Builder bestOf(Integer bestOf) {
            this.bestOf = bestOf;
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

        public CompletionRequest build() {
            return new CompletionRequest(this);
        }
    }
}
