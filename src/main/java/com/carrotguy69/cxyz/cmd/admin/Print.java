package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.models.config.ActiveCosmetic;
import com.carrotguy69.cxyz.models.config.Announcement;
import com.carrotguy69.cxyz.models.config.channel.utils.ChannelRegistry;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Print implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /print <users | parties | partyinvites | partyexpires | punishments | messages | cosmetics | channels>
            /xp add 50 Steve
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.admin.print";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "users":
                case "user":
                    Logger.info(users.toString());
                    return true;
                case "party":
                case "parties":
                    Logger.info(parties.toString());
                    return true;
                case "partyinvites":
                case "partyinvite":
                    Logger.info(partyInvites.toString());
                    return true;
                case "partyexpires":
                case "partyexpire":
                    Logger.info(partyExpires.toString());
                    return true;
                case "punishment":
                case "punishments":
                    Logger.info(punishmentIDMap.toString());
                    return true;
                case "message":
                case "messages":
                    Logger.info(messageMap.toString());
                    return true;
                case "cosmetic":
                case "cosmetics":
                    Logger.info(cosmetics.toString());
                    Logger.info(ActiveCosmetic.activeCosmeticMap.toString());
                case "channel":
                case "channels":
                    Logger.info(channels.toString());
                    Logger.info(ChannelRegistry.functionalChannels.toString());
                case "announcements":
                case "announcement":
                    Logger.info(Announcement.getAnnouncements().toString());
                case "friendrequest":
                case "friendrequests":
                    Logger.info(friendRequests.toString());

            }
        }


        return true;
    }

}
