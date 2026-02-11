package com.carrotguy69.cxyz.cmd.general.privacy.ignore;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
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

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.channel";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

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
