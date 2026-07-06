package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
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

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.mend";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());

        ItemStack is = np.getPlayer().getInventory().getItemInMainHand();

        ItemMeta meta = is.getItemMeta();

        if (meta instanceof Damageable) {
            Damageable damagableMeta = (Damageable) meta;

            ((Damageable) meta).setDamage(0);

            is.setItemMeta(damagableMeta);
            MessageUtils.sendParsedMessage(sender, MessageKey.MEND, Map.of());
        }
        else {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_REPAIRABLE_ITEM, Map.of("item", is.getType().name()));
            return true;
        }

        return true;
    }
}
