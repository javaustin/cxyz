package com.carrotguy69.cxyz.models.config.cooldowns;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.exceptions.InvalidConfigException;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

public class CommandCooldownRule {
    public enum Scope {
        CMD,
        ALL
    }

    private final @Nullable List<String> affectedCommands;
    private final int cooldown;
    private final List<PlayerRank> affectedRanks;
    private final List<PlayerRank> exemptRanks;
    private final Scope scope;

    public CommandCooldownRule(@Nullable List<String> affectedCommands, int cooldown, List<PlayerRank> affectedRanks, List<PlayerRank> exemptRanks, Scope scope) {
        this.affectedCommands = affectedCommands;
        this.cooldown = cooldown;
        this.affectedRanks = affectedRanks;
        this.exemptRanks = exemptRanks;
        this.scope = scope;
    }

    public List<String> getAffectedCommands() {
        return affectedCommands;
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

    public List<String> getMatchingCommands(String commandLine) {
        if (affectedCommands == null || affectedCommands.isEmpty()) {
            return List.of();
        }

        List<String> matchingCommands = new ArrayList<>();
        for (String affectedCommand : affectedCommands) {
            if (commandLine.toLowerCase().startsWith(affectedCommand.toLowerCase())) {
                matchingCommands.add(affectedCommand);
            }
        }

        return matchingCommands;
    }

    public boolean appliesTo(NetworkPlayer player, String commandLine) {
        if (affectedCommands != null && !affectedCommands.isEmpty() && getMatchingCommands(commandLine).isEmpty()) {
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

        boolean isPermissionExempt = player.getPlayer() != null && (player.getPlayer().hasPermission("cxyz.command-cooldown-bypass.*"));

        return result && !isPermissionExempt;
    }

    public static CommandCooldownRule getGlobalRules() {
        ConfigurationSection section = CXYZ.configYaml.getConfigurationSection("command-cooldowns");

        if (section == null) {
            throw new InvalidConfigException("config.yml", "command-cooldowns", "Could not load command cooldowns: Path not found!");
        }

        int ruleCooldown = section.getInt("global-rules.cooldown");
        List<String> ruleAffectedRanks = section.getStringList("global-rules.affected-ranks");
        List<String> ruleExemptRanks = section.getStringList("global-rules.exempt-ranks");
        CXYZ.cooldownExemptCommands = section.getStringList("global-rules.exempt-commands");
        String ruleScope = section.getString("global-rules.scope", "all");

        List<PlayerRank> affectedRanks = new ArrayList<>();

        for (String rankName : ruleAffectedRanks) {
            PlayerRank rank = PlayerRank.getRankByName(rankName);
            affectedRanks.add(rank);
        }

        if (affectedRanks.isEmpty()) {
            // If affected-ranks is empty, it is assumed all ranks are affected by the cooldown rule. (exempt-ranks will still remain excluded.)
            affectedRanks.addAll(PlayerRank.getRanks());
        }

        List<PlayerRank> exemptRanks = new ArrayList<>();

        for (String rankName : ruleExemptRanks) {
            PlayerRank rank = PlayerRank.getRankByName(rankName);
            exemptRanks.add(rank);
        }

        Scope scope = Scope.valueOf(ruleScope.toUpperCase());

        return new CommandCooldownRule(null, ruleCooldown, affectedRanks, exemptRanks, scope);
    }

    public static List<CommandCooldownRule> getPerCommandRules() {
        List<CommandCooldownRule> results = new ArrayList<>();

        ConfigurationSection section = CXYZ.configYaml.getConfigurationSection("command-cooldowns");

        if (section == null) {
            throw new InvalidConfigException("config.yml", "command-cooldowns", "Could not load command cooldowns: Path not found!");
        }

        for (Map<?, ?> map : section.getMapList("per-command-rules")) {
            List<String> affectedCommands = (List<String>) map.get("affected-commands");
            int ruleCooldown = ((Number) map.get("cooldown")).intValue();
            List<String> ruleAffectedRanks = (List<String>) map.get("affected-ranks");
            List<String> ruleExemptRanks = (List<String>) map.get("exempt-ranks");
            String ruleScope = map.containsKey("scope") ? (String) map.get("scope") : "cmd";

            if (ruleAffectedRanks == null)
                ruleAffectedRanks = List.of();

            if (ruleExemptRanks == null)
                ruleExemptRanks = List.of();

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

            if (affectedCommands == null)
                affectedCommands = List.of();

            CommandCooldownRule rule = new CommandCooldownRule(affectedCommands, ruleCooldown, affectedRanks, exemptRanks, scope);
            results.add(rule);
        }

        // Only a universal bypass. No bypasses for individual commands.
        CXYZ.registerPermission("cxyz.command-cooldown-bypass.*", PermissionDefault.OP);

        return results;
    }

    public static boolean isGlobalExemptCommand(String commandLine) {
        for (String line : CXYZ.cooldownExemptCommands) {
            if (commandLine.toLowerCase().startsWith(line.toLowerCase())) {
                return true;
            }
        }

        return false;

    }

}
