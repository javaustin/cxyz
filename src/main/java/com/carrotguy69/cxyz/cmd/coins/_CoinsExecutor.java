package com.carrotguy69.cxyz.cmd.coins;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _CoinsExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /coins <add | remove | set | view> <amount> [player]
            /coins add 50 Steve
        */

        String node = "coins";
       if (!sender.hasPermission("cxyz." + node)) {
           MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
           return true;
       }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        String value = "default";
        if (args.length > 0) {
            value = args[0];
        }

        switch (value) {
            case "add":
                new CoinsAdd().onCommand(sender, command, s, slice(args, 1));
                break;

            case "remove":
                new CoinsRemove().onCommand(sender, command, s, slice(args, 1));
                break;

            case "set":
                new CoinsSet().onCommand(sender, command, s, slice(args, 1));
                break;

            default:
                new CoinsView().onCommand(sender, command, s, slice(args, 1));
                break;
        }

        return true;
    }

}
