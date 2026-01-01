package com.carrotguy69.cxyz.classes.models.config.channel.coreChannels;

import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.CoreChannel;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.TimeUtils;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import com.carrotguy69.cxyz.template.MapFormatters;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.CXYZ.configYaml;

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
        Logger.log("called PublicChannel.onChat(...)");


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
            p.sendMessage(MessageGrabber.grab(MessageKey.CHAT_CHANNEL_NO_ACCESS, commonMap));
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
            if (lastMessage.get(np.getUUID()) > TimeUtils.unixTimeNow() - np.getRank().getChatCooldown()) {

                np.getPlayer().sendMessage(
                        MessageGrabber.grab(
                                "&cPlease wait {remaining-seconds} seconds before sending another message.", // Replace with cooldown message key (update)
                                Map.of("remaining-seconds", TimeUtils.unixTimeNow() - lastMessage.get(np.getUUID()))
                        )
                );

                return;
            }
        }

        lastMessage.put(np.getUUID(), TimeUtils.unixTimeNow());


        for (Player pl : Bukkit.getOnlinePlayers()) {

            NetworkPlayer n = NetworkPlayer.getPlayerByUUID(pl.getUniqueId());

            boolean isIgnorable = configYaml.getBoolean("chat.core-channels.public.ignorable");

            String chatFormat = configYaml.getString("chat.core-channels.public.chat-format", "{player-tag}{player-prefix}{player-color}{player-display-name}: {player-chat-color}{message}");
            if (n.isMutingChannel("public") && isIgnorable) {
                continue;
            }

            MessageUtils.sendParsedMessage(pl, chatFormat, commonMap);
        }


    }

    @Override
    public void onSelect(NetworkPlayer np) {
        Logger.log("called PublicChannel.onSelect(...)");


        np.setChatChannel(this);
        np.sync();

        MessageUtils.sendParsedMessage(np.getPlayer(), MessageKey.CHAT_CHANNEL_SET, MapFormatters.channelFormatter(this));

    }


}
