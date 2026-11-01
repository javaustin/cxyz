package com.carrotguy69.cxyz.events.bukkit;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.models.config.CommandCooldownRule;
import com.carrotguy69.cxyz.utils.CommandNode;
import com.carrotguy69.cxyz.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.lastCommand;
import static com.carrotguy69.cxyz.CXYZ.lastCommandByNode;

public class CommandPreprocessEvent {
    public static void onCommand(PlayerCommandPreprocessEvent e) {

        String commandString = e.getMessage();

        NetworkPlayer np = NetworkPlayer.resolvePlayer(e.getPlayer().getUniqueId());

        commandString = commandString.substring(1).trim(); // Remove leading "/"
        String[] commandParts = commandString.split("\\s+");
        String label = commandParts[0].toLowerCase();
        PluginCommand registered = Bukkit.getPluginCommand(label);
        String rootPath = CommandNode.path(registered == null ? label : registered.getName());
        String commandNode = resolveCooldownNode(rootPath, commandParts);

        long now = TimeUtils.unixTimeNow();
        long allCommandCooldown = 0;
        long commandCooldown = 0;
        for (CommandCooldownRule rule : CXYZ.globalCooldownRules) {
            if (rule.getScope() == CommandCooldownRule.Scope.ALL && rule.appliesTo(np, commandNode)) {
                allCommandCooldown = Math.max(allCommandCooldown, rule.getCooldown());
            }
        }
        for (CommandCooldownRule rule : CXYZ.perCommandCooldownRules) {
            if (rule.appliesTo(np, commandNode)) {
                if (rule.getScope() == CommandCooldownRule.Scope.ALL) {
                    allCommandCooldown = Math.max(allCommandCooldown, rule.getCooldown());
                } else {
                    commandCooldown = Math.max(commandCooldown, rule.getCooldown());
                }
            }
        }

        if (allCommandCooldown > 0 || commandCooldown > 0) {
            long lastCommandAt = lastCommandByNode.computeIfAbsent(np.getUUID(), ignored -> new java.util.concurrent.ConcurrentHashMap<>())
                    .getOrDefault(commandNode, 0L);
            long lastAnyCommand = CXYZ.lastCommand.getOrDefault(np.getUUID(), 0L);
            long remainingAll = allCommandCooldown == 0 ? 0 : lastAnyCommand + allCommandCooldown - now;
            long remainingCommand = commandCooldown == 0 ? 0 : lastCommandAt + commandCooldown - now;
            if (Math.max(remainingAll, remainingCommand) > 0) {
                MessageUtils.sendParsedMessage(e.getPlayer(), MessageKey.COMMAND_COOLDOWN,
                        Map.of("remaining-seconds", Math.max(remainingAll, remainingCommand), "permission", commandNode));
                e.setCancelled(true);
                return;
            }
            if (allCommandCooldown > 0) {
                lastCommand.put(np.getUUID(), now);
            }
            if (commandCooldown > 0) {
                lastCommandByNode.get(np.getUUID()).put(commandNode, now);
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

    /**
     * Resolves the most specific configured cooldown node for a command.
     * For example, /cosmetic buy resolves to cosmetic.buy when that path is
     * configured, while /cosmetic equip can still resolve to cosmetic.
     */
    private static String resolveCooldownNode(String rootPath, String[] commandParts) {
        String resolved = rootPath;

        for (CommandCooldownRule rule : CXYZ.perCommandCooldownRules) {
            String configuredNode = rule.getName();
            if (configuredNode == null) {
                continue;
            }

            String configuredPath = CommandNode.path(configuredNode);
            if (!configuredPath.startsWith(rootPath + ".")) {
                continue;
            }

            String[] configuredParts = configuredPath.split("\\.");
            if (configuredParts.length > commandParts.length) {
                continue;
            }

            boolean matches = true;
            for (int i = 0; i < configuredParts.length; i++) {
                String actualPart = i == 0 ? rootPath : commandParts[i];
                if (!configuredParts[i].equalsIgnoreCase(actualPart)) {
                    matches = false;
                    break;
                }
            }

            if (matches && configuredParts.length > resolved.split("\\.").length) {
                resolved = configuredPath;
            }
        }

        return resolved;
    }
}
