package com.carrotguy69.cxyz.cmd.general.party;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.other.utils.ObjectUtils.slice;

public class _PartyExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /party <chat | create | disband | delete | invite | join | leave | list | public | private | remove | setleader | warp>
            /friend accept Notch
        */

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
                return new PartyChat().onCommand(sender, command, s, slice(args, 1));
            case "create":
                return new PartyCreate().onCommand(sender, command, s, slice(args, 1));
            case "disband":
            case "delete":
                return new PartyDisband().onCommand(sender, command, s, slice(args, 1));
            case "invite":
                return new PartyInvite().onCommand(sender, command, s, slice(args, 1));
            case "join":
                return new PartyJoin().onCommand(sender, command, s, slice(args, 1));
            case "leave":
                return new PartyLeave().onCommand(sender, command, s, slice(args, 1));
            case "list":
                return new PartyList().onCommand(sender, command, s, slice(args, 1));
            case "public":
            case "setpublic":
                return new PartyPublic().onCommand(sender, command, s, slice(args, 1));
            case "private":
            case "setprivate":
                return new PartyPrivate().onCommand(sender, command, s, slice(args, 1));
            case "remove":
            case "kick":
                return new PartyRemove().onCommand(sender, command, s, slice(args, 1));
            case "setleader":
            case "promote":
            case "transfer":
                return new PartySetLeader().onCommand(sender, command, s, slice(args, 1));
            case "warp":
                return new PartyWarp().onCommand(sender, command, s, slice(args, 1));

            default:
                return new PartyInvite().onCommand(sender, command, s, args);
        }
    }
}
