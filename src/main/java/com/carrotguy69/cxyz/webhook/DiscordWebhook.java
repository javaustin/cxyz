package com.carrotguy69.cxyz.webhook;
import com.carrotguy69.cxyz.http.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.gson;

public class DiscordWebhook {
    private String url;
    private String content;
    private final List<DiscordEmbed> embeds = new ArrayList<>();

    // Attachments are not supported

    public DiscordWebhook setContent(String content) {
        this.content = content;

        return this;
    }

    public DiscordWebhook setURL(String url) {
        this.url = url;

        return this;
    }

    public DiscordWebhook addEmbed(DiscordEmbed embed) {
        this.embeds.add(embed);

        return this;
    }

    public void send() {

        Map<String, Object> postMap = new HashMap<>();
        List<Map<String, Object>> embedMap = new ArrayList<>();

        for (DiscordEmbed embed : embeds) {
            Map<String, Object> embedEntry = new HashMap<>();

            embedEntry.put("title", embed.getTitle());
            embedEntry.put("description", embed.getDescription());
            embedEntry.put("color", embed.getColor());

            if (embed.getTitleURL() != null) {
                embedEntry.put("url", embed.getTitleURL());
            }

            if (embed.getAuthor() != null || embed.getAuthor().getName() == null) {
                Map<String, String> authorEntry = new HashMap<>();
                authorEntry.put("name", embed.getAuthor().getName());

                if (embed.getAuthor().getIconURL() != null) {
                    authorEntry.put("icon_url", embed.getAuthor().getIconURL());
                }
                if (embed.getAuthor().getURL() != null) {
                    authorEntry.put("url", embed.getAuthor().getURL());
                }

                embedEntry.put("author", authorEntry);
            }

            List<Map<String, Object>> fields = new ArrayList<>();
            for (DiscordEmbed.Field field : embed.getFields()) {
                Map<String, Object> fieldEntry = new HashMap<>();

                if (field.getName() == null && field.getValue() == null) {
                    continue;
                }

                fieldEntry.put("name", field.getName());
                fieldEntry.put("value", field.getValue());
                fieldEntry.put("inline", field.isInline());

                fields.add(fieldEntry);
            }

            embedEntry.put("fields", fields);

            if (embed.getFooter() != null) {
                Map<String, String> footerObject = new HashMap<>();
                if (embed.getFooter().getText() != null) {
                    footerObject.put("text", embed.getFooter().getText());

                    if (embed.getFooter().getIconURL() != null) {
                        footerObject.put("icon_url", embed.getFooter().getIconURL());
                    }
                }

                embedEntry.put("footer", footerObject);
            }

            if (embed.getTimestamp() != null) {
                embedEntry.put("timestamp", embed.getTimestamp());
            }

            if (embed.getImageURL() != null) {
                embedEntry.put("url", embed.getImageURL());
            }

            if (embed.getThumbnailURL() != null) {
                embedEntry.put("url", embed.getThumbnailURL());
            }

            embedMap.add(embedEntry);
        }

        postMap.put("content", content);
        postMap.put("embeds", embedMap);
        postMap.put("attachments", new ArrayList<>());


        Request.postRequest(url, gson.toJson(postMap));
    }
}