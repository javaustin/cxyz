package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Ping implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /ping <player>
            /ping Steve
        */


        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.ping";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }


        else if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        else if (args.length == 0) {
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());

            viewPing(sender, np);

            return true;
        }

        else if (args.length == 1) {

            String nodeOthers = "cxyz.admin.ping.others";
            if (!sender.hasPermission(nodeOthers)) {
                viewPing(sender, NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId()));
                return true;
            }

            // This is the only block of code that represents a sender entering a username

            String possibleUsername = args[0];

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(possibleUsername);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", possibleUsername));
                return true;
            }

            viewPing(sender, np);
        }

        return true;
    }


    public static void viewPing(CommandSender sender, NetworkPlayer np) {
        // No need to await a request anymore!

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("ping", np.getPlayer().getPing());

        MessageUtils.sendParsedMessage(sender, MessageKey.PING, commonMap);
    }
}
