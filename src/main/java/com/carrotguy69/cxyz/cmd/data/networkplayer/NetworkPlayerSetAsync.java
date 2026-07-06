package com.carrotguy69.cxyz.cmd.data.networkplayer;

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
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.gson;

public class NetworkPlayerSetAsync implements CommandExecutor {
    public static CommandExecutor executor = new NetworkPlayerSetAsync();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.networkplayer.get-async";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "uuid/username, attribute, value"));
            return true;
        }

        if (args.length == 1) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "attribute, value"));
            return true;
        }

        if (args.length == 2) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "value"));
            return true;
        }

        NetworkPlayer np;

        try {
            UUID uuid = UUID.fromString(args[0]);
            np = NetworkPlayer.resolvePlayer(uuid);
        }
        catch (Exception general) {
            np = NetworkPlayer.getPlayerByUsername(args[0]);
        }

        if (np == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_ENTRY, Map.of("key", args[0]));
            return true;
        }


        String fieldName = args[1];
        String value = ObjectUtils.sliceToString(args, 2);

        String url = CXYZ.apiEndpoint + "/user/modify";

        Request req = new Request(RequestType.POST, url, gson.toJson(Map.of(fieldName, value, "version", np.version + 1, "uuid", np.getUUID().toString())));

        NetworkPlayer finalNp = np;
        req.send().thenAccept(res -> {
            if (res.statusCode == 404) { // User not found
                Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND_UUID, Map.of("uuid", finalNp.getUUID().toString())));
                return;
            }

            if (res.statusCode == 400) { // Attribute does not exist or (unlikely) uuid not provided
                Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_ATTRIBUTE, Map.of("attribute", args[1])));
                return;
            }

            if (res.statusCode != 200) { // Represents 500 internal server error
                Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.API_ERROR, Map.of()));
                return;
            }

            Map<String, Object> commonMap = MapFormatters.playerFormatter(finalNp);
            commonMap.putAll(
                    Map.of(
                            "value", ObjectUtils.isParsableAsNumber(value) ? BigDecimal.valueOf(ObjectUtils.parseAs(Double.class, value)).stripTrailingZeros().toPlainString() : value,
                            "attribute", fieldName.isBlank() ? "*" : fieldName
                    )
            );

            Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(
                    sender,
                    MessageGrabber.grab(MessageKey.DATA_NETWORKPLAYER_SET_ASYNC),
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
