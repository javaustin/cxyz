package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.EnchantingTable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnchantTable implements CommandExecutor {

    public static List<UUID> inGUI = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        args = CommandUtils.handleSilent(args);

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.enchanttable";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        Player p;

        if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.MISSING_GENERAL), Map.of("missing-args", "player"));
            return true;
        }

        if (args.length == 0 || !sender.hasPermission(node + ".others")) {
            p = (Player) sender;
        }

        else {
            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[0]);
            if (np != null)
                p = np.getPlayer();
            else {
                p = null;
            }

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

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        InventoryView view = p.openEnchanting(null, true);

//        if (view == null) {
//            throw new RuntimeException("InventoryView view cannot be null!");
//        }
//
//        Inventory inv = view.getTopInventory();
//
//        if (sender.hasPermission(node + ".with-lapis")) {
//            Logger.log(inv.getType() + ", " +  inv.getHolder());
//
//            Logger.log(view.getTopInventory().getType().toString());
//            Logger.log(view.getBottomInventory().getType().toString());
//
//            p.getOpenInventory().setItem(1, new ItemStack(Material.LAPIS_LAZULI, 64));
//            inGUI.add(p.getUniqueId());
//        }

        MessageUtils.sendParsedMessage(sender, MessageKey.ENCHANT_TABLE, commonMap);

        return true;
    }
}
