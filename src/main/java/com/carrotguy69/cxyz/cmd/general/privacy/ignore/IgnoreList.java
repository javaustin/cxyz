package com.carrotguy69.cxyz.cmd.general.privacy.ignore;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.msgYML;

public class IgnoreList implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /ignorelist [page]
            /ignorelist 1
        */

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.ignore.list";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (!(sender instanceof Player)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        Player p = (Player) sender;
        NetworkPlayer sp = NetworkPlayer.getPlayerByUUID(p.getUniqueId());

        int page = 1;
        try {
            if (args.length > 0) {
                page = Integer.parseInt(args[0]);
            }
        }
        catch (NumberFormatException ignored) {}

        ignoreList(sender, sp, page);

        return true;
    }

    public static void ignoreList(CommandSender sender, NetworkPlayer sp, int page) {
        List<NetworkPlayer> ignored = sp.getIgnoreList();
        Map<String, Object> commonMap = MapFormatters.playerFormatter(sp);

        if (ignored.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.IGNORE_LIST_NONE, commonMap);
            return;
        }

        String format = MessageGrabber.grab(MessageKey.IGNORE_LIST_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.IGNORE_LIST_SEPARATOR);

        int maxEntriesPerPage = msgYML.getInt(MessageKey.IGNORE_LIST_MAX_ENTRIES.getPath(), -1);

        MapFormatters.ListFormatter formatter = MapFormatters.playerListFormatter(ignored, format, delimiter, maxEntriesPerPage, page);
        commonMap.putAll(formatter.getFormatMap());

        String unparsed = MessageGrabber.grab(MessageKey.IGNORE_LIST);

        int min = 1;
        int max = formatter.getMaxPages();

        if (page < 1 || page > max) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PAGE, Map.of("min", min, "max", max, "page", page));
            return;
        }

        unparsed = unparsed.replace("{ignored-players}", !formatter.getEntries().isEmpty() ? formatter.generatePage(page) : "None");

        commonMap.put("page", page);
        commonMap.put("previous-page", page - 1);
        commonMap.put("next-page", page + 1);
        commonMap.put("max-pages", max);

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
    }
}
