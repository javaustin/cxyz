package com.carrotguy69.cxyz.other.webhook;
import com.carrotguy69.cxyz.http.Request;

import java.util.ArrayList;
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

        List<Map<String, Object>> embedMap = new ArrayList<>();

        for (DiscordEmbed embed : embeds) {
            embedMap.add(Map.of(
                    "title", embed.getTitle(),
                    "description", embed.getDescription(),
                    "color", embed.getColor()
            ));
        }


        String postData = gson.toJson(
                Map.of(
                "content", content,
                "embeds", embedMap,
                "attachments", new ArrayList<>()
                )
        );

        Request.postRequest(url, postData);
    }
}