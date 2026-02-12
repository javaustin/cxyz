package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Debug implements CommandExecutor {

    public enum DebugValue {
        MESSAGE_PARSER,
        FAILED_REQUESTS,
        ALL_REQUESTS,
        SHORTHAND_COMMANDS,
        PUNISHMENT,
        USER,
        PARTY,
        PLAYER_MESSAGE,
        FRIEND_REQUEST,
        COSMETIC,
        MAP
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /debug <message_parser | failed_requests | all_requests | shorthand_commands | punishment | user | party | player_message | friend_request>
            /debug message_parser
            /debug
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.admin.debug";
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

            MessageUtils.sendParsedMessage(sender, MessageKey.DEBUG_UNSET, Map.of("value", args[0].toUpperCase()));
            return true;
        }

        // We've already ensured the string points to a valid debug value, so we now know it's a DebugValue not enabled yet. So enable it!


        enabledDebugs.add(stringValue);

        configYaml.set("debugger.enabled-values", enabledDebugs);
        plugin.saveConfig();

        MessageUtils.sendParsedMessage(sender, MessageKey.DEBUG_SET, Map.of("value", args[0].toUpperCase()));
        return true;

    }


}
