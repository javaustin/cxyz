package com.carrotguy69.cxyz.cmd.mod.punishment;

import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class _PunishmentExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /punishment delete <id>
            /punishment edit <id> <attribute> <new_value>
            /punishment history <player>
            /punishment info <id>
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.punishment";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_AVAILABLE_SUBCOMMANDS, Map.of("permission", node));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clear":
                new PunishmentClear().onCommand(sender, command, label, ObjectUtils.slice(args, 1));
                break;

            case "remove":
            case "delete":
                new PunishmentDelete().onCommand(sender, command, label, ObjectUtils.slice(args, 1));
                break;

            case "edit":
                new PunishmentEdit().onCommand(sender, command, label, ObjectUtils.slice(args, 1));
                break;

            case "history":
                new PunishmentHistory().onCommand(sender, command, label, ObjectUtils.slice(args, 1));
                break;

            case "info":
                new PunishmentInfo().onCommand(sender, command, label, ObjectUtils.slice(args, 1));
                break;
        }



        return true;
    }
}
