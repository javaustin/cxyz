package com.carrotguy69.cxyz.cmd.general.friend;

import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.other.ObjectUtils.slice;

public class _FriendExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.friend";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.FRIEND_AVAILABLE_SUBCOMMANDS, Map.of());
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "add":
                new Add().onCommand(sender, command, s, slice(args, 1));
                break;

            case "accept":
                new Accept().onCommand(sender, command, s, slice(args, 1));
                break;

            case "list":
                new List_().onCommand(sender, command, s, slice(args, 1));
                break;

            case "deny":
                new Deny().onCommand(sender, command, s, slice(args, 1));
                break;

            case "remove":
                new Remove().onCommand(sender, command, s, slice(args, 1));
                break;

            default:
                new Add().onCommand(sender, command, s, args);
        }

        return true;
    }
}
