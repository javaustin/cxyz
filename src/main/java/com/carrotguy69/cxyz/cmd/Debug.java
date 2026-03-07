package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Debug implements CommandExecutor {

    public enum DebugValue {
        COSMETIC,
        FRIEND_REQUEST,
        GAME_STAT,
        MAP,
        MESSAGE_PARSER,
        PARTY,
        PLAYER_MESSAGE,
        PUNISHMENT,
        REQUESTS,
        SHORTHAND_COMMANDS,
        USER
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /debug <cosmetic | friend_request | game_stat | map | message_parser | party | player_message | punishment | requests | shorthand_commands | user>
            /debug message_parser
            /debug
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.debug";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.DEBUG_VIEW, Map.of("debuggers", String.join(", ", enabledDebugs)));
            return true;
        }

        String stringValue = args[0].toUpperCase();

        try {
            DebugValue.valueOf(stringValue);
        }
        catch (IllegalArgumentException e) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_DEBUG, Map.of("input", args[0].toUpperCase()));
            return true;
        }

        if (ObjectUtils.containsIgnoreCase(enabledDebugs, stringValue)) {
            enabledDebugs.remove(stringValue);

            configYaml.set("debugger.enabled-values", enabledDebugs);
            plugin.saveConfig();
            plugin.reloadConfig();

            MessageUtils.sendParsedMessage(sender, MessageKey.DEBUG_UNSET, Map.of("value", args[0].toUpperCase()));
            return true;
        }

        // We've already ensured the string points to a valid debug value, so we now know it's a DebugValue not enabled yet. So enable it!


        enabledDebugs.add(stringValue);

        configYaml.set("debugger.enabled-values", enabledDebugs);
        plugin.saveConfig();
        plugin.reloadConfig();

        MessageUtils.sendParsedMessage(sender, MessageKey.DEBUG_SET, Map.of("value", args[0].toUpperCase()));
        return true;

    }


}
