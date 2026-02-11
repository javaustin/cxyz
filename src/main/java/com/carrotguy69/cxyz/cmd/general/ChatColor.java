package com.carrotguy69.cxyz.cmd.general;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChatColor implements CommandExecutor {

    public static final Map<String, String> map = new HashMap<>(CXYZ.colorMap);
    public static final Map<String, String> reverseMap = ObjectUtils.invertMap(map);

    public static class Color {
        public String name;
        public String code;

        public Color(String name, String code) {
            this.name = name;
            this.code = code;
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /chatcolor <color>
            /chatcolor GREEN
        */

        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }

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


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(p, MessageKey.MISSING_GENERAL, Map.of("missing-args", "color"));
            return true;
        }

        Color color = getColor(args[0]);

        if (color == null) {
            np.setChatColor("");
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_COLOR_RESET, commonMap);
            return true;
        }

        if (color.name.equalsIgnoreCase("unknown")) {
            MessageUtils.sendParsedMessage(p, MessageKey.INVALID_COLOR, Map.of("input", args[0]));
            return true;
        }

        commonMap.put("code", color.code);
        commonMap.put("color", color.name);

        if (np.getChatColor().equalsIgnoreCase(color.code)) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_COLOR_DUPLICATE_STATE, commonMap);
            return true;
        }

        np.unEquipCosmeticOfType(Cosmetic.CosmeticType.CHAT_COLOR);
        np.setChatColor(color.code);

        np.sync();

        MessageUtils.sendParsedMessage(p, MessageKey.CHAT_COLOR_SET, commonMap);

        return true;
    }

    public static Color getColor(String input) {
        if (input.equalsIgnoreCase("reset") || input.equalsIgnoreCase("none")) {
            return null;
        }

        else if (map.containsKey(input.toUpperCase())) {
            // If the value matches any color name e.g., "BLACK"
            String colorCode = map.get(input.toUpperCase());
            String colorName = input.toUpperCase();

            return new Color(colorName, colorCode);
        }

        else if (reverseMap.containsKey(input.toLowerCase())) {
            // If the value matches any color code e.g., "&0"
            String colorCode = input.toLowerCase();
            String colorName = reverseMap.get(input.toLowerCase());

            return new Color(colorName, colorCode);
        }

        else {
            return new Color("unknown", input);
        }
    }

    public static String getColorNameByCode(String colorCode) {
        String possibleValue = reverseMap.get(colorCode);

        if (possibleValue == null) {
            return colorCode;
        }

        return possibleValue;
    }
}
