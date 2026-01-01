package com.carrotguy69.cxyz.cmd.general;

import com.carrotguy69.cxyz.classes.models.config.Cosmetic;
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


public class Equip implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.equip";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_COSMETIC, Map.of());
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
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_COSMETIC, Map.of());
            return true;
        }


        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        if (!(np.hasOwnedCosmetic(cosmetic))) {
            MessageUtils.sendParsedMessage(p, MessageKey.EQUIP_COSMETIC_NOT_OWNED, MapFormatters.cosmeticFormatter(cosmetic));
            return true;
        }

        if (np.hasEquippedCosmetic(cosmetic)) {
            MessageUtils.sendParsedMessage(p, MessageKey.EQUIP_COSMETIC_ALREADY_EQUIPPED, MapFormatters.cosmeticFormatter(cosmetic));
            return true;
        }

        np.equipCosmetic(cosmetic);

        MessageUtils.sendParsedMessage(p, MessageKey.EQUIP_COSMETIC_SUCCESS, MapFormatters.cosmeticFormatter(cosmetic));

        return true;
    }
}
