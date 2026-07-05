package com.carrotguy69.cxyz.cmd.data.networkplayer;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class NetworkPlayerSet implements CommandExecutor {

    public static CommandExecutor executor = new NetworkPlayerSet();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.networkplayer.set";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "uuid/username, attribute, value"));
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
        /np set cerrot nickname auburn -> auburn
        */

        if (args.length == 1) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "attribute, value"));
            return true;
        }

        if (args.length == 2) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "value"));
            return true;
        }

        String fieldName = args[1];
        String value = String.join(" ", ObjectUtils.slice(args, 2));

        if (value.equalsIgnoreCase("null")) {
            value = "";
        }

        try {
            Field f = NetworkPlayer.class.getDeclaredField(fieldName);
            f.setAccessible(true);

            Object v = ObjectUtils.castFromString(f, value);

            f.set(np, v);

            np.sync();
        }
        catch (NoSuchFieldException e) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_ATTRIBUTE, Map.of("attribute", fieldName));
            return true;
        }
        catch (NumberFormatException e) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", value));
            return true;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.putAll(
                Map.of(
                        "value", value,
                        "attribute", fieldName
                )
        );

        MessageUtils.sendParsedMessage(
                sender,
                MessageGrabber.grab(MessageKey.DATA_NETWORKPLAYER_SET),
                commonMap
        );


        return false;
    }
}
