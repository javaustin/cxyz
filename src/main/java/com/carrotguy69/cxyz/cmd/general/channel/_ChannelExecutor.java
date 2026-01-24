package com.carrotguy69.cxyz.cmd.general.channel;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


import java.util.Map;

import static com.carrotguy69.cxyz.other.utils.ObjectUtils.slice;

public class _ChannelExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /channel <set | ignore | unignore> <channel>
            /channel <channel>
            /channel ignore public
            /channel unignore parties
            /channel public
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.channel";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String subcommand = "";

        if (args.length > 0)
            subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "ignore":
                new ChannelIgnore().onCommand(sender, command, s, slice(args, 1));
                break;

            case "unignore":
                new ChannelUnignore().onCommand(sender, command, s, slice(args, 1));
                break;

            case "set":
                 new ChannelSet().onCommand(sender, command, s, slice(args, 1));
                 break;

            default:
                new ChannelSet().onCommand(sender, command, s, args);
                break;
        }

        return true;
    }
}
