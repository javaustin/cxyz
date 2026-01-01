package com.carrotguy69.cxyz.other.webhook;

public class DiscordEmbed {
    private String title;
    private String description;
    private int color;

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

    public DiscordEmbed create(String title, String description, int color) {
        this.title = title;
        this.description = description;
        this.color = color;

        return this;
    }
}
