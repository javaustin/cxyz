package com.carrotguy69.cxyz.webhook;

import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebhookMessageParser {

    public DiscordWebhook fromYML(ConfigurationSection section) {
        String webhookURL = section.getString("webhook-url");

        if (webhookURL == null) {
            return null;
        }

        String editMessageURL = section.getString("message-url");
        boolean editMode = ObjectUtils.parseCasualBoolean(section.getString("edit-mode"), false) && editMessageURL != null;

        String content = section.getString("content", "");

        List<Map<?, ?>> embedMapList = section.getMapList("embeds");
        List<DiscordEmbed> embeds = new ArrayList<>();

        for (Map<?, ?> entry : embedMapList) {
            String title = entry.get("title") != null ? (String) entry.get("title") : null;
            String desc = entry.get("description") != null ? (String) entry.get("description") : null;
            int color = entry.get("color") != null ? (int) entry.get("color") : 0;

            Object fieldsObj = entry.get("fields");
            List<DiscordEmbed.Field> fields = new ArrayList<>();

            if (fieldsObj instanceof List<?>) {
                try {
                    List<Map<?, ?>> listObj = (List<Map<?, ?>>) fieldsObj;

                    for (Map<?, ?> fieldEntry : listObj) {
                        String name = fieldEntry.get("name") != null ? (String) fieldEntry.get("name") : "";
                        String value = fieldEntry.get("value") != null ? (String) fieldEntry.get("value") : "";
                        boolean inline = fieldEntry.get("value") != null && ObjectUtils.parseCasualBoolean((String) fieldEntry.get("value"));

                        DiscordEmbed.Field field = new DiscordEmbed.Field(name, value, inline);
                        fields.add(field);
                    }
                }
                catch (ClassCastException ignore) {}
            }

            String titleURL = entry.get("title-url") != null ? (String) entry.get("title-url") : null;
            String thumbnailURL = entry.get("thumbnail-url") != null ? (String) entry.get("thumbnail-url") : null;
            String imageURL = entry.get("image-url") != null ? (String) entry.get("image-url") : null;

            String timestamp = entry.get("timestamp") != null ? (String) entry.get("timestamp") : null;


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

            DiscordEmbed.Author author = new DiscordEmbed.Author(authorName, authorURL, authorIconURL);

            String footerText = null;
            String footerIconURL = null;

            try {
                Map<String, String> footerMap = (Map<String, String>) entry.get("footer");

                footerText = footerMap.get("text") != null ? footerMap.get("text") : null;
                footerIconURL = footerMap.get("icon-url") != null ? footerMap.get("icon-url") : null;
            }
            catch (ClassCastException ignoreIt) {}

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
/*
# An example webhook in YAML:

a-webhook:
  webhook-url: "https://discordapp.com/api/webhooks/1547866577859518596/rTlUCshZi6kHEDR1E5_GdVajNXTYPsFszhwicv8ZFySDOAMknDlEvGc6bLRl5EFat-jN"

  content: "Hello World"
  embeds:
    - title: "Hi everyone"
      description: "This is my description"
      color: 988888

      fields:
        - name: f
          value: v1
          inline: true

        - name: f2
          value: v2
          inline: true

        - name: f3
          value: v3
          inline: true

      title-url: "https://example.com/title-url"
      thumbnail-url: "https://example.com/thumbnail-url"
      image-url: "https://example.com/image-url"

      timestamp: 0 # Accept integer, formal timestamp, or the text "now"

      author:
        name: "Author"
        url: "https://example.com/author-url"
        icon-url: "https://example.com/author-icon-url"

      footer:
        text: "Footer"
        icon-url: "https://example.com/footer-icon-url"

  edit-mode: true
  message-url: "https://discordapp.com/channels/1238973352144666737/1241447347880333342/1547888922665816097"

 */