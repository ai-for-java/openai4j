package dev.ai4j.openai4j.chat;

import java.util.Objects;

public final class StreamOptions {

    private final Boolean includeUsage;

    public StreamOptions(Boolean includeUsage) {
        this.includeUsage = includeUsage;
    }

    public Boolean includeUsage() {
        return includeUsage;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof StreamOptions
                && equalTo((StreamOptions) another);
    }

    private boolean equalTo(StreamOptions another) {
        return Objects.equals(includeUsage, another.includeUsage);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(includeUsage);
        return h;
    }

    @Override
    public String toString() {
        return "StreamOptions{" +
                "includeUsage=" + includeUsage +
                "}";
    }
}
