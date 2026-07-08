package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Enchant implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.enchant";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        NetworkPlayer target = null;

        int level = 1;

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.MISSING_GENERAL), Map.of("missing-args", "enchantment"));
            return true;
        }

        boolean clear = args[0].equalsIgnoreCase("clear");

        Enchantment enchant = !clear ? Registry.ENCHANTMENT.get(NamespacedKey.minecraft(args[0])) : null;

        if (args.length >= 2 && !clear) {
            try {
                level = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[1]));
                return true;
            }
        }

        if (enchant == null && !clear) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_ENCHANT, Map.of("input", args[0]));
            return true;
        }

        if (args.length == (3 - (clear ? 1 : 0)) && sender.hasPermission(node + ".others")) {
            String username = args[2 - (clear ? 1 : 0)];

            target = NetworkPlayer.getPlayerByUsername(username);

            if (target == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", username));
                return true;
            }

            if (target.getPlayer() == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(target));
                return true;
            }
        }

        else if (sender instanceof Player) {
            target = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());
        }

        if (target == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        ItemStack is = target.getPlayer().getInventory().getItemInMainHand();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(target);
        commonMap.put("item", is.getType().name());

        if (clear) {
            is.removeEnchantments();
            MessageUtils.sendParsedMessage(sender, MessageKey.ENCHANT_CLEAR, commonMap);

            return true;
        }

        commonMap.put("level", level);
        commonMap.put("enchant", enchant.getKey().getKey());
        commonMap.put("enchantment", enchant.getKey().getKey());

        try {
            is.addUnsafeEnchantment(enchant, level);
        }
        catch (RuntimeException e) {
            Logger.debug("enchant command: " + e);
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_ENCHANTABLE_ITEM, commonMap);
            return true;
        }

        MessageUtils.sendParsedMessage(sender, MessageKey.ENCHANT, commonMap);

        return true;
    }
}
