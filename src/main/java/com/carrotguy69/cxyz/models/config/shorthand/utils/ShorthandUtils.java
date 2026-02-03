package com.carrotguy69.cxyz.models.config.shorthand.utils;

import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import com.carrotguy69.cxyz.tabCompleters.AnyPlayer;
import com.carrotguy69.cxyz.tabCompleters.ChatChannel;
import com.carrotguy69.cxyz.tabCompleters.OnlinePlayer;
import org.bukkit.command.CommandSender;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.*;

public class ShorthandUtils {

    public static Map<String, Object> mapArgs(String[] triggerArgs, String[] commandArgs) {

        Map<String, Object> map = new LinkedHashMap<>();

        if (triggerArgs.length > commandArgs.length) {
            for (int i = 0; i < triggerArgs.length; i++) {
                if (i < commandArgs.length) {
                    map.put(stripParam(triggerArgs[i]), commandArgs[i]);
                }
                else {
                    map.put(stripParam(triggerArgs[i]), "");
                }
            }
        }

        if (triggerArgs.length == commandArgs.length) {
            for (int i = 0; i < triggerArgs.length; i++) {
                map.put(stripParam(triggerArgs[i]), commandArgs[i]);
            }
        }

        if (triggerArgs.length < commandArgs.length) {
            for (int i = 0; i < commandArgs.length; i++) {
                if (i < triggerArgs.length - 1) {
                    map.put(stripParam(triggerArgs[i]), commandArgs[i]);
                }
                else {
                    map.put(stripParam(triggerArgs[i]), String.join(" ", ObjectUtils.slice(commandArgs, i)));
                    break;
                }
            }
        }

        return map;

    }

    public static List<String> getTabSuggestions(String param, CommandSender sender, NetworkPlayer np) {
        if (param == null || param.isBlank()) {
            return List.of();
        }

        if (isEnumeratedParam(param))
            param = stripEnumeratedParam(param);

        else
            param = stripParam(param);

        switch (param) {
            case "player":
                return AnyPlayer.getAllUsernames();

            case "online-player":
                return OnlinePlayer.getVisibleUsernames(sender, np);

            case "channel":
                return ChatChannel.getVisibleChannels(sender, np);

            case "rank":
                return ranks.stream().map(PlayerRank::getName).collect(Collectors.toList());

            case "cosmetic":
                return cosmetics.stream().map(Cosmetic::getId).collect(Collectors.toList());

        }

        return List.of();
    }

    public static String stripParam(String param) {
        // "{any-param}" -> "any-param"

        return param.replace("{", "").replace("}", "");
    }

    public static String stripEnumeratedParam(String param) {
        // "{player-0}" -> "player"

        String stripped = param;

        stripped = stripped.replace("{", "").replace("}", "");

        String[] slices = stripped.split("-");

        return String.join("-", ObjectUtils.slice(slices, 0, slices.length - 1));
    }

    public static boolean isEnumeratedParam(String param) {
        // "{player-30}" -> true
        // "{player}" -> false

        String stripped = param;

        stripped = stripped.replace("{", "").replace("}", "");

        String[] slices = stripped.split("-");

        if (slices.length >= 2) {
            String first = String.join("-", ObjectUtils.slice(slices, 0, slices.length - 1));
            String last = String.join("-", ObjectUtils.slice(slices, slices.length-1, slices.length));

            return last.matches("\\d+");
        }

        return false;
    }


}
