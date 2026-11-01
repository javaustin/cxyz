package com.carrotguy69.cxyz.cmd.cosmetic;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.cosmetics;


public class CosmeticUnequip implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /cosmetic unequip <item>
            /cosmetic unequip example_tag
        */

        String node = "cosmetic.unequip";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "item"));
            return true;
        }


        Player p = (Player) sender;

        String cosmeticName = args[0].toLowerCase();

        List<String> cosmeticIDs = new ArrayList<>();

        for (Cosmetic c : cosmetics) {
            cosmeticIDs.add(c.getId().toLowerCase());
        }

        if (!cosmeticIDs.contains(cosmeticName)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_COSMETIC, Map.of("input", args[0]));
            return true;
        }

        Cosmetic cosmetic = Cosmetic.getCosmetic(cosmeticName);

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        if (cosmetic == null) {
            MessageUtils.sendParsedMessage(p, MessageKey.INVALID_COSMETIC, Map.of("input", cosmeticName));
            return true;
        }

        if (!(np.hasOwnedCosmetic(cosmetic))) {
            MessageUtils.sendParsedMessage(p, MessageKey.COSMETIC_EQUIP_ERROR_NOT_OWNED, MapFormatters.cosmeticFormatter(cosmetic));
            return true;
        }

        if (!np.hasEquippedCosmetic(cosmetic)) {
            MessageUtils.sendParsedMessage(p, MessageKey.COSMETIC_UNEQUIP_ERROR_NOT_EQUIPPED, MapFormatters.cosmeticFormatter(cosmetic));
            return true;
        }

        np.unEquipCosmetic(cosmetic, true);
        // NetworkPlayer handles the runtime cleanup, including the ActiveCosmetic wrapper.
        np.sync();

        // in case there is a chat tag or rank thing
        np.updateDisplayNames();

        MessageUtils.sendParsedMessage(p, MessageKey.COSMETIC_UNEQUIP_SUCCESS, MapFormatters.cosmeticFormatter(cosmetic));

        return true;
    }
}
