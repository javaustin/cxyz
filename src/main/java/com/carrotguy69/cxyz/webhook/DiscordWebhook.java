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

            embedMap.add(embedEntry);
        }

        postMap.put("content", content);
        postMap.put("embeds", embedMap);
        postMap.put("attachments", new ArrayList<>());


        Request.postRequest(url, gson.toJson(postMap));
    }
}