package com.carrotguy69.cxyz.cmd.ignore;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class _IgnoreExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /ignore <player>
            /unignore <player>
            /ignore <page>
        */

        String node = "channel";

        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (args.length == 0) {
            new IgnoreList().onCommand(sender, command, s, args);
            return true;
        }

        // if the first argument is an integer, we will use the ignore list command.
        try {
            Integer.parseInt(args[0]);

            new IgnoreList().onCommand(sender, command, s, args);
        }
        catch (NumberFormatException e) {
            new Ignore().onCommand(sender, command, s, args);
        }


        return true;
    }
}
