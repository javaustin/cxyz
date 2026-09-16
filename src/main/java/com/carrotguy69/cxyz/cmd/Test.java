package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class Test implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String node = "cxyz.test";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        /*
        SYNTAX:
            /test ...
            /test whatever
        */

        if (!(sender instanceof Player p)) {
            return true;
        }

        if (args.length == 0) {
            args = new String[1];
            args[0] = "CRAFTING";
        }

        p.openInventory(Bukkit.createInventory(p, InventoryType.valueOf(args[0].toUpperCase())));



        return true;
    }
}
