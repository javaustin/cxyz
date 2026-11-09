package com.carrotguy69.cxyz.models.config.cooldowns;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChatCooldownRule {

    private final int cooldown;
    private final List<PlayerRank> affectedRanks;
    private final List<PlayerRank> exemptRanks;
    private final List<BaseChannel> affectedChannels;
    private final List<BaseChannel> exemptChannels;

    public ChatCooldownRule(int cooldown, List<PlayerRank> affectedRanks, List<PlayerRank> exemptRanks, List<BaseChannel> affectedChannels, List<BaseChannel> exemptChannels) {
        this.cooldown = cooldown;
        this.affectedRanks = affectedRanks;
        this.exemptRanks = exemptRanks;
        this.affectedChannels = affectedChannels;
        this.exemptChannels = exemptChannels;
    }

    public int getCooldown() {
        return cooldown;
    }

    public List<PlayerRank> getAffectedRanks() {
        return affectedRanks;
    }

    public List<PlayerRank> getExemptRanks() {
        return exemptRanks;
    }

    public List<BaseChannel> getAffectedChannels() {
        return affectedChannels;
    }

    public List<BaseChannel> getExemptChannels() {
        return exemptChannels;
    }

    public boolean appliesTo(NetworkPlayer player, BaseChannel channel) {
        boolean result = false;

        for (PlayerRank rank : player.getRanks()) {
            if (affectedRanks.contains(rank)) {
                result = true;
            }

            if (exemptRanks.contains(rank)) {
                return false;
            }
        }

        return result && affectedChannels.contains(channel) && !exemptChannels.contains(channel);
    }

    public static List<ChatCooldownRule> getGlobalRules() {

        List<ChatCooldownRule> results = new ArrayList<>();

//        try {
            for (Map<?, ?> map : CXYZ.configYaml.getMapList("chat-cooldowns")) {
                int ruleCooldown = ((Number) map.get("cooldown")).intValue();

                List<String> ruleAffectedRanks = (List<String>) map.get("affected-ranks");
                List<String> ruleExemptRanks = (List<String>) map.get("exempt-ranks");
                List<String> ruleAffectedChannels = (List<String>) map.get("affected-channels");
                List<String> ruleExemptChannels = (List<String>) map.get("exempt-channels");

                if (ruleAffectedRanks == null)
                    ruleAffectedRanks = List.of();

                if (ruleExemptRanks == null)
                    ruleExemptRanks = List.of();

                if (ruleAffectedChannels == null)
                    ruleAffectedChannels = List.of();

                if (ruleExemptChannels == null)
                    ruleExemptChannels = List.of();

                List<PlayerRank> affectedRanks = new ArrayList<>();

                for (String rankName : ruleAffectedRanks) {
                    PlayerRank rank = PlayerRank.getRankByName(rankName);
                    affectedRanks.add(rank);
                }

                List<PlayerRank> exemptRanks = new ArrayList<>();

                for (String rankName : ruleExemptRanks) {
                    PlayerRank rank = PlayerRank.getRankByName(rankName);
                    exemptRanks.add(rank);
                }

                List<BaseChannel> affectedChannels = new ArrayList<>();

                for (String channelName : ruleAffectedChannels) {
                    BaseChannel channel = BaseChannel.getChannel(channelName);
                    affectedChannels.add(channel);
                }

                List<BaseChannel> exemptChannels = new ArrayList<>();

                for (String channelName : ruleExemptChannels) {
                    BaseChannel channel = BaseChannel.getChannel(channelName);
                    exemptChannels.add(channel);
                }

                if (affectedRanks.isEmpty()) {
                    // If affected-ranks is empty, it is assumed all ranks are affected by the cooldown rule. (exempt-ranks will still remain excluded.)
                    affectedRanks.addAll(PlayerRank.getRanks());
                }

                if (affectedChannels.isEmpty()) {
                    // If affected-channels is empty, it is assumed all channels are affected by the cooldown rule. (exempt-channels will still remain excluded.)
                    affectedChannels.addAll(BaseChannel.getAllChannels());
                }

                ChatCooldownRule rule = new ChatCooldownRule(ruleCooldown, affectedRanks, exemptRanks, affectedChannels, exemptChannels);

                results.add(rule);
            }
//        }
//        catch (RuntimeException e) {
//            throw new InvalidConfigException("config.yml", "chat-cooldowns", "Could not load chat cooldowns: Path not found!");
//        }

        return results;
    }

}
