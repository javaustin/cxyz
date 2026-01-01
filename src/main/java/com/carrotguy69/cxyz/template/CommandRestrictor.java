package com.carrotguy69.cxyz.template;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.config.PlayerRank;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.configYaml;

public class CommandRestrictor implements CommandExecutor {
    public static class CommandRestriction {
        private final Command command;
        private final PlayerRank minRank;
        private final long minLevel;
        private final String message;

        // make getters and loader/constructor

        public CommandRestriction(Command command, String minRankName, long minLevel, String message) {
            this.command = command;
            this.minRank = PlayerRank.getRankByName(minRankName);
            this.minLevel = minLevel;
            this.message = message != null ? message : MessageGrabber.grab(MessageKey.COMMAND_RESTRICTED);
        }

        public static CommandRestriction getRestriction(String commandName) {

            if (configYaml.getString("command-restrictions." + commandName) == null) {
                return null;
            }

            Command cmd = Bukkit.getPluginCommand(commandName);

            if (cmd == null) {
                Logger.warning("In CommandRestriction.getRestriction(String commandName). Bukkit.getPluginCommand(commandName) returned null where commandName = '" + commandName + "'");
                return null;
            }

            return new CommandRestriction(
                    Bukkit.getPluginCommand(commandName),
                    configYaml.getString("command-restrictions." + commandName + ".minimum-rank", "default"),
                    configYaml.getLong("command-restrictions." + commandName + ".minimum-level", 0L),
                    configYaml.getString("command-restrictions." + commandName + ".message", MessageGrabber.grab("errors.command.restricted")
                    )
            );
        }

        public Command getCommand() {
            return command;
        }

        public long getMinLevel() {
            return minLevel;
        }

        public PlayerRank getMinRank() {
            return minRank;
        }

        public String getMessage() {
            return message;
        }
    }

    public static boolean handleRestricted(Command command, CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false; // Not restricted
        }

        Player p = (Player) sender;

        CommandRestriction restriction = CommandRestriction.getRestriction(command.getName().toLowerCase());

        if (restriction == null) {
            return false;
        }

        // A restriction exists. Let's see if the player can access it.
        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        boolean restricted = restriction.getMinLevel() > np.getLevel()
                || restriction.getMinRank().getHierarchy() > np.getRank().getHierarchy();


        Map<String, Object> map = new java.util.HashMap<>(Map.of(
                "command", command.getName().toLowerCase(),
                "level", restriction.getMinLevel()
        ));

        map.putAll(MapFormatters.playerFormatter(np));
        map.putAll(MapFormatters.rankFormatter(restriction.getMinRank()));

        if (restricted) {
            MessageUtils.sendParsedMessage(p, restriction.getMessage(), map);
        }

        return restricted;
    }

    public static boolean handleRestrictedTabCompleter(Command command, CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false; // Not restricted
        }

        Player p = (Player) sender;

        CommandRestriction restriction = CommandRestriction.getRestriction(command.getName().toLowerCase());

        if (restriction == null) {
            return false;
        }

        // A restriction exists. Let's see if the player can access it.
        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());


        return restriction.getMinLevel() > np.getLevel()
                || restriction.getMinRank().getHierarchy() > np.getRank().getHierarchy();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        return false;
    }
}
