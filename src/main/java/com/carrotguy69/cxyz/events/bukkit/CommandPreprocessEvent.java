package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.models.config.cooldowns.CommandCooldownRule;
import com.carrotguy69.cxyz.utils.TimeUtils;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.globalCooldownRules;
import static com.carrotguy69.cxyz.CXYZ.lastCommand;
import static com.carrotguy69.cxyz.CXYZ.lastCommandByLine;

public class CommandPreprocessEvent {
    public static void onCommand(PlayerCommandPreprocessEvent e) {

        String commandString = e.getMessage();

        NetworkPlayer np = NetworkPlayer.resolvePlayer(e.getPlayer().getUniqueId());

        commandString = commandString.substring(1).trim(); // Remove leading "/"
        String[] commandParts = commandString.split("\\s+");
        String commandNode = commandParts[0].toLowerCase();

        long now = TimeUtils.unixTimeNow();
        long allCommandCooldown = 0;
        Map<String, Integer> commandCooldowns = new HashMap<>();

        if (globalCooldownRules.getScope() == CommandCooldownRule.Scope.ALL && globalCooldownRules.appliesTo(np, commandString)) {
            allCommandCooldown = Math.max(allCommandCooldown, globalCooldownRules.getCooldown());
        }

        for (CommandCooldownRule rule : CXYZ.perCommandCooldownRules) {
            if (rule.appliesTo(np, commandString)) {
                if (rule.getScope() == CommandCooldownRule.Scope.ALL) {
                    allCommandCooldown = Math.max(allCommandCooldown, rule.getCooldown());
                }
                else {
                    for (String affectedCommand : rule.getMatchingCommands(commandString)) {
                        commandCooldowns.merge(affectedCommand, rule.getCooldown(), Math::max);
                    }
                }
            }
        }

        boolean hasCommandCooldown = !commandCooldowns.isEmpty();

        if (allCommandCooldown > 0 || hasCommandCooldown) {

            Map<String, Long> commandHistory = lastCommandByLine.computeIfAbsent(np.getUUID(), ignored -> new java.util.concurrent.ConcurrentHashMap<>());
            long lastAnyCommand = CXYZ.lastCommand.getOrDefault(np.getUUID(), 0L);

            long remainingAll = allCommandCooldown == 0 ? 0 : lastAnyCommand + allCommandCooldown - now;
            long remainingCommand = 0;
            for (Map.Entry<String, Integer> entry : commandCooldowns.entrySet()) {
                long lastCommandAt = commandHistory.getOrDefault(entry.getKey(), 0L);
                remainingCommand = Math.max(remainingCommand, lastCommandAt + entry.getValue() - now);
            }

            if (Math.max(remainingAll, remainingCommand) > 0 && !CommandCooldownRule.isGlobalExemptCommand(commandString)) {
                MessageUtils.sendParsedMessage(e.getPlayer(), MessageKey.COMMAND_COOLDOWN, Map.of("remaining-seconds", Math.max(remainingAll, remainingCommand), "permission", commandNode));
                e.setCancelled(true);
                return;
            }
            if (allCommandCooldown > 0) {
                lastCommand.put(np.getUUID(), now);
            }
            for (String affectedCommand : commandCooldowns.keySet()) {
                commandHistory.put(affectedCommand, now);
            }
        }

        for (String restrictedCommand : CXYZ.muteRestrictions) {
            if (commandString.startsWith(restrictedCommand) && np.isMuted()) {
                Punishment mute = Punishment.getActivePunishment(np, Punishment.PunishmentType.MUTE);

                MessageUtils.sendParsedMessage(e.getPlayer(), MessageKey.PUNISHMENT_MUTE_PLAYER_MESSAGE, MapFormatters.punishmentFormatter(e.getPlayer(), mute));
                e.setCancelled(true);
                return;
            }
        }
    }

}
