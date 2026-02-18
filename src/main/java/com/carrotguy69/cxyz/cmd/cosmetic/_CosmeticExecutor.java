package com.carrotguy69.cxyz.cmd.cosmetic;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.other.utils.ObjectUtils.slice;

public class _CosmeticExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /cosmetic <buy | equip | unequip | list>
            /cosmetic buy rainbow-armor
            /cosmetic list
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.cosmetic";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COSMETIC_AVAILABLE_SUBCOMMANDS, Map.of());
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {

            case "buy":
                return new CosmeticBuy().onCommand(sender, command, s, slice(args, 1));
            case "equip":
                return new CosmeticEquip().onCommand(sender, command, s, slice(args, 1));
            case "unequip":
                return new CosmeticUnequip().onCommand(sender, command, s, slice(args, 1));
            default:
                return new CosmeticList().onCommand(sender, command, s, slice(args, 1));
        }
    }
}
