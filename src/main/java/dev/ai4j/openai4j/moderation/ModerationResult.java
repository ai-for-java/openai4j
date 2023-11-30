package dev.ai4j.openai4j.moderation;

import java.util.Objects;

public final class ModerationResult {

    private final Categories categories;
    private final CategoryScores categoryScores;
    private final Boolean flagged;

    private ModerationResult(Builder builder) {
        this.categories = builder.categories;
        this.categoryScores = builder.categoryScores;
        this.flagged = builder.flagged;
    }

    public Categories categories() {
        return categories;
    }

    public CategoryScores categoryScores() {
        return categoryScores;
    }

    public Boolean isFlagged() {
        return flagged;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ModerationResult
                && equalTo((ModerationResult) another);
    }

    private boolean equalTo(ModerationResult another) {
        return Objects.equals(categories, another.categories)
                && Objects.equals(categoryScores, another.categoryScores)
                && Objects.equals(flagged, another.flagged);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(categories);
        h += (h << 5) + Objects.hashCode(categoryScores);
        h += (h << 5) + Objects.hashCode(flagged);
        return h;
    }

    @Override
    public String toString() {
        return "ModerationResult{"
                + "categories=" + categories
                + ", categoryScores=" + categoryScores
                + ", flagged=" + flagged
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Categories categories;
        private CategoryScores categoryScores;
        private Boolean flagged;

        private Builder() {
        }

        public Builder categories(Categories categories) {
            this.categories = categories;
            return this;
        }

        public Builder categoryScores(CategoryScores categoryScores) {
            this.categoryScores = categoryScores;
            return this;
        }

        public Builder flagged(Boolean flagged) {
            this.flagged = flagged;
            return this;
        }

        public ModerationResult build() {
            return new ModerationResult(this);
        }
    }
}
