package com.carrotguy69.cxyz.cmd.general;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class Timezone implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /timezone <timezone>
            /timezone America/Los_Angeles
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.timezone";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        if (args.length == 0) {
            Player p = (Player) sender;

            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

            String timeZone = np.getTimezone();

            Map<String, Object> commonMap = new HashMap<>(MapFormatters.playerFormatter(np));

            commonMap.put("timezone-id", timeZone);
            commonMap.put("timezone-id-short", TimeUtils.getTimezoneShort(timeZone));

            MessageUtils.sendParsedMessage(sender, MessageKey.TIMEZONE_VIEW, commonMap);
            return true;
        }

        if (args.length == 1) {
            Player p = (Player) sender;

            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());


            for (String tz : ZoneId.getAvailableZoneIds()) {
                if (tz.equalsIgnoreCase(args[0])) {

                    Map<String, Object> commonMap = new HashMap<>(MapFormatters.playerFormatter(np));

                    commonMap.put("timezone-id", tz);
                    commonMap.put("timezone-id-short", TimeUtils.getTimezoneShort(tz));

                    if (np.getTimezone().equalsIgnoreCase(tz)) {
                        MessageUtils.sendParsedMessage(p, MessageKey.TIMEZONE_ERROR_DUPLICATE_STATE, commonMap);
                        return true;
                    }

                    np.setTimezone(tz);

                    MessageUtils.sendParsedMessage(sender, MessageKey.TIMEZONE_SET, commonMap);

                    return true;
                }
            }

            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_TIMEZONE, Map.of("input", args[0]));
        }


        return true;
    }
}
