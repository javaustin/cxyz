package com.carrotguy69.cxyz.tabCompleters;

import com.carrotguy69.cxyz.classes.models.db.FriendRequest;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.friendRequests;

public class Friend implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return List.of();
        }

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());

        List<String> visibleUsernames = OnlinePlayer.getVisibleUsernames(sender, np);

        List<String> subcommands = List.of("add", "accept", "deny", "list");

        if (args.length == 0) {
            // assume they want a player

            return visibleUsernames;
        }

        if (args.length == 1) {
            List<String> results = new ArrayList<>();

            for (String username : visibleUsernames) {
                if (username.toLowerCase().startsWith(args[0].toLowerCase())) {
                    NetworkPlayer otherNP = NetworkPlayer.getPlayerByUsername(username); // We must check if we are already friends with this user. If we are, we shouldn't recommend.

                    if (otherNP == null)
                        continue;

                    if (otherNP.isFriendsWith(np))
                        continue;

                    if (otherNP.getUUID().equals(np.getUUID()))
                        continue;


                    results.add(username);
                }
            }

            if (results.isEmpty()) { // return subcommands if nothing matches
                for (String subcommand : subcommands) {
                    if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                        results.add(subcommand);
                    }
                }
            }

            return results;
        }

        if (args.length == 2) {

            List<String> results = new ArrayList<>();

            switch (args[1].toLowerCase()) {
                case "add":

                    for (String username : visibleUsernames) {
                        if (username.toLowerCase().startsWith(args[1].toLowerCase())) {
                            NetworkPlayer otherNP = NetworkPlayer.getPlayerByUsername(username); // We must check if we are already friends with this user. If we are, we shouldn't recommend.

                            if (otherNP == null)
                                continue;

                            if (otherNP.isFriendsWith(np))
                                continue;

                            if (otherNP.getUUID().equals(np.getUUID()))
                                continue;


                            results.add(username);
                        }
                    }

                    break;

                case "accept":
                case "deny":
                    // This is a little confusing, but the player typing this command would be doing so from the POV of a recipient.
                    // The sender sends them a request, and they (the recipient) can accept the request.

                    List<String> playerRequests = new ArrayList<>(); // This represents any sender that has sent a friend request to the recipient

                    for (Map.Entry<UUID, FriendRequest> entry : friendRequests.entries()) {
                        // If the recipient is in any one of these values, that means the sender should be added to this list

                        FriendRequest req = entry.getValue();

                        if (req.getRecipient().getUUID().equals(np.getUUID()) && req.getExpireTimestamp() > TimeUtils.unixTimeNow())
                            playerRequests.add(req.getSender().getDisplayName());

                    }

                    for (String username : playerRequests) {
                        if (username.toLowerCase().startsWith(args[1].toLowerCase())) {
                            results.add(username);
                        }
                    }


                    break;

                case "remove":
                    List<String> friends = new ArrayList<>();

                    for (NetworkPlayer friendlyPlayer : np.getFriends()) {
                        friends.add(friendlyPlayer.getDisplayName());
                    }

                    for (String username : friends) {
                        if (username.toLowerCase().startsWith(args[1].toLowerCase())) {
                            results.add(username);
                        }
                    }

                    break;



                case "list":
                    return null;

                default:
                    return results; // unsure if this will work,
                                    // the idea is that i dont trust return statements inside a switch, so im explicitly breaking out of the switch cases, and then finally returning
            }
        }

        return null;
    }
}
