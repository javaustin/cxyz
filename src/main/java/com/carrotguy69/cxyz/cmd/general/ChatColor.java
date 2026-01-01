package com.carrotguy69.cxyz.cmd.general;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChatColor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.chatcolor";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;
        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (args.length > 0 && (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("none"))) {
            np.setChatColor("");
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_COLOR_RESET, commonMap);
        }

        else if (args.length > 0 && getMap().containsKey(args[0].toUpperCase())) {
            // If the args[0].toUpperCase() matches any color name e.g., "BLACK"
            String colorCode = getMap().get(args[0].toUpperCase());
            String colorName = args[0].toUpperCase();

            np.setChatColor(colorCode);

            commonMap.put("code", colorCode);
            commonMap.put("color", colorName);

            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_COLOR_SET, commonMap);
        }

        else if (args.length > 0 && getReverseMap().containsKey(args[0].toUpperCase())) {
            // If the args[0].toUpperCase() matches any color name e.g., "BLACK"
            String colorCode = args[0].toUpperCase();
            String colorName = getReverseMap().get(args[0].toUpperCase());

            np.setChatColor(colorCode);


            commonMap.put("code", colorCode);
            commonMap.put("color", colorName);

            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_COLOR_SET, commonMap);
        }

        else {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_COLOR_INVALID_COLOR, commonMap);
            return true;
        }

        np.sync();

        return true;
    }

    public static String getColorByName(String colorName) {
        String possibleValue = getMap().get(colorName.toUpperCase());

        if (possibleValue == null) {
            return colorName;
        }

        return possibleValue;
    }

    public static String getColorNameByCode(String colorCode) {
        String possibleValue = getReverseMap().get(colorCode);

        if (possibleValue == null) {
            return colorCode;
        }

        return possibleValue;
    }


    public static Map<String, String> getMap() {
        Map<String, String> colorCodes = new HashMap<>();

        colorCodes.put("BLACK", "&0");
        colorCodes.put("DARK_BLUE", "&1");
        colorCodes.put("DARK_GREEN", "&2");
        colorCodes.put("DARK_AQUA", "&3");
        colorCodes.put("DARK_RED", "&4");
        colorCodes.put("DARK_PURPLE", "&5");
        colorCodes.put("GOLD", "&6");
        colorCodes.put("GRAY", "&7");
        colorCodes.put("DARK_GRAY", "&8");
        colorCodes.put("BLUE", "&9");
        colorCodes.put("GREEN", "&a");
        colorCodes.put("AQUA", "&b");
        colorCodes.put("RED", "&c");
        colorCodes.put("LIGHT_PURPLE", "&d");
        colorCodes.put("YELLOW", "&e");
        colorCodes.put("WHITE", "&f");

        return colorCodes;
    }

    public static Map<String, String> getReverseMap() {
        Map<String, String> reversedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : getMap().entrySet()) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }

        return reversedMap;
    }
}
