package com.carrotguy69.cxyz.cmd.data.networkplayer;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class NetworkPlayerGetAsync implements CommandExecutor {
    public static CommandExecutor executor = new NetworkPlayerGetAsync();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.networkplayer.get-async";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        return false;
    }
}
