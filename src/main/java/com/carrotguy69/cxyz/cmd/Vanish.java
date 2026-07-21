package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.events.custom.VanishToggleEvent;
import com.carrotguy69.cxyz.events.custom.service.EventService;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Vanish implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.vanish";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        NetworkPlayer target;
        boolean value;

        if (args.length == 0 && sender instanceof Player) {
            target = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());
            value = !target.isVanished();
        }

        else if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "value, player"));
            return true;
        }

        else if (args.length == 1 && sender instanceof Player) {
            value = ObjectUtils.parseCasualBoolean(args[0]);
            target = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());
        }

        else if (args.length == 1) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        else if (args.length == 2) {
            target = NetworkPlayer.getPlayerByUsername(args[1]);

            if (target == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[1]));
                return true;
            }

            boolean isOtherTarget = !(sender instanceof Player) || !target.getUUID().equals(((Player) sender).getUniqueId());
            if (isOtherTarget && !sender.hasPermission(node + ".others")) {
                MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node + ".others"));
                return true;
            }

            value = ObjectUtils.parseCasualBoolean(args[0]);
        }

        else {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "value, player"));
            return true;
        }

        setVanish(target, value);

        Map<String, Object> commonMap = MapFormatters.playerFormatter(target);
        commonMap.put("toggle", value ? "enabled" : "disabled");

        MessageUtils.sendParsedMessage(sender, MessageKey.valueOf("VANISH_" + (value ? "ENABLE" : "DISABLE")), commonMap);

        VanishToggleEvent event = new VanishToggleEvent(target, value);
        EventService.dispatch(event);
        return true;
    }


    public static void setVanish(NetworkPlayer target, boolean value) {
        target.setVanished(value);

        if (target.getPlayer() == null) {
            return;
        }

        target.getPlayer().setInvisible(value);

        if (value) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(target.getPlayer())) {
                    p.showPlayer(CXYZ.plugin, target.getPlayer());
                    continue;
                }

                NetworkPlayer other = NetworkPlayer.resolvePlayer(p.getUniqueId());

                if (target.isVisibleTo(other)) {
                    p.showPlayer(CXYZ.plugin, target.getPlayer());
                }
                else {
                    p.hidePlayer(CXYZ.plugin, target.getPlayer());
                }
            }
        }
        else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(CXYZ.plugin, target.getPlayer());
            }
        }

    }
}
