package com.carrotguy69.cxyz.cmd.friend;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _FriendExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /friend <add | accept | deny | remove> <player>
            /friend list
            /friend add Steve
            /friend accept Alex
        */

        String node = "friend";

        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.FRIEND_AVAILABLE_SUBCOMMANDS, Map.of());
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "add":
                new FriendAdd().onCommand(sender, command, s, slice(args, 1));
                break;

            case "accept":
                new FriendAccept().onCommand(sender, command, s, slice(args, 1));
                break;

            case "deny":
                new FriendDeny().onCommand(sender, command, s, slice(args, 1));
                break;

            case "list":
                new FriendList().onCommand(sender, command, s, slice(args, 1));
                break;

            case "remove":
                new FriendRemove().onCommand(sender, command, s, slice(args, 1));
                break;

            default:
                new FriendAdd().onCommand(sender, command, s, args);
        }

        return true;
    }
}
