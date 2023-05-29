package dev.ai4j.openai4j.chat;

public final class Delta {

    private final Role role;
    private final String content;

    private Delta(Builder builder) {
        this.role = builder.role;
        this.content = builder.content;
    }

    public Role role() {
        return role;
    }

    public String content() {
        return content;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof Delta
                && equalTo((Delta) another);
    }

    private boolean equalTo(Delta another) {
        return role.equals(another.role)
                && content.equals(another.content);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + role.hashCode();
        h += (h << 5) + content.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return "Delta{"
                + "role=" + role
                + ", content=" + content
                + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Role role;
        private String content;

        private Builder() {
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Delta build() {
            return new Delta(this);
        }
    }
}
