package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Location implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String node = "cxyz.location";
        String node2 = "cxyz.location.others";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.COMMAND_NO_ACCESS), Map.of("permission", node));
            return true;
        }

        Player p = null;
        boolean longFlag = false;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("-l")) {
                longFlag = true;
                break;
            }
        }

        if (longFlag)
            args = ObjectUtils.removeItem(args, "-l");

        if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.MISSING_GENERAL), Map.of("missing-args", "player"));
            return true;
        }

        if (args.length == 0 || !sender.hasPermission(node2)) {
            p = (Player) sender;
        }

        else {
            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[0]);
            if (np != null)
                p = np.getPlayer();

            if (p == null && np != null) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_IS_OFFLINE), MapFormatters.playerFormatter(np));
                return true;
            }

            else if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_NOT_FOUND), Map.of("username", args[0]));
                return true;
            }
        }

        Map<String, Object> commonMap = MapFormatters.locationFormatter(p.getLocation());

        MessageUtils.sendParsedMessage(
                sender,
                MessageGrabber.grab(MessageKey.valueOf("LOCATION_" + (longFlag ? "LONG" : "SHORT"))),
                commonMap
        );

        return true;
    }
}
