package com.carrotguy69.cxyz.cmd.data.networkplayer;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class NetworkPlayerGet implements CommandExecutor {

    public static CommandExecutor executor = new NetworkPlayerGet();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.networkplayer.get";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "uuid/username, attribute"));
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

        /*
        ex:
        /np get cerrot -> NetworkPlayer{uuid=...,username=...} (use toString() method)
        /np get cerrot ranks -> ["default", "vip"]
        /np get cerrot username -> cerrot
        */

        String fieldName = "";
        String value;

        if (args.length >= 2)
            fieldName = args[1];

        if (fieldName.isEmpty()) {
            value = np.toString();
        }

        else {

            try {
                Field f = NetworkPlayer.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                value = f.get(np).toString();
            }
            catch (NoSuchFieldException e) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_ATTRIBUTE, Map.of("attribute", fieldName));
                return true;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.putAll(
                Map.of(
                        "value", value == null ? "null" : value,
                        "attribute", fieldName.isBlank() ? "*" : fieldName
                        )
        );

        MessageUtils.sendParsedMessage(
                sender,
                MessageGrabber.grab(MessageKey.DATA_NETWORKPLAYER_GET),
                commonMap
        );


        return false;
    }
}
