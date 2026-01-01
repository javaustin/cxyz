package com.carrotguy69.cxyz.cmd.admin.xp;

import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class XPView implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // Surprisingly, this whole thing is a little tricky for a simple command.
        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.xp.view";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        //                 0      1
        // Syntax: /xp view {player}

        else if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        else if (args.length == 0) {
            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());

            view(sender, np);

            return true;
        }

        else if (args.length == 1) {

            String nodeOthers = "cxyz.admin.xp.view.others";
            if (!sender.hasPermission(nodeOthers)) {
                view(sender, NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId()));
                return true;
            }

            // This is the only block of code that represents a sender entering a username

            String possibleUsername = args[0];

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(possibleUsername);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", possibleUsername));
                return true;
            }

            view(sender, np);
        }

        return true;
    }

    public static void view(CommandSender sender, NetworkPlayer np) {
        // No need to await a request anymore!

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("xp", np.getXP());
        commonMap.put("amount", np.getXP());

        MessageUtils.sendParsedMessage(sender, MessageKey.XP_VIEW, commonMap);
    }
}
