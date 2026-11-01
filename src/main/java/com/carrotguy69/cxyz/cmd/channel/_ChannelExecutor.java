package com.carrotguy69.cxyz.cmd.channel;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _ChannelExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /channel <set | ignore | ignorelist | lock | unignore | unlock> <channel>
            /channel <channel>
            /channel ignore public
            /channel unignore parties
            /channel public
        */

        String node = "channel";

        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;



        String subcommand = "";

        if (args.length > 0)
            subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "ignore":
                new ChannelIgnore().onCommand(sender, command, s, slice(args, 1));
                break;

            case "ignorelist":
                new ChannelIgnoreList().onCommand(sender, command, s, slice(args, 1));
                break;

            case "unignore":
                new ChannelUnignore().onCommand(sender, command, s, slice(args, 1));
                break;

            case "set":
                 new ChannelSet().onCommand(sender, command, s, slice(args, 1));
                 break;

            case "lock":
                new ChannelLock().onCommand(sender, command, s, slice(args, 1));
                break;

            case "unlock":
                new ChannelUnlock().onCommand(sender, command, s, slice(args, 1));
                break;

            default:
                new ChannelSet().onCommand(sender, command, s, args);
                break;
        }

        return true;
    }
}
