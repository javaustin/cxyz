package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.classes.models.config.GameServer;

import com.carrotguy69.cxyz.classes.http.Requests;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Broadcast implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.admin.broadcast";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String content = String.join(" ", args);
        boolean raw = false;

        if (content.contains(" -r")) {
            raw = true;
            content = content.replace(" -r", "");

            content = f(content);
        }

        if (content.isBlank()) {
            // our first trial of the error message
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_CONTENT, Map.of());
            return true;
        }

        broadcast(content, !raw, Map.of());

        return true;
    }

    public static void broadcast(String content, boolean parsed, Map<String, Object> formatMap) {
        for (GameServer server : servers) {

            if (Objects.equals(server.getName(), this_server.getName())) {
                MessageUtils.sendPublicMessage(content, parsed, Map.of());
                continue;
            }

            Map<String, Object> map = Map.of("content", content, "parsed", parsed, "formatMap", formatMap);

            Requests.postRequest(server.getIP() + "/sendPublicMessage", gson.toJson(map));
        }
    }
}
