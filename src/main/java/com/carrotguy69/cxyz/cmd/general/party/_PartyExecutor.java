package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.other.ObjectUtils.slice;

public class _PartyExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.party";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PARTY_AVAILABLE_SUBCOMMANDS, Map.of());
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {

            case "chat":
                return new Chat().onCommand(sender, command, s, slice(args, 1));
            case "create":
                return new Create().onCommand(sender, command, s, slice(args, 1));
            case "disband":
            case "delete":
                return new Disband().onCommand(sender, command, s, slice(args, 1));
            case "invite":
                return new Invite().onCommand(sender, command, s, slice(args, 1));
            case "join":
                return new Join().onCommand(sender, command, s, slice(args, 1));
            case "leave":
                return new Leave().onCommand(sender, command, s, slice(args, 1));
            case "list":
                return new List_().onCommand(sender, command, s, slice(args, 1));
            case "public":
            case "setpublic":
                return new Public().onCommand(sender, command, s, slice(args, 1));
            case "private":
            case "setprivate":
                return new Private().onCommand(sender, command, s, slice(args, 1));
            case "remove":
            case "kick":
                return new Remove().onCommand(sender, command, s, slice(args, 1));
            case "setleader":
            case "promote":
            case "transfer":
                return new SetLeader().onCommand(sender, command, s, slice(args, 1));
            case "warp":
                return new Warp().onCommand(sender, command, s, slice(args, 1));

            default:
                return new Invite().onCommand(sender, command, s, args);
        }
    }
}
