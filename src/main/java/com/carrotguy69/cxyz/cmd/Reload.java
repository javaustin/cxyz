package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Constants;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.plugin;

public class Reload implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        /*
        SYNTAX:
            /reload
        */

        String node = "reload";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        long before = System.currentTimeMillis();

        try {
            reload();
        }
        catch (RuntimeException ex) {
            Map<String, Object> commonMap = Map.of("error", ex.getMessage());
            MessageUtils.sendParsedMessage(sender, MessageKey.RELOAD_FAIL, commonMap);

            throw new RuntimeException(ex);
        }

        long now = System.currentTimeMillis();

        Map<String, Object> commonMap = Map.of("time", now - before);

        MessageUtils.sendParsedMessage(sender, MessageKey.RELOAD_SUCCESS, commonMap);

        return true;
    }

    public static void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        plugin.getConfig().options().copyDefaults(true); // Copies default values (values that are not set by user.) Keep this as it will be useful for version changes.

        plugin.saveConfig();

        Constants.loadConstants();

        for (NetworkPlayer np : Bukkit.getOnlinePlayers().stream().map(p -> NetworkPlayer.resolvePlayer(p.getUniqueId())).toList()) {
            // (The following code is taken from the JoinEvent class).

            // Physically unequip all cosmetics, then re-equip the ones listed in equipped_cosmetics.
            np.unEquipAllCosmetics(false);

            // Since we did not update the state of `equipped_cosmetics` (by the false flag in the above line), the getEquippedCosmetics method still has the players' equipped cosmetic preferences
            for (Cosmetic cosmetic : np.getEquippedCosmetics()) {
                if (CXYZ.enabledCosmeticTypes.contains(cosmetic.getType())) {
                    np.equipCosmetic(cosmetic);
                }
            }
        }
    }
}
