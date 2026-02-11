package com.carrotguy69.cxyz.cmd.general.message;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.config.channel.coreChannels.MessageChannel;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelFunction;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.models.db.Message;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.defaultRank;
import static com.carrotguy69.cxyz.CXYZ.messageMap;

public class MessageSend implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /msg <player> [message]
            /msg Steve hello!
            /msg Steve
        */

        String node = "cxyz.general.message.send";

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;


        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        String targetName;
        String content;


        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player, content"));
            return true;
        }

        // If there are at least two args (One for the targetPlayer, one for the content)
        targetName = args[0];

        if (args.length > 1) {
            content = String.join(" ", ObjectUtils.slice(args, 1));
        }
        else {
            content = null;
        }

        sendMessage(np, targetName, content, false);

        return true;
    }


    public static boolean isMessagable(NetworkPlayer sender, NetworkPlayer recipient) {
        MessageChannel messageChannel = (MessageChannel) ChannelRegistry.getChannelByFunction(ChannelFunction.MESSAGE);


        return
                !recipient.isIgnoring(sender)
                && !recipient.isMutingChannel(messageChannel)
                && !recipient.getMessagePrivacy().equals(NetworkPlayer.MessagePrivacy.DISALLOWED)
                && (!recipient.getMessagePrivacy().equals(NetworkPlayer.MessagePrivacy.FRIENDS_ONLY) || recipient.isFriendsWith(sender));
    }


    public static void sendMessage(NetworkPlayer sender, String targetName, String content, boolean reply) {

        NetworkPlayer recipient = NetworkPlayer.getPlayerByUsername(targetName);

        if (recipient == null) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_NOT_FOUND, Map.of("username", targetName));
            return;
        }

        if (Objects.equals(sender.getUUID(), recipient.getUUID())) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_SELF, Map.of());
            return;
        }

        Map<String, Object> commonMap = MapFormatters.playerSenderFormatter(recipient, sender);

        if (!recipient.isOnline() || (recipient.isOnline() && !recipient.isVisibleTo(sender))) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(recipient));
            return;
        }


        if (sender.isMuted()) {
            Punishment mute = Punishment.getActivePunishment(sender, Punishment.PunishmentType.MUTE);

            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.PUNISHMENT_MUTE_PLAYER_MESSAGE, MapFormatters.punishmentFormatter(sender.getPlayer(), mute));
            return;
        }


        if (Objects.equals(sender.getTopRank().getName(), defaultRank.getName())) { // update when more robust perks/permissions happen
            String stripped = ChatColor.stripColor(content);
            commonMap.put("content", stripped);
        }
        else {
            commonMap.put("content", content);
        }

        if (!isMessagable(sender, recipient) && !reply) {
            MessageUtils.sendParsedMessage(sender.getPlayer(), MessageKey.MESSAGE_FAIL, commonMap);
            return;
        }

        Message msg = new Message(sender, recipient, content == null ? "" : content);

        msg.submit();


        messageMap.put(recipient.getUUID(), msg);

        if (content != null) {
            // Using the NetworkPlayer version because we may need to send it cross server!
            sender.sendParsedMessage(MessageGrabber.grab(MessageKey.MESSAGE_SENT), commonMap);
            recipient.sendParsedMessage(MessageGrabber.grab(MessageKey.MESSAGE_RECEIVED), commonMap);
        }

        else { // Content is null

            sender.setChatChannel(Objects.requireNonNull(ChannelRegistry.getChannelByFunction(ChannelFunction.MESSAGE)));
            sender.sendParsedMessage(MessageGrabber.grab(MessageKey.MESSAGE_OPENED), commonMap);

            sender.sync();
        }
    }
}
