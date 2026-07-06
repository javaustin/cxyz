package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Fly implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // /fly [enable/disable/on/off] [player]

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.fly";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0 && sender instanceof Player) {
            setFlight(sender, NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId()), !((Player) sender).getAllowFlight());
            return true;
        }

        else if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "value, player"));
            return true;
        }

        if (args.length == 1 && sender instanceof Player) {
            setFlight(sender, NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId()), ObjectUtils.parseCasualBoolean(args[0]));
            return true;
        }

        else if (args.length == 1) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        if (args.length == 2 && sender.hasPermission(node + ".others")) {
            NetworkPlayer target = NetworkPlayer.getPlayerByUsername(args[1]);

            if (target == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[1]));
                return true;
            }

            if (target.getPlayer() == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(target));
                return true;
            }

            setFlight(sender, target, ObjectUtils.parseCasualBoolean(args[0]));
        }

        // An edge case may exist where a non player sender attempts to run the command but without permissions. Not sure if I care about fixing it.
        return true;
    }

    private static void setFlight(CommandSender sender, NetworkPlayer target, boolean value) {
        target.getPlayer().setAllowFlight(value);

        Map<String, Object> commonMap = MapFormatters.playerFormatter(target);
        commonMap.put("toggle", value ? "enabled" : "disabled");

        MessageUtils.sendParsedMessage(sender, MessageKey.valueOf("FLY_" + (value ? "ENABLE" : "DISABLE")), commonMap);
    }


}
