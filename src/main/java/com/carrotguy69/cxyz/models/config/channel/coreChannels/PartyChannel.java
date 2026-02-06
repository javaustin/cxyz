package com.carrotguy69.cxyz.models.config.channel.coreChannels;

import com.carrotguy69.cxyz.models.config.ChatFilterRule;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Party;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.cmd.general.party.PartyChat;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.chatFilterEnabled;
import static com.carrotguy69.cxyz.CXYZ.f;

public class PartyChannel extends CoreChannel {

    public PartyChannel(BaseChannel channel) {
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
        Logger.debugMessage("called PartyChannel.onChat(...)");

        Player p = e.getPlayer();

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        Map<String, Object> commonMap = MapFormatters.channelFormatter(this);
        commonMap.putAll(MapFormatters.playerFormatter(np));

        String content = ChatColor.stripColor(f(e.getMessage()));

        commonMap.put("content", content);
        commonMap.put("message", content);

        boolean blocked = this.evaluateContent(p, content, commonMap);

        if (blocked)
            return;


        if (!np.canAccessChannel(this)) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_NO_ACCESS, commonMap);
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

        if (np.isMutingChannel(this) && this.isIgnorable()) {
            MessageUtils.sendParsedMessage(p, MessageKey.CHAT_CHANNEL_IS_MUTED, commonMap);
            return;
        }

        if (content.isBlank()) {
            MessageUtils.sendParsedMessage(p, MessageKey.MISSING_CONTENT, commonMap);
            return;
        }

        if (np.isMuted()) {
            Punishment punishment = Punishment.getActivePunishment(np, Punishment.PunishmentType.MUTE);

            if (punishment != null) {
                MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PUNISHMENT_MUTE_PLAYER_MESSAGE, MapFormatters.punishmentFormatter(np.getPlayer(), punishment));
                return;
            }
        }

        PartyChat.chat(e.getPlayer(), content);

    }

    @Override
    public void onSelect(NetworkPlayer np) {
        Logger.debugMessage("called PartyChannel.onSelect(...)");


        Party party = Party.getPlayerParty(np.getUUID());

        if (party == null) {
            MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.PARTY_ERROR_NOT_IN_PARTY, Map.of());
            return;
        }
        Logger.debugParty("party found apparently: " + party);

        np.setChatChannel(this);
        np.sync();

        MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_SET, MapFormatters.channelFormatter(this));

    }


}
