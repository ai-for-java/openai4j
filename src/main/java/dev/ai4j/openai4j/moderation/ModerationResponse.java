package dev.ai4j.openai4j.moderation;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

public final class ModerationResponse {

    private final String id;
    private final String model;
    private final List<ModerationResult> results;

    private ModerationResponse(Builder builder) {
        this.id = builder.id;
        this.model = builder.model;
        this.results = builder.results;
    }

    public String id() {
        return id;
    }

    public String model() {
        return model;
    }

    public List<ModerationResult> results() {
        return results;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ModerationResponse
                && equalTo((ModerationResponse) another);
    }

    private boolean equalTo(ModerationResponse another) {
        return Objects.equals(id, another.id)
                && Objects.equals(model, another.model)
                && Objects.equals(results, another.results);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(id);
        h += (h << 5) + Objects.hashCode(model);
        h += (h << 5) + Objects.hashCode(results);
        return h;
    }

    @Override
    public String toString() {
        return "ModerationResponse{"
                + "id=" + id
                + ", model=" + model
                + ", results=" + results
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        public String id;
        public String model;
        public List<ModerationResult> results;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder results(List<ModerationResult> results) {
            if (results != null) {
                this.results = unmodifiableList(results);
            }
            return this;
        }

        public ModerationResponse build() {
            return new ModerationResponse(this);
        }
    }
}
