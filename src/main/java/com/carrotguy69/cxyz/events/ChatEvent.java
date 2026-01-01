package com.carrotguy69.cxyz.events;

import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent {

    public static void handleChat(AsyncPlayerChatEvent e) {
        // Mind e.getCancelled(bool) in this inner function. We don't want to cancel the event.
        // We'll want to refactor it and just set the message to blank, once we are done testing out channels.

        Player p = e.getPlayer();
        String content = e.getMessage();

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        BaseChannel chatChannel = null; // We only set a chatChannel after we confirmed that the player did not use a trigger prefix.

        boolean triggered = false;

        for (BaseChannel channel : BaseChannel.getAllChannels()) {
            if (channel.getTriggerPrefix() != null && !channel.getTriggerPrefix().isBlank() && content.startsWith(channel.getTriggerPrefix()) && np.canAccessChannel(channel)) {
                chatChannel = channel;
                triggered = true;
                content = content.substring(1, content.length() - 1);
                // If the user's message starts with a trigger prefix.
            }
        }

        if (!triggered) { // If a trigger prefix has not been used in the message, we will determine the chat channel as normal.
            chatChannel = np.getChatChannel();
        }

        e.setCancelled(true);


        BaseChannel ch = ChannelRegistry.getChannelByName(chatChannel.getName());


        if (ch == null) {
            throw new RuntimeException(String.format("Could not find channel in ChannelDefaults.getChannelByName(\"%s\")", chatChannel.getName()));
        }

        ch.onChat(e);
    }
}
