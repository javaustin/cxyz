package com.carrotguy69.cxyz.models.config;

import com.carrotguy69.cxyz.other.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class PlayerRank {
    private final String name;
    private final String prefix;
    private final String color;
    private final int hierarchy;
    private final int chatCooldown;
    private final String defaultChatColor;

    public PlayerRank (String name, String prefix, String color, int hierarchy, int chatCooldown, String defaultChatColor) {
        this.name = name.toLowerCase();
        this.prefix = prefix;
        this.color = color;
        this.hierarchy = hierarchy;
        this.chatCooldown = chatCooldown;
        this.defaultChatColor = defaultChatColor;
    }

    public static List<PlayerRank> loadRanks() {
        ConfigurationSection section = configYaml.getConfigurationSection("ranks");

        List<PlayerRank> results = new ArrayList<>();

        if (section != null) {
            for (String rank : section.getKeys(false)) { // Should get all rank names (default, vip, ...)
                ConfigurationSection rankData = section.getConfigurationSection(rank);

                if (rankData != null) {
                    String prefix = rankData.getString("prefix", "");
                    String color = rankData.getString("color", "&f");
                    String defaultChatColor = rankData.getString("default-chat-color", "&7");
                    int chatCooldown = rankData.getInt("chat-cooldown", 3);
                    int hierarchy = rankData.getInt("hierarchy", 0);

                    PlayerRank r = new PlayerRank(rank, prefix, color, hierarchy, chatCooldown, defaultChatColor);

                    results.add(r);

                    if (rank.equalsIgnoreCase("default")) {
                        defaultRank = r;
                    }
                }
            }
        }

        if (results.isEmpty()) {
            PlayerRank fallback = new PlayerRank("default", "", "&f", 0, 3, "&7");
            results.add(fallback);
            defaultRank = fallback;

            Logger.warning("No ranks specified in config.yml, creating default!");
        }

        return results;
    }

    @Override
    public boolean equals(Object otherRank) {
        PlayerRank rank = null;

        if (otherRank instanceof PlayerRank) {
            rank = (PlayerRank) otherRank;
        }

        if (rank == null) {
            return false;
        }


        return Objects.equals(this.getName(), rank.getName());
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getColor() {
        return color;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public int getPosition() {
        return hierarchy;
    }

    public int getChatCooldown() {
        return chatCooldown;
    }

    public String getDefaultChatColor() {
        return defaultChatColor;
    }

    @Override
    public String toString() {
        return "PlayerRank{" +
                "name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", color='" + color + '\'' +
                ", hierarchy=" + hierarchy +
                ", chatCooldown=" + chatCooldown +
                ", defaultChatColor='" + defaultChatColor + '\'' +
                '}';
    }

    public static PlayerRank getRankByName(String rankName) {
        for (PlayerRank rank : ranks) {
            if (Objects.equals(rank.getName(), rankName.toLowerCase())) {
                return rank;
            }
        }

        throw new IllegalArgumentException(String.format("Provided rank (%s) does not exist in the config.yml file!", rankName));
    }


    public static boolean compareRank(PlayerRank greater, PlayerRank lesser) {
        return greater.getHierarchy() > lesser.getHierarchy();
    }

    public static boolean rankExists(String rankName) {
        for (PlayerRank rank : ranks) {
            if (Objects.equals(rank.getName(), rankName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }


    public static boolean contains(List<PlayerRank> collection, PlayerRank object) {
        for (PlayerRank element : collection) {
            if (element.equals(object)) {
                return true;
            }
        }

        return false;
    }

}
