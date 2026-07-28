package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;

public class Sudo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }

        String node = "cxyz.sudo";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", "node"));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player, command"));
            return true;
        }

        if (args.length == 1) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "command"));
            return true;
        }

        NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[0]);

        if (np == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[0]));
            return true;
        }

        if (np.getPlayer() == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(np));
            return true;
        }

        String line = String.join(" ", ObjectUtils.slice(args, 1));

        boolean chat = line.toLowerCase().startsWith("c:");


        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("command", line);
        commonMap.put("message", line.substring(2));

        MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.valueOf("SUDO" + (chat ? "_MESSAGE" : ""))), commonMap);

        if (chat) {
            // can i simulate a chat event just by creating one? do i have to register it or something?
            Event e = new AsyncPlayerChatEvent(false, np.getPlayer(), line.substring(2), new HashSet<>(Bukkit.getOnlinePlayers()));
            Bukkit.getPluginManager().callEvent(e);
        }
        else {
            Bukkit.dispatchCommand(np.getPlayer(), line);
        }

        return true;
    }
}
