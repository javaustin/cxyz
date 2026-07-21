package com.carrotguy69.cxyz.cmd.data.messages;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _MessageDataExecutor implements CommandExecutor {

    public static CommandExecutor executor = new _MessageDataExecutor();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
        (alias: /np)
                [-1]      [0]                 [1]                          [2]
            /message   get        <from_sender / from_recipient>     <uuid / username>
            /message   get-async  <from_sender / from_recipient>     <uuid / username>
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.message";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "get":
                MessageGet.executor.onCommand(sender, command, s, slice(args, 1));
                break;

            case "get-async":
                MessageGetAsync.executor.onCommand(sender, command, s, slice(args, 1));
                break;
        }

        return true;
    }

}
