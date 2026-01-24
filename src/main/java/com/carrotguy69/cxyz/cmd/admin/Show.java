package com.carrotguy69.cxyz.cmd.admin;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.other.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Show implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /show <player> <text>
            /show Steve Hello World!
        */


        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.admin.show";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        if (args.length == 1) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_CONTENT, Map.of());
            return true;
        }

        String content = String.join(" ", ObjectUtils.slice_(args, 1));
        content = content.replace("\\n", "\n");

        if (args[0].equalsIgnoreCase("console")) {
            MessageUtils.sendParsedMessage(Bukkit.getConsoleSender(), content, Map.of());
            return true;
        }

        NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[0]);

        if (np == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of());
            return true;
        }

        if (!np.isOnline()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(np));
            return true;
        }

        np.sendParsedMessage(String.join(" ", content), Map.of());


        return true;
    }
}
