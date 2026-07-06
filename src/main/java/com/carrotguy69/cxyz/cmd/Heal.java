package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Heal implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.heal";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        Player p = null;

        if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.MISSING_GENERAL), Map.of("missing-args", "player"));
            return true;
        }

        if (args.length == 0 || !sender.hasPermission(node + ".others")) {
            p = (Player) sender;
        }

        else {
            if (args[0].equalsIgnoreCase("*")) {
                healAll(sender);
                return true;
            }

            NetworkPlayer np = NetworkPlayer.getPlayerByUsername(args[0]);
            if (np != null)
                p = np.getPlayer();

            if (p == null && np != null) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_IS_OFFLINE), MapFormatters.playerFormatter(np));
                return true;
            }

            else if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageGrabber.grab(MessageKey.PLAYER_NOT_FOUND), Map.of("username", args[0]));
                return true;
            }
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        healOne(sender, np);

        return true;
    }

    private static void healOne(CommandSender sender, NetworkPlayer target) {
        Map<String, Object> commonMap = MapFormatters.playerFormatter(target);

        assert target.getPlayer().isOnline();

        AttributeInstance attr = target.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);

        double max = attr != null ? attr.getValue() : 20.0;

        target.getPlayer().setHealth(max);

        commonMap.put("health", String.format("%.1f", max));
        commonMap.put("player-health", String.format("%.1f", max));

        MessageUtils.sendParsedMessage(sender, MessageKey.HEAL, commonMap);
    }

    private static void healAll(CommandSender sender) {

        int amount = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            amount++;
            AttributeInstance attr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);

            double max = attr != null ? attr.getValue() : 20.0;

            p.setHealth(max);
        }

        MessageUtils.sendParsedMessage(sender, MessageKey.HEAL_ALL, Map.of("amount", amount));
    }


}
