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

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        NetworkPlayer np = NetworkPlayer.resolvePlayer(((Player) sender).getUniqueId());

        boolean value = false;
        boolean isSet = false;

        if (args.length >= 1) {
            value = !ObjectUtils.parseCasualBoolean(args[0]);
            isSet = true;
        }

        if (args.length >= 2 && sender.hasPermission(node + ".others")) {
            np = NetworkPlayer.getPlayerByUsername(args[1]);

            if (np == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[1]));
                return true;
            }

            if (np.getPlayer() == null) {
                MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_IS_OFFLINE, MapFormatters.playerFormatter(np));
                return true;
            }
        }

        Player p = np.getPlayer();

        if (!isSet) {
            value = !p.getAllowFlight();
        }

        p.setAllowFlight(value);

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("toggle", value ? "enabled" : "disabled");

        MessageUtils.sendParsedMessage(sender, MessageKey.valueOf("FLY_" + (value ? "ENABLE" : "DISABLE")), commonMap);

        return true;
    }


}
