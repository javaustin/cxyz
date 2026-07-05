package com.carrotguy69.cxyz.cmd.data;

import com.carrotguy69.cxyz.cmd.data.networkplayer._NetworkPlayerDataExecutor;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _DataExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
        (alias: /np)
            /data <type> <get | set | get-async | set-async> <entry-id> [key] [value]
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "subcommand"));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "networkplayer":
                _NetworkPlayerDataExecutor.executor.onCommand(sender, command, s, slice(args, 1));
                break;
        }

        return true;
    }
}
