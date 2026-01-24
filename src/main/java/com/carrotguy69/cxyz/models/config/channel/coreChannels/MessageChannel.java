package com.carrotguy69.cxyz.models.config.channel.coreChannels;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.models.db.Message;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.cmd.general.message.MessageReply;
import com.carrotguy69.cxyz.cmd.general.message.MessageSend;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public class MessageChannel extends CoreChannel {

    public MessageChannel(BaseChannel channel) {
        this.channelName = channel.getName();
        this.channelPrefix = channel.getPrefix();
        this.chatFormat = channel.getChatFormat();
        this.webhookURL = channel.getWebhookURL();
        this.triggerPrefix = channel.getTriggerPrefix();
        this.console = channel.isConsoleEnabled();
        this.ignorable = channel.isIgnorable();
        this.lockable = channel.isLockable();
        this.locked = channel.isLocked();
        this.readOnly = channel.isReadOnly();
        this.aliases = channel.getAliases();
    }

    @Override
    public void onChat(AsyncPlayerChatEvent e) {
        Logger.debugMessage("called MessageChannel.onChat(...)");

        Player p = e.getPlayer();
        String content = e.getMessage();
        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        Map<String, Object> commonMap = MapFormatters.channelFormatter(this);

        if (np.isMutingChannel(this) && this.isIgnorable()) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_IS_MUTED, commonMap);
            return;
        }

        if (this.isLockable() && this.isLocked()) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_IS_LOCKED, commonMap);
            return;
        }

        if (this.isReadOnly()) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_READ_ONLY, commonMap);
            return;
        }

        if (!np.canAccessChannel(this)) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_NO_ACCESS, commonMap);
            return;
        }

        if (np.isMuted()) {
            Punishment punishment = Punishment.getActivePunishment(np, Punishment.PunishmentType.MUTE);

            if (punishment != null) {
                MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PUNISHMENT_MUTE_PLAYER_MESSAGE, MapFormatters.punishmentFormatter(np.getPlayer(), punishment));
                return;
            }
        }


        Message lastMessage = Message.getLastReplyableMessage(np.getUUID());
        // We first will try to get a message that was received by the player. If there are no messages that meet the criteria

        if (lastMessage != null && lastMessage.getTimestamp() > TimeUtils.unixTimeNow() - 300 && !lastMessage.getContent().isBlank()) {
            // We are stricter about which messages one can reply to. They must be sent within 5 minutes of the current time, and they must NOT be blank messages (openers).
            MessageReply.replyMessage(p, content);
            return;
        }

        // There are no messages with content from a recipient to reply to.
        // Maybe the sender send a message that they want to continue the conversation with.

        // Note that the blank messages are only for use by the player that sent that message. So they can open a chat convo.
        // If the recipient player can't access it, then they shouldn't be able to reply to it.

        Message lastSent = Message.getLastSent(np.getUUID()); // This is getting the last message the player sent.

        if (lastSent != null && lastSent.getTimestamp() > TimeUtils.unixTimeNow() - 300 && lastSent.getContent().isBlank()) {
            // We are also strict about which messages one can follow up with. The original messages have been sent within 5 minutes of the current time, and they MUST be blank messages (openers).
            // If the player sent a previous opener message to the player, this will follow it up, and send it as a non-reply message (as in the message itself is not a reply, it is in fact reply-able).
            MessageSend.sendMessage(np, lastSent.getRecipient().getDisplayName(), content, false);
            return;
        }

        // There are no messages to reply to, nor any openers to follow up, move them out of their channel and then send a helpful message.
        np.setChatChannel("public");

        MessageUtils.sendParsedMessage(p, MessageKey.MESSAGE_CHANNEL_CHANGED, Map.of());

    }

    @Override
    public void onSelect(NetworkPlayer np) {
        Logger.debugMessage("called MessageChannel.onSelect(...)");

        Message lastMessage = Message.getLastReplyableMessage(np.getUUID());

        if (lastMessage == null || TimeUtils.unixTimeNow() - 300L > lastMessage.getTimestamp()) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.MESSAGE_REPLY_FAIL, Map.of());
            return;
        }

        np.setChatChannel(this);
        np.sync();

        MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_SET, MapFormatters.channelFormatter(this));

    }

}
