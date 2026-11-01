package com.carrotguy69.cxyz.cmd.punishment.manager;

import com.carrotguy69.cxyz.utils.ObjectUtils;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
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
            /punishment <clear | delete | edit | history | info> ...
        */

        String node = "punishment";
       if (!sender.hasPermission("cxyz." + node)) {
           MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
           return true;
       }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_AVAILABLE_SUBCOMMANDS, Map.of());
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
