package com.carrotguy69.cxyz.models.config;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandNode;


public class CommandCooldownRule {
    public enum Scope {
        CMD,
        ALL
    }

    private final @Nullable String name;
    private final int cooldown;
    private final List<PlayerRank> affectedRanks;
    private final List<PlayerRank> exemptRanks;
    private final Scope scope;

    public CommandCooldownRule(@Nullable String name, int cooldown, List<PlayerRank> affectedRanks, List<PlayerRank> exemptRanks, Scope scope) {
        this.name = name;
        this.cooldown = cooldown;
        this.affectedRanks = affectedRanks;
        this.exemptRanks = exemptRanks;
        this.scope = scope;
    }

    public @Nullable String getName() {
        return name;
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

    public Scope getScope() {
        return scope;
    }

    public boolean appliesTo(NetworkPlayer player, String commandNode) {
        if (name != null && !name.equals(CommandNode.path(commandNode))) {
            return false;
        }

        boolean result = false;

        for (PlayerRank rank : player.getRanks()) {
            if (affectedRanks.contains(rank)) {
                result = true;
            }

            if (exemptRanks.contains(rank)) {
                return false;
            }
        }

        return result;
    }

    public static List<CommandCooldownRule> getGlobalRules() {
        List<CommandCooldownRule> results = new ArrayList<>();

        ConfigurationSection section = CXYZ.configYaml.getConfigurationSection("cooldowns");

        if (section == null) {
            throw new InvalidConfigException("config.yml", "cooldowns", "Could not load command cooldowns: Path not found!");
        }

        for (Map<?, ?> map : section.getMapList("global-rules")) {
            int ruleCooldown = ((Number) map.get("cooldown")).intValue();
            List<String> ruleAffectedRanks = (List<String>) map.get("affected-ranks");
            List<String> ruleExemptRanks = (List<String>) map.get("exempt-ranks");
            String ruleScope = map.containsKey("scope") ? (String) map.get("scope") : "all";

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

            Scope scope = Scope.valueOf(ruleScope.toUpperCase());

            CommandCooldownRule rule = new CommandCooldownRule(null, ruleCooldown, affectedRanks, exemptRanks, scope);

            results.add(rule);
        }

        return results;
    }

    public static List<CommandCooldownRule> getPerCommandRules() {
        List<CommandCooldownRule> results = new ArrayList<>();

        ConfigurationSection section = CXYZ.configYaml.getConfigurationSection("cooldowns");

        if (section == null) {
            throw new InvalidConfigException("config.yml", "cooldowns", "Could not load command cooldowns: Path not found!");
        }

        for (Map<?, ?> map : section.getMapList("per-command-rules")) {
            String commandName = CommandNode.path((String) map.get("name"));
            int ruleCooldown = ((Number) map.get("cooldown")).intValue();
            List<String> ruleAffectedRanks = (List<String>) map.get("affected-ranks");
            List<String> ruleExemptRanks = (List<String>) map.get("exempt-ranks");
            String ruleScope = map.containsKey("scope") ? (String) map.get("scope") : "cmd";

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

            Scope scope = Scope.valueOf(ruleScope.toUpperCase());

            CommandCooldownRule rule = new CommandCooldownRule(commandName, ruleCooldown, affectedRanks, exemptRanks, scope);
            results.add(rule);
        }

        return results;
    }


}
