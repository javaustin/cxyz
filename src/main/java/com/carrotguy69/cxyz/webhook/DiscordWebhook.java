package com.carrotguy69.cxyz.webhook;

import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.gson;

public class DiscordWebhook {
    private final String url;
    private final String content;
    private final List<DiscordEmbed> embeds;

    private final boolean editMode;
    private final String messageURL;

    public DiscordWebhook(String webhookURL, String content, List<DiscordEmbed> embeds, boolean editMode, String editMessageURL) {
        this.url = webhookURL;
        this.content = content;
        this.embeds = embeds;
        this.editMode = editMode;
        this.messageURL = editMessageURL;
    }

    public DiscordWebhook(String webhookURL, String content, List<DiscordEmbed> embeds) {
        this.url = webhookURL;
        this.content = content;
        this.embeds = embeds;
        this.editMode = false;
        this.messageURL = null;
    }

    public DiscordWebhook(String webhookURL, String content) {
        this.url = webhookURL;
        this.content = content;
        this.embeds = new ArrayList<>();
        this.editMode = false;
        this.messageURL = null;
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

            if (embed.getAuthor() != null && embed.getAuthor().getName() != null) {
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

            embedEntry.put("timestamp", TimeUtils.unixTimeToTimestamp(embed.getTimestamp()));


            if (embed.getImageURL() != null) {
                embedEntry.put("image", Map.of("url", embed.getImageURL()));
            }

            if (embed.getThumbnailURL() != null) {
                embedEntry.put("thumbnail", Map.of("url", embed.getThumbnailURL()));
            }

            embedMap.add(embedEntry);
        }

        postMap.put("content", content);
        postMap.put("embeds", embedMap);
        postMap.put("attachments", new ArrayList<>());

        String body = gson.toJson(postMap);


        if (!editMode || messageURL == null) {
            Request.postRequest(url, body);
            return;
        }

        String messageID = messageURL.substring(messageURL.lastIndexOf("/") + 1);

        Request.patchRequest(url + "/messages/" + messageID, body);
    }
}