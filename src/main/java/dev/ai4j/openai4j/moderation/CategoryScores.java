package dev.ai4j.openai4j.moderation;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public final class CategoryScores {

    private final Double hate;

    @SerializedName("hate/threatening")
    private final Double hateThreatening;

    @SerializedName("self-harm")
    private final Double selfHarm;

    private final Double sexual;

    @SerializedName("sexual/minors")
    private final Double sexualMinors;

    private final Double violence;

    @SerializedName("violence/graphic")
    private final Double violenceGraphic;

    private CategoryScores(Builder builder) {
        this.hate = builder.hate;
        this.hateThreatening = builder.hateThreatening;
        this.selfHarm = builder.selfHarm;
        this.sexual = builder.sexual;
        this.sexualMinors = builder.sexualMinors;
        this.violence = builder.violence;
        this.violenceGraphic = builder.violenceGraphic;
    }

    public Double hate() {
        return hate;
    }

    public Double hateThreatening() {
        return hateThreatening;
    }

    public Double selfHarm() {
        return selfHarm;
    }

    public Double sexual() {
        return sexual;
    }

    public Double sexualMinors() {
        return sexualMinors;
    }

    public Double violence() {
        return violence;
    }

    public Double violenceGraphic() {
        return violenceGraphic;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof CategoryScores
                && equalTo((CategoryScores) another);
    }

    private boolean equalTo(CategoryScores another) {
        return Objects.equals(hate, another.hate)
                && Objects.equals(hateThreatening, another.hateThreatening)
                && Objects.equals(selfHarm, another.selfHarm)
                && Objects.equals(sexual, another.sexual)
                && Objects.equals(sexualMinors, another.sexualMinors)
                && Objects.equals(violence, another.violence)
                && Objects.equals(violenceGraphic, another.violenceGraphic);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(hate);
        h += (h << 5) + Objects.hashCode(hateThreatening);
        h += (h << 5) + Objects.hashCode(selfHarm);
        h += (h << 5) + Objects.hashCode(sexual);
        h += (h << 5) + Objects.hashCode(sexualMinors);
        h += (h << 5) + Objects.hashCode(violence);
        h += (h << 5) + Objects.hashCode(violenceGraphic);
        return h;
    }

    @Override
    public String toString() {
        return "CategoryScores{"
                + "hate=" + hate
                + ", hateThreatening=" + hateThreatening
                + ", selfHarm=" + selfHarm
                + ", sexual=" + sexual
                + ", sexualMinors=" + sexualMinors
                + ", violence=" + violence
                + ", violenceGraphic=" + violenceGraphic
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Double hate;
        private Double hateThreatening;
        private Double selfHarm;
        private Double sexual;
        private Double sexualMinors;
        private Double violence;
        private Double violenceGraphic;

        private Builder() {
        }

        public Builder hate(Double hate) {
            this.hate = hate;
            return this;
        }

        public Builder hateThreatening(Double hateThreatening) {
            this.hateThreatening = hateThreatening;
            return this;
        }

        public Builder selfHarm(Double selfHarm) {
            this.selfHarm = selfHarm;
            return this;
        }

        public Builder sexual(Double sexual) {
            this.sexual = sexual;
            return this;
        }

        public Builder sexualMinors(Double sexualMinors) {
            this.sexualMinors = sexualMinors;
            return this;
        }

        public Builder violence(Double violence) {
            this.violence = violence;
            return this;
        }

        public Builder violenceGraphic(Double violenceGraphic) {
            this.violenceGraphic = violenceGraphic;
            return this;
        }

        public CategoryScores build() {
            return new CategoryScores(this);
        }
    }
}