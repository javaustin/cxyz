package com.carrotguy69.cxyz.cmd.data.networkplayer;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

import static com.carrotguy69.cxyz.utils.ObjectUtils.slice;

public class _NetworkPlayerDataExecutor implements CommandExecutor {

    public static CommandExecutor executor = new _NetworkPlayerDataExecutor();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
        (alias: /np)
                [-1]            [0]             [1]              [2]         [3]
            /networkplayer   get         <uuid / username>   [attribute]
            /networkplayer   get-async   <uuid / username>   [attribute]
            /networkplayer   set         <uuid / username>   <attribute>   <value>
            /networkplayer   set-async   <uuid / username>   <attribute>   <value>
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.networkplayer";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "get":
                NetworkPlayerGet.executor.onCommand(sender, command, s, slice(args, 1));
                break;

            case "get-async":
                NetworkPlayerGetAsync.executor.onCommand(sender, command, s, slice(args, 1));
                break;

            case "set":
                NetworkPlayerSet.executor.onCommand(sender, command, s, slice(args, 1));
                break;

            case "set-async":
                NetworkPlayerSetAsync.executor.onCommand(sender, command, s, slice(args, 1));
                break;
        }

        return true;
    }




}
