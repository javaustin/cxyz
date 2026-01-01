package com.carrotguy69.cxyz.cmd.admin.rank;

import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.other.ObjectUtils.slice;

public class _RankExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.rank";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {

            case "set":
                return new Set().onCommand(sender, command, s, slice(args, 1));
            case "list":
                return new List_().onCommand(sender, command, s, slice(args, 1));
            default:
                return new View().onCommand(sender, command, s, slice(args, 1));
        }
    }

}
