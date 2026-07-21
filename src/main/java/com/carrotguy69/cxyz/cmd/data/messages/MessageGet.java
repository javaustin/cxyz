package com.carrotguy69.cxyz.cmd.data.messages;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.Message;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class MessageGet implements CommandExecutor {

    public static CommandExecutor executor = new MessageGet();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.data.message.get";

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

        if (np == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_QUERY, Map.of("key", args[1]));
            return true;
        }

        Collection<Message> messages;

        if (fromSender) {
            messages = np.getSentMessages();
        }
        else {
            messages = np.getReceivedMessages();
        }

        String value = messages.toString();

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.putAll(
                Map.of(
                        "value", value == null ? "null" : value,
                        "attribute", fromSender ? "as sender" : "as recipient"
                        )
        );

        MessageUtils.sendParsedMessage(
                sender,
                MessageGrabber.grab(MessageKey.DATA_MESSAGE_GET),
                commonMap
        );


        return true;
    }
}
