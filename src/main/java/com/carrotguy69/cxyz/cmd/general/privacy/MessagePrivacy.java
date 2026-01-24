package com.carrotguy69.cxyz.cmd.general.privacy;

import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MessagePrivacy implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /messageprivacy <ALLOWED | DISALLOWED | FRIENDS_ONLY>
            /messageprivacy FRIENDS_ONLY
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.privacy.messageprivacy";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }


        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (args.length == 0) {
            commonMap.put("value", np.getMessagePrivacy().name());

            MessageUtils.sendParsedMessage(sender, MessageKey.MESSAGE_PRIVACY_VIEW, commonMap);
            return true;
        }

        try {
            NetworkPlayer.MessagePrivacy value = NetworkPlayer.MessagePrivacy.valueOf(args[0].toUpperCase());

            np.setMessagePrivacy(value);
            np.sync();

            commonMap.put("value", np.getMessagePrivacy().name());

            MessageUtils.sendParsedMessage(sender, MessageKey.MESSAGE_PRIVACY_SET, commonMap);


        }
        catch (IllegalArgumentException ex) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_MESSAGE_PRIVACY_VALUE, Map.of("input", args[0]));
        }

        return true;
    }


}
