package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Item implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Syntax: /item <item> [amount] [player] [slot]

        String node = "cxyz.item";

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        if (!(sender.hasPermission(node))) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            boolean isPlayer = sender instanceof Player;
            MessageUtils.sendParsedMessage(
                    sender,
                    MessageKey.MISSING_GENERAL,
                    Map.of("missing-args", isPlayer ? "item" : "item, amount, player")
            );
            return true;
        }

        final Material item;
        try {
            item = Material.valueOf(args[0].toUpperCase());
        }
        catch (IllegalArgumentException ex) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_ITEM, Map.of("input", args[0]));
            return true;
        }

        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException ex) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[1]));
                return true;
            }
        }

        final Player p;
        boolean senderIsPlayer = sender instanceof Player;

        if (args.length == 2) {
            if (!senderIsPlayer) {
                MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
                return true;
            }

            p = (Player) sender;
        }
        else if (args.length >= 3) {
            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[2]);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("input", args[2]));
                return true;
            }

            Player resolved = np.getPlayer();
            if (resolved == null || !np.isOnline() || !np.isVisibleTo(sender)) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, Map.of("input", args[2]));
                return true;
            }

            p = resolved;
        }
        else {
            if (!senderIsPlayer) {
                MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
                return true;
            }

            p = (Player) sender;
        }

        int slot = -1;
        if (args.length == 4) {
            try {
                slot = Integer.parseInt(args[3]);
            }
            catch (NumberFormatException ex) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[3]));
                return true;
            }
        }

        addItem(p, new ItemStack(item, amount), slot);
        return true;
    }

    public static void addItem(Player p, ItemStack incoming, int slot) {
        PlayerInventory inv = p.getInventory();

        if (incoming == null) return;

        // If slot == -1, normal add
        if (slot == -1) {
            inv.addItem(incoming);
            return;
        }

        ItemStack current = inv.getItem(slot);

        // Empty slot: just put it there
        if (current == null || current.getType() == Material.AIR) {
            inv.setItem(slot, incoming);
            return;
        }

        // If not similar, add elsewhere
        if (!current.isSimilar(incoming)) {
            inv.addItem(incoming);
            return;
        }

        int max = Math.min(current.getMaxStackSize(), current.getType().getMaxStackSize());
        int canFit = max - current.getAmount();

        // If it can't fit anything, add elsewhere
        if (canFit <= 0) {
            inv.addItem(incoming);
            return;
        }

        // Merge as much as possible into the target slot
        int toMove = Math.min(canFit, incoming.getAmount());
        current.setAmount(current.getAmount() + toMove);
        inv.setItem(slot, current);

        // Add remainder to inventory
        int remainder = incoming.getAmount() - toMove;
        if (remainder > 0) {
            ItemStack rest = incoming.clone();
            rest.setAmount(remainder);
            inv.addItem(rest);
        }
    }

}
