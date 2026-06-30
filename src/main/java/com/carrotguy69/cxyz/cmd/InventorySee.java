package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InventorySee implements CommandExecutor {

    // Usage: /invsee <player> [armor | offhand]

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String node = "cxyz.inventorysee";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.COMMAND_NO_ACCESS), Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.COMMAND_PLAYER_ONLY), Map.of());
            return true;
        }

        Player p = (Player) sender;
        NetworkPlayer np = null;

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.MISSING_GENERAL), Map.of("missing-args", "player"));
            return true;
        }


        if (args.length >= 1) {
            np = NetworkPlayer.getPlayerByUsername(args[0]);
            if (np != null)
                p = np.getPlayer();

            if (p == null) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_IS_OFFLINE), MapFormatters.playerFormatter(np));
                return true;
            }

            else if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_NOT_FOUND), Map.of("username", args[0]));
                return true;
            }
        }
//
//        boolean armor = false;
//        boolean offhand = false;
//
//        if (args.length == 2) {
//            if (args[1].equalsIgnoreCase("armor")) {
//                armor = true;
//            }
//            else if (args[1].equalsIgnoreCase("offhand")) {
//                offhand = true;
//            }
//        }


        p.openInventory(np.getPlayer().getInventory());


        MessageUtils.sendParsedMessage(
                sender,
                MessageGrabber.grab(MessageKey.INVENTORY_VIEW),
                MapFormatters.playerFormatter(np)
        );

        return true;
    }

}
