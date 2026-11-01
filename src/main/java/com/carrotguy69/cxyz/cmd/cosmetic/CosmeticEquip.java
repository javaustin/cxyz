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

import java.util.List;
import java.util.Map;



public class CosmeticEquip implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // Validate the request, confirm ownership, then create and activate the runtime cosmetic.
        /*
        SYNTAX:
            /cosmetic equip <item>
            /cosmetic equip example_tag
        */


        String node = "cosmetic.equip";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "item"));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;

        String cosmeticName = args[0].toLowerCase();

        Cosmetic cosmetic = Cosmetic.getCosmetic(cosmeticName);


        if (cosmetic == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_COSMETIC, Map.of("input", args[0]));
            return true;
        }

        Map<String, Object> commonMap = MapFormatters.cosmeticFormatter(cosmetic);

        NetworkPlayer np = NetworkPlayer.resolvePlayer(p.getUniqueId());

        commonMap.putAll(MapFormatters.playerFormatter(np));

        if (!(np.hasOwnedCosmetic(cosmetic))) {
            MessageUtils.sendParsedMessage(p, MessageKey.COSMETIC_EQUIP_ERROR_NOT_OWNED, commonMap);
            return true;
        }

        if (np.hasEquippedCosmetic(cosmetic)) {
            MessageUtils.sendParsedMessage(p, MessageKey.COSMETIC_EQUIP_ERROR_ALREADY_EQUIPPED, commonMap);
            return true;
        }

        if (np.hasEquippedCosmeticOfType(cosmetic.getType()) && !List.of(Cosmetic.CosmeticType.GADGET, Cosmetic.CosmeticType.WEARABLE).contains(cosmetic.getType())) {
            np.unEquipCosmeticOfType(cosmetic.getType(), true);
        }


        if (cosmetic.getType() == Cosmetic.CosmeticType.RANK_PLATE && cosmetic.getRequiredRank().getHierarchy() > np.getTopRank().getHierarchy()) {
            commonMap.putAll(MapFormatters.cloneFormaterToNewKey(MapFormatters.rankFormatter(cosmetic.getRequiredRank()), "rank", "cosmetic-required-rank"));
            MessageUtils.sendParsedMessage(p, MessageKey.COSMETIC_BUY_ERROR_INSUFFICIENT_RANK, commonMap);
            return true;
        }

        np.equipCosmetic(cosmetic);
        np.sync();

        // in case there is a chat tag or rank thing
        np.updateDisplayNames();

        MessageUtils.sendParsedMessage(p, MessageKey.COSMETIC_EQUIP_SUCCESS, commonMap);

        return true;
    }

}
