package com.carrotguy69.cxyz.cmd.rank;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _RankExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /rank <add | list | remove | view>
            /rank add <rank> [player]
            /rank view [player]
        */

        String node = "rank";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.RANK_AVAILABLE_SUBCOMMANDS, Map.of());
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {

            case "add":
                return new RankAdd().onCommand(sender, command, s, slice(args, 1));
            case "remove":
                return new RankRemove().onCommand(sender, command, s, slice(args, 1));
            case "list":
                return new RankList().onCommand(sender, command, s, slice(args, 1));

            default:
                return new RankList().onCommand(sender, command, s, args);
        }
    }

}
