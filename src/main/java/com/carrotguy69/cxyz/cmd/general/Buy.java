package com.carrotguy69.cxyz.cmd.general;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.carrotguy69.cxyz.messages.MessageKey.*;

public class Buy implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        /*
        SYNTAX:
            /buy <item>
            /buy example_tag
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.buy";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "item"));
            return true;
        }

        buy((Player) sender, args[0]);

        return true;
    }

    public void buy(Player p, String cosmeticID) {
        // Check if the player has the required rank and level. Then check for coins.
        // Check if the player already has the cosmetic.

        NetworkPlayer np = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        Cosmetic cosmetic = Cosmetic.getCosmetic(cosmeticID);
        if (cosmetic == null) {
            MessageUtils.sendParsedMessage(p, INVALID_ITEM, Map.of("input", cosmeticID));
            return;
        }

        Map<String, Object> formatter = MapFormatters.playerFormatter(np);
        formatter.putAll(MapFormatters.cosmeticFormatter(cosmetic));

        PlayerRank rank = np.getTopRank();
        long level = np.getLevel();
        long coins = np.getCoins();

        if (!cosmetic.isEnabled()) {
            MessageUtils.sendParsedMessage(p, BUY_ERROR_DISABLED_ITEM, formatter);
            return;
        }

        if (np.hasOwnedCosmetic(cosmetic)) {
            MessageUtils.sendParsedMessage(p, BUY_ERROR_DUPLICATE_ITEM, formatter);
            return;
        }

        if (rank.getHierarchy() < cosmetic.getRequiredRank().getHierarchy()) {
            MessageUtils.sendParsedMessage(p, BUY_ERROR_INSUFFICIENT_RANK, formatter);
            return;
        }

        else if (level < cosmetic.getRequiredLevel()) {
            MessageUtils.sendParsedMessage(p, BUY_ERROR_INSUFFICIENT_LEVEL, formatter);
            return;
        }

        else if (coins < cosmetic.getPrice()) {
            MessageUtils.sendParsedMessage(p, BUY_ERROR_INSUFFICIENT_COINS, formatter);
            return;
        }

        np.addOwnedCosmetic(cosmetic);

        np.sync();

        MessageUtils.sendParsedMessage(p, BUY_SUCCESS, formatter);
    }

}
