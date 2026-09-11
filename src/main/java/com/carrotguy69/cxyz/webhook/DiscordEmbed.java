package com.carrotguy69.cxyz.webhook;

import java.util.ArrayList;
import java.util.List;

public class DiscordEmbed {
    private String title;
    private String description;
    private int color;

    private Footer footer = null;
    private List<Field> fields = new ArrayList<>();
    private String titleURL = null;
    private String thumbnailURL = null;
    private String imageURL = null;

    private String timestamp;

    private Author author;

    public static class Author {
        private final String name;
        private final String url;
        private final String iconURL;

        public Author(String name, String url, String iconURL) {
            this.name = name;
            this.url = url;
            this.iconURL = iconURL;
        }

        public String getName() {
            return name;
        }

        public String getURL() {
            return url;
        }

        public String getIconURL() {
            return iconURL;
        }
    }

    public static class Footer {
        public Footer(String text, String iconURL) {
            this.text = text;
            this.iconURL = iconURL;
        }

        private final String text;
        private final String iconURL;

        public String getText() {
            return text;
        }

        public String getIconURL() {
            return iconURL;
        }
    }

    public static class Field {
        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        private final String name;
        private final String value;
        private final boolean inline;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isInline() {
            return inline;
        }
    }

    public DiscordEmbed setTitle(String title) {
        this.title = title;

        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public DiscordEmbed setDescription(String description) {
        this.description = description;

        return this;
    }

    public DiscordEmbed setColor(int color) {
        this.color = color;

        return this;
    }

    private DiscordEmbed setTimestamp(String timestamp) {
        this.timestamp = timestamp;

        return this;
    }

    public DiscordEmbed setFooter(Footer footer) {
        this.footer = footer;

        return this;
    }

    public DiscordEmbed setFields(List<Field> fields) {
        this.fields = fields;

        return this;
    }

    public DiscordEmbed setTitleURL(String titleURL) {
        this.titleURL = titleURL;

        return this;
    }

    public DiscordEmbed setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;

        return this;
    }

    public DiscordEmbed setImageURL(String imageURL) {
        this.imageURL = imageURL;

        return this;
    }

    public DiscordEmbed setAuthor(Author author) {
        this.author = author;

        return this;
    }

    public DiscordEmbed create(String title, String description, int color) {
        this.title = title;
        this.description = description;
        this.color = color;

        return this;
    }

    public Footer getFooter() {
        return footer;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitleURL() {
        return titleURL;
    }

    public Author getAuthor() {
        return author;
    }
}
