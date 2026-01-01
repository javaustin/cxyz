package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Port implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.admin.port";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }


        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PORT_VIEW, Map.of("port", String.valueOf(this_port)));
            return true;
        }

        try {
            int newValue = Integer.parseInt(args[0]);

            configYaml.set("port", newValue);
            instance.saveConfig();
            MessageUtils.sendParsedMessage(sender, MessageKey.PORT_SET, Map.of("port", String.valueOf(newValue)));
        }

        catch (NumberFormatException ex) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[0]));
            return true;
        }

        return true;
    }
}
