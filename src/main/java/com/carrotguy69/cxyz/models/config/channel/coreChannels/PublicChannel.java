package com.carrotguy69.cxyz.models.config.channel.coreChannels;

import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.TimeUtils;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class PublicChannel extends CoreChannel {

    public PublicChannel(BaseChannel channel) {
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
        Logger.debugMessage("called PublicChannel.onChat(...)");


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

        commonMap.clear();
        commonMap = MapFormatters.playerFormatter(np);

        commonMap.put("message", ChatColor.stripColor(f(content)));
        commonMap.put("content", ChatColor.stripColor(f(content)));

        if (lastMessage.containsKey(np.getUUID())) { // Cooldown checker
            long remainingSeconds = lastMessage.get(np.getUUID()) - TimeUtils.unixTimeNow() + np.getTopRank().getChatCooldown();

            if (remainingSeconds > 0) {
                MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_COOLDOWN, Map.of("remaining-seconds", remainingSeconds));
                return;
            }
        }

        lastMessage.put(np.getUUID(), TimeUtils.unixTimeNow());


        for (Player pl : Bukkit.getOnlinePlayers()) {

            NetworkPlayer n = NetworkPlayer.getPlayerByUUID(pl.getUniqueId());

            if (n.isMutingChannel(this) && this.isIgnorable()) {
                continue;
            }

            MessageUtils.sendParsedMessage(pl, this.getChatFormat(), commonMap);
        }


    }

    @Override
    public void onSelect(NetworkPlayer np) {
        Logger.debugMessage("called PublicChannel.onSelect(...)");


        np.setChatChannel(this);
        np.sync();

        MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_SET, MapFormatters.channelFormatter(this));

    }


}
