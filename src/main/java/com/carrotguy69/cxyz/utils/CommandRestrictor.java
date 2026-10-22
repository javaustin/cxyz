package com.carrotguy69.cxyz.utils;

import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Locale;

import static com.carrotguy69.cxyz.CXYZ.configYaml;

public class CommandRestrictor {
    public static class CommandRestriction {
        private final PlayerRank minRank;
        private final long minLevel;
        private final String message;

        public CommandRestriction(String minRankName, long minLevel, String message) {
            this.minRank = PlayerRank.getRankByName(minRankName);
            this.minLevel = minLevel;
            this.message = message != null ? message : MessageGrabber.grab(MessageKey.COMMAND_NO_ACCESS);
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

    public static CommandRestriction getRestriction(String commandPath) {

        if (configYaml.getString("command-restrictions." + commandPath) == null) {
            return null;
        }

        return new CommandRestriction(
                configYaml.getString("command-restrictions." + commandPath + ".minimum-rank", "default"),
                configYaml.getLong("command-restrictions." + commandPath + ".minimum-level", 0L),
                configYaml.getString("command-restrictions." + commandPath + ".message")
        );
    }

    public static boolean handleRestricted(String commandPath, CommandSender sender) {

        if (!(sender instanceof Player p)) {
            return false;
        }

        commandPath = commandPath.toLowerCase(Locale.ROOT);
        CommandRestriction restriction = getRestriction(commandPath);

        if (restriction == null) {
            return false;
        }

        // A restriction exists. Let's see if the player can access it.
        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        boolean isRestricted = restriction.getMinLevel() > np.getLevel()
                || restriction.getMinRank().getHierarchy() > np.getTopRank().getHierarchy();


        Map<String, Object> map = new java.util.HashMap<>(Map.of(
                "command-path", commandPath,
                "level", restriction.getMinLevel()
        ));

        map.putAll(MapFormatters.playerFormatter(np));
        map.putAll(MapFormatters.rankFormatter(restriction.getMinRank()));

        if (isRestricted) {
            MessageUtils.sendParsedMessage(p, restriction.getMessage(), map);
        }

        return isRestricted;
    }

    public static boolean handleRestrictedTabCompleter(Command command, CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false; // Not restricted
        }

        Player p = (Player) sender;

        CommandRestriction restriction = getRestriction(command.getName().toLowerCase());

        if (restriction == null) {
            return false;
        }

        // A restriction exists. Let's see if the player can access it.
        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());


        return restriction.getMinLevel() > np.getLevel()
                || restriction.getMinRank().getHierarchy() > np.getTopRank().getHierarchy();
    }
}
