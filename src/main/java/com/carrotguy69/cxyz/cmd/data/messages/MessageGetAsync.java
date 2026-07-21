package com.carrotguy69.cxyz.cmd.data.messages;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.http.RequestType;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.JsonConverters;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageGetAsync implements CommandExecutor {
    public static CommandExecutor executor = new MessageGetAsync();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.message.get-async";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "context, uuid/username"));
            return true;
        }

        if (args.length == 1) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "uuid/username"));
            return true;
        }

        boolean fromSender = !args[0].equalsIgnoreCase("as_recipient");

        NetworkPlayer np;

        try {
            UUID uuid = UUID.fromString(args[1]);
            np = NetworkPlayer.resolvePlayer(uuid);
        }
        catch (Exception general) {
            np = NetworkPlayer.getPlayerByUsername(args[1]);
        }

        Map<String, Object> requestBody = new HashMap<>();



        if (fromSender) {
            if (np != null)
                requestBody.put("sender_uuid", np.getUUID().toString());
            else
                requestBody.put("sender_uuid", args[1]);
        }
        else {
            if (np != null)
                requestBody.put("recipient_uuid", np.getUUID().toString());
            else
                requestBody.put("recipient_uuid", args[1]);
        }

        String url = CXYZ.apiEndpoint + "/message/query";
        String json = CXYZ.gson.toJson(requestBody);

        Request req = new Request(RequestType.GET, url, json);

        NetworkPlayer finalNp = np;
        req.send().thenAccept(res -> {
            if (res.statusCode == 404) { // User not found
                Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND_UUID, Map.of("uuid", finalNp.getUUID())));
                return;
            }

            if (res.statusCode != 200) { // Represents 500 internal server error
                Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.API_ERROR, Map.of()));
                return;
            }

            Object value = JsonConverters.toMap(res.responseBody);

            Map<String, Object> commonMap = MapFormatters.playerFormatter(finalNp);
            commonMap.putAll(
                    Map.of(
                            "value", value,
                            "attribute", fromSender ? "as sender" : "as recipient"
                    )
            );

            Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(
                    sender,
                    MessageGrabber.grab(MessageKey.DATA_MESSAGE_GET_ASYNC),
                    commonMap
            ));

        }).exceptionally(ex -> {
            Logger.logStackTrace((Exception) ex);
            Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.API_ERROR, Map.of()));
            return null;
        });

        return true;
    }
}
