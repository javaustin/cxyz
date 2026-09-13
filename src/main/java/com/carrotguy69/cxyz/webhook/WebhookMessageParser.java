package com.carrotguy69.cxyz.webhook;

import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebhookMessageParser {

    public static DiscordWebhook createWebhook(ConfigurationSection section, String fallbackWebhookURL, Map<String, Object> placeholderValues) {
        if (section == null) {
            return null;
        }

        String webhookURL = section.getString("webhook-url");

        if (webhookURL == null) {
            if (fallbackWebhookURL == null || fallbackWebhookURL.isBlank()) {
                return null;
            }

            else
                webhookURL = fallbackWebhookURL;
        }

        String editMessageURL = section.getString("message-url");
        boolean editMode = ObjectUtils.parseCasualBoolean(section.getString("edit-mode"), false) && editMessageURL != null;

        String content = section.getString("content", "");

        content = MessageUtils.formatPlaceholders(content, placeholderValues);

        List<Map<?, ?>> embedMapList = section.getMapList("embeds");
        List<DiscordEmbed> embeds = new ArrayList<>();

        for (Map<?, ?> entry : embedMapList) {

            String title = entry.get("title") != null ? (String) entry.get("title") : null;
            String desc = entry.get("description") != null ? (String) entry.get("description") : null;
            int color = entry.get("color") != null || !(entry.get("color") instanceof Integer) ? (int) entry.get("color") : 0;

            if (title != null) {
                title = MessageUtils.formatPlaceholders(title, placeholderValues);
            }

            if (desc != null)
                desc = MessageUtils.formatPlaceholders(desc, placeholderValues);

            color = Integer.parseInt(MessageUtils.formatPlaceholders(String.valueOf(color), placeholderValues));


            Object fieldsObj = entry.get("fields");
            List<DiscordEmbed.Field> fields = new ArrayList<>();

            if (fieldsObj instanceof List<?>) {
                try {
                    List<Map<?, ?>> listObj = (List<Map<?, ?>>) fieldsObj;

                    for (Map<?, ?> fieldEntry : listObj) {
                        String name = fieldEntry.get("name") != null ? (String) fieldEntry.get("name") : "";
                        String value = fieldEntry.get("value") != null ? (String) fieldEntry.get("value") : "";
                        boolean inline = fieldEntry.get("value") != null && ObjectUtils.parseCasualBoolean((String) fieldEntry.get("value"));

                        name = MessageUtils.formatPlaceholders(name, placeholderValues);
                        value = MessageUtils.formatPlaceholders(value, placeholderValues);

                        DiscordEmbed.Field field = new DiscordEmbed.Field(name, value, inline);
                        fields.add(field);
                    }
                }
                catch (ClassCastException ignore) {}
            }

            String titleURL = entry.get("title-url") != null ? (String) entry.get("title-url") : null;
            String thumbnailURL = entry.get("thumbnail-url") != null ? (String) entry.get("thumbnail-url") : null;
            String imageURL = entry.get("image-url") != null ? (String) entry.get("image-url") : null;

            String timestamp = entry.get("timestamp") != null && entry.get("timestamp") instanceof String ? (String) entry.get("timestamp") : entry.get("timestamp") instanceof Long ? String.valueOf((long) entry.get("timestamp")) : null;

            if (timestamp == null || timestamp.isBlank()) {
                timestamp = null;
            }

            String authorName = null;
            String authorURL = null;
            String authorIconURL = null;

            try {
                Map<String, String> authorMap = (Map<String, String>) entry.get("author");

                authorName = authorMap.get("name") != null ? authorMap.get("name") : null;
                authorURL = authorMap.get("url") != null ? authorMap.get("url") : null;
                authorIconURL = authorMap.get("icon-url") != null ? authorMap.get("icon-url") : null;
            }
            catch (ClassCastException ignoreIt) {}

            if (authorName != null) {
                authorName = MessageUtils.formatPlaceholders(authorName, placeholderValues);
            }

            DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorIconURL);

            String footerText = null;
            String footerIconURL = null;

            try {
                Map<String, String> footerMap = (Map<String, String>) entry.get("footer");

                footerText = footerMap.get("text") != null ? footerMap.get("text") : null;
                footerIconURL = footerMap.get("icon-url") != null ? footerMap.get("icon-url") : null;
            }
            catch (ClassCastException ignoreIt) {}

            if (footerText != null) {
                footerText = MessageUtils.formatPlaceholders(footerText, placeholderValues);
            }

            DiscordEmbed.Footer footer = new DiscordEmbed.Footer(footerText, footerIconURL);

            DiscordEmbed embed = new DiscordEmbed().create(title, desc, color);

            if (!fields.isEmpty()) {
                embed.setFields(fields);
            }

            if (titleURL != null) {
                embed.setTitleURL(titleURL);
            }

            if (thumbnailURL != null) {
                embed.setThumbnailURL(thumbnailURL);
            }

            if (imageURL != null) {
                embed.setImageURL(imageURL);
            }

            if (timestamp != null) {
                embed.setTimestamp(timestamp);
            }

            // Author will be null (by the embed class) if fields are null.
            embed.setAuthor(author);

            // ^ Same with footer
            embed.setFooter(footer);

            embeds.add(embed);
        }

        return new DiscordWebhook(webhookURL, content, embeds, editMode, editMessageURL);
    }

}