package com.carrotguy69.cxyz.models.config;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.other.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

import static com.carrotguy69.cxyz.CXYZ.configYaml;

public class ChatFilterRule {
    private final String name;
    private final List<String> enabledChannels;
    private final List<String> blacklistedWords;
    private final List<String> actions;


    public ChatFilterRule(String name, List<String> enabledChannels, List<String> blacklistedWords, List<String> actions) {
        this.name = name;
        this.enabledChannels = enabledChannels;
        this.blacklistedWords = blacklistedWords;
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public List<String> getEnabledChannels() {
        return enabledChannels;
    }

    public List<String> getBlacklistedWords() {
        return blacklistedWords;
    }

    public List<String> getActions() {
        return actions;
    }

    public static List<ChatFilterRule> getChatFilterRules() {
        ConfigurationSection section = configYaml.getConfigurationSection("chat.chat-filter.rules");

        if (section == null) {
            Logger.warning("No chat filter rules found in config.yml! Ignoring...");
            return List.of();
        }

        List<ChatFilterRule> results = new ArrayList<>();

        for (String name : section.getKeys(false)) {

            List<String> enabledChannels = section.getStringList(name + ".enabled-channels");
            List<String> blacklistedWords = section.getStringList(name + ".blacklisted-words");
            List<String> actions = section.getStringList(name + ".actions");

            results.add(new ChatFilterRule(name, enabledChannels, blacklistedWords, actions));
        }

        return results;
    }

    public static ChatFilterRule getByName(String name) {
        for (ChatFilterRule cfr : getChatFilterRules()) {
            if (cfr.getName().equalsIgnoreCase(name)) {
                return cfr;
            }
        }

        return null;
    }

    public static List<ChatFilterRule> getRulesForChannel(BaseChannel ch) {
        return getRulesForChannel(ch.getName());
    }

    public static List<ChatFilterRule> getRulesForChannel(String channelName) {
        List<ChatFilterRule> results = new ArrayList<>();

        for (ChatFilterRule cfr : getChatFilterRules()) {
            if (cfr.getEnabledChannels().contains(channelName)) {
                results.add(cfr);
            }
        }

        return results;
    }

    public String toString() {
        return "ChatFilterRule{" +
                "name='" + name + '\'' +
                ", enabledChannels=" + enabledChannels +
                ", blacklistedWords=" + blacklistedWords +
                ", actions=" + actions +
                '}';

    }




}
