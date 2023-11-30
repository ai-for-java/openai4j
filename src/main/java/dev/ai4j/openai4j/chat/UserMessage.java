package dev.ai4j.openai4j.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.ai4j.openai4j.chat.ContentType.IMAGE_URL;
import static dev.ai4j.openai4j.chat.ContentType.TEXT;
import static dev.ai4j.openai4j.chat.Role.USER;
import static java.util.Collections.unmodifiableList;

public final class UserMessage implements Message {

    private final Role role = USER;
    private final List<Content> content;
    private final String name;

    private UserMessage(Builder builder) {
        this.content = builder.content;
        this.name = builder.name;
    }

    public Role role() {
        return role;
    }

    public List<Content> content() {
        return content;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof UserMessage
                && equalTo((UserMessage) another);
    }

    private boolean equalTo(UserMessage another) {
        return Objects.equals(role, another.role)
                && Objects.equals(content, another.content)
                && Objects.equals(name, another.name);
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(role);
        h += (h << 5) + Objects.hashCode(content);
        h += (h << 5) + Objects.hashCode(name);
        return h;
    }

    @Override
    public String toString() {
        return "UserMessage{"
                + "role=" + role
                + ", content=" + content
                + ", name=" + name
                + "}";
    }

    public static UserMessage from(String text, String... imageUrls) {
        return UserMessage.builder()
                .addText(text)
                .addImageUrls(imageUrls)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private List<Content> content;
        private String name;

        private Builder() {
        }

        public Builder addText(String text) {
            if (this.content == null) {
                this.content = new ArrayList<>();
            }
            Content content = Content.builder()
                    .type(TEXT)
                    .text(text)
                    .build();
            this.content.add(content);
            return this;
        }

        public Builder addImageUrl(String imageUrl) {
            return addImageUrl(imageUrl, null);
        }

        public Builder addImageUrl(String imageUrl, ImageDetail imageDetail) {
            if (this.content == null) {
                this.content = new ArrayList<>();
            }
            Content content = Content.builder()
                    .type(IMAGE_URL)
                    .imageUrl(ImageUrl.builder()
                            .url(imageUrl)
                            .detail(imageDetail)
                            .build())
                    .build();
            this.content.add(content);
            return this;
        }

        public Builder addImageUrls(String... imageUrls) {
            for (String imageUrl : imageUrls) {
                addImageUrl(imageUrl);
            }
            return this;
        }

        public Builder content(List<Content> content) {
            if (content != null) {
                this.content = unmodifiableList(content);
            }
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public UserMessage build() {
            return new UserMessage(this);
        }
    }
}
