package com.carrotguy69.cxyz.cmd.admin.xp;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.other.utils.ObjectUtils.slice;

public class _XPExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /xp <add | remove | set | view> <amount> [player]
            /xp add 50 Steve
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.xp";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
        }

        String value = "default";
        if (args.length > 0) {
            value = args[0];
        }

        switch (value) {
            case "add":
                new XPAdd().onCommand(sender, command, s, slice(args, 1));
                break;

            case "remove":
                new XPRemove().onCommand(sender, command, s, slice(args, 1));
                break;

            case "set":
                new XPSet().onCommand(sender, command, s, slice(args, 1));
                break;

            default:
                new XPView().onCommand(sender, command, s, slice(args, 1));
                break;
        }

        return true;
    }

}
