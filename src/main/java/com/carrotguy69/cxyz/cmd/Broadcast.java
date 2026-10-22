package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.http.Request;

import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Broadcast implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /broadcast <message>
            /broadcast &eHello world!
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted("broadcast", sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.broadcast";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String content = String.join(" ", args);

        if (List.of(args).contains("-r")) {
            ObjectUtils.removeItem(args, "-r");

            content = f(content);
        }

        if (content.isBlank()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_CONTENT, Map.of());
            return true;
        }

        broadcast(content, Map.of(), true);

        return true;
    }

    public static void broadcast(String content, Map<String, Object> formatMap, boolean forwardToOtherServers) {
        // Sends an announcement to all players of a server. If `forwardToOtherServers` is true, then this function will initiate requests to other servers.

        if (forwardToOtherServers) {
            // The announcement is sent to the API first, and then the API sends it back to us as a recipient server.
            // I am aware this solution increases latency, but it is better than reworking the backend
            Map<String, Object> map = Map.of("content", content, "format_map", formatMap,"to_players", List.of("*"));
            Request.postRequest(apiEndpoint + "/send_message", gson.toJson(map));
            return;
        }

        TextComponent component = MessageUtils.createMessage(content, Map.of());

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(component);
        }

    }
}
