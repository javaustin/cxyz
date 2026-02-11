package com.carrotguy69.cxyz.cmd.mod.punishment;

import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.*;

public class PunishmentClear implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /punishment clear <player>
            /punishment clear Alex
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.mod.punishment.clear";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        String username = sender.getName();

        if (args.length >= 1) {
            username = args[0];
        }

        NetworkPlayer target = NetworkPlayer.getPlayerByUsername(username);

        if (target == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", username));
            return true;
        }

        List<Long> toRemove = new ArrayList<>();

        for (Map.Entry<Long, Punishment> entry : punishmentIDMap.entrySet()) {
            if (Objects.equals(entry.getValue().getUUID(), target.getUUID().toString())) {
                toRemove.add(entry.getKey());
            }
        }

        for (long l : toRemove) {
            punishmentIDMap.remove(l);
        }

        Map<String, Object> commonMap = MapFormatters.playerFormatter(target);

        MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_CLEAR, commonMap);

        Request.postRequest(apiEndpoint + "/punishment/clear", gson.toJson(Map.of("uuid", target.getUUID().toString())));

        return true;
    }
}
