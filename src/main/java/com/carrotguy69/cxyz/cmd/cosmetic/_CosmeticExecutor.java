package com.carrotguy69.cxyz.cmd.cosmetic;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _CosmeticExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /cosmetic <buy | equip | unequip | list>
            /cosmetic buy rainbow-armor
            /cosmetic list
        */

        String node = "cosmetic";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COSMETIC_AVAILABLE_SUBCOMMANDS, Map.of());
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {

            case "buy":
                return new CosmeticBuy().onCommand(sender, command, s, slice(args, 1));
            case "disable":
                return new CosmeticDisable().onCommand(sender, command, s, slice(args, 1));
            case "enable":
                return new CosmeticEnable().onCommand(sender, command, s, slice(args, 1));
            case "equip":
                return new CosmeticEquip().onCommand(sender, command, s, slice(args, 1));
            case "unequip":
                return new CosmeticUnequip().onCommand(sender, command, s, slice(args, 1));
            default:
                return new CosmeticList().onCommand(sender, command, s, slice(args, 1));
        }
    }
}
