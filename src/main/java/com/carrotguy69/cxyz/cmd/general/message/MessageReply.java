package com.carrotguy69.cxyz.cmd.general.message;

import com.carrotguy69.cxyz.models.db.Message;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class MessageReply implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.message.reply";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_CONTENT, Map.of());

            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;

        String content = String.join(" ", args);

        replyMessage(p, content);

        return true;
    }

    public static void replyMessage(Player senderPlayer, String content) {
        Message lastMessage = Message.getLastReplyableMessage(senderPlayer.getUniqueId());

        if (lastMessage == null || TimeUtils.unixTimeNow() - 300 > lastMessage.getTimestamp()) { // The reply window expired
            MessageUtils.sendParsedMessage(senderPlayer, MessageKey.MESSAGE_REPLY_FAIL, Map.of());

            return;
        }

        // This is the sender of the message we are referencing the reply from.
        // In other words it is who we are replying to - our target player.
        NetworkPlayer target = NetworkPlayer.getPlayerByUUID(lastMessage.getSenderUUID());
        MessageSend.sendMessage(NetworkPlayer.getPlayerByUUID(senderPlayer.getUniqueId()), target.getDisplayName(), content, true);

    }

}
