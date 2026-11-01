package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import com.carrotguy69.cxyz.utils.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Mend implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*
        SYNTAX:
            /mend [player]
            /mend cerrot
            /mend
        */

        args = CommandUtils.handleSilent(args);

        String node = "mend";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        Player p = null;

        if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.MISSING_GENERAL), Map.of("missing-args", "player"));
            return true;
        }

        if (args.length == 0 || !sender.hasPermission("cxyz." + node + ".others")) {
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

            else if (np == null || !np.isVisibleTo(sender)) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_NOT_FOUND), Map.of("username", args[0]));
                return true;
            }
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        ItemStack is = p.getInventory().getItemInMainHand();

        ItemMeta meta = is.getItemMeta();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("item", is.getType().name());

        if (meta instanceof Damageable damagableMeta) {
            damagableMeta.setDamage(0);

            is.setItemMeta(damagableMeta);
            MessageUtils.sendParsedMessage(sender, MessageKey.MEND, commonMap);
        }
        else {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_REPAIRABLE_ITEM, commonMap);
            return true;
        }

        return true;
    }
}
