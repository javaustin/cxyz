package com.carrotguy69.cxyz.cmd.mod.punishment;

import com.carrotguy69.cxyz.messages.utils.PageGenerator;
import com.carrotguy69.cxyz.models.db.Punishment;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PunishmentHistory implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /punishment history <player>
            /punishment history Notch
        */

        if (CommandRestrictor.handleRestricted(command, sender))
            return true;

        String node = "cxyz.mod.punishment.history";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0 && !(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
            return true;
        }

        String username = sender.getName();
        int page = 1;

        if (args.length >= 1) {
             username = args[0];
        }

        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[1]));
                return true;
            }
        }


        viewHistory(sender, username, page);

        return true;
    }

    private void viewHistory(CommandSender sender, String targetPlayer, int page) {
        NetworkPlayer np = NetworkPlayer.getPlayerByUsername(targetPlayer);

        if (np == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", targetPlayer));
            return;
        }

        List<Punishment> punishments = Punishment.getPlayerPunishments(np);
        punishments.sort(Comparator.comparing(Punishment::getIssuedTimestamp));

        Map<String, Object> commonMap = new HashMap<>(MapFormatters.playerFormatter(np));
        String format = MessageGrabber.grab(MessageKey.PUNISHMENT_HISTORY_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.PUNISHMENT_HISTORY_SEPARATOR);

        if (punishments.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PUNISHMENT_HISTORY_LIST_NONE, commonMap);
            return;
        }

        MapFormatters.ListFormatter formatter = MapFormatters.punishmentListFormatter(sender, punishments, format, delimiter);
        commonMap.putAll(formatter.getFormatMap());

        String unparsed = MessageGrabber.grab(MessageKey.PUNISHMENT_HISTORY_LIST);

        PageGenerator generator = new PageGenerator(formatter.getEntries(), delimiter, 5);

        int min = 1;
        int max = generator.getMaxPages();

        if (page <= 0 || page > generator.getMaxPages()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PAGE, Map.of("min", min, "max", max));
            return;
        }

        unparsed = unparsed.replace("{punishments}", !punishments.isEmpty() ? generator.generatePage(page) : "None");

        commonMap.put("page", page);
        commonMap.put("previous-page", page - 1);
        commonMap.put("next-page", page + 1);
        commonMap.put("max-pages", max);


        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
    }

}
