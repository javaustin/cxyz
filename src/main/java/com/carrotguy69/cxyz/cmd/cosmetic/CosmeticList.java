package com.carrotguy69.cxyz.cmd.cosmetic;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.config.cosmetics.Cosmetic;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import static com.carrotguy69.cxyz.CXYZ.msgYML;

public class CosmeticList implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        /*
        SYNTAX:
            /cosmetic list [owned | equipped | all] [player] [page]
            /cosmetic list owned Notch 3
            /cosmetic list equipped
            /cosmetic list all
            /cosmetic list
        */

        String node = "cxyz.cosmetic.list";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }

        //            [-1]   [0]    [1]      [2]
        // "/cosmetic list [type] [player] [page] "

        boolean isConsole = (!(sender instanceof Player));

        if (args.length == 0) {
            if (isConsole) {
                listAll(sender, 1);
            }
            else {
                NetworkPlayer np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());
                listOwned(sender, np, 1);
            }
            return true;
        }

        String type = null;
        NetworkPlayer np = null;
        int page = 1;

        if (args.length >= 1) {
            if (!List.of("all", "equipped", "owned").contains(args[0].toLowerCase())) {
                type = "all";
            }
            else {
                type = args[0];
            }
        }

        if (args.length >= 2) {
            if (type.equals("all")) {
                try {
                    page = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException e) {
                    MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[1]));
                    return true;
                }
            }

            else {
                np = NetworkPlayer.getPlayerByUsername(args[1]);

                if (np == null) {
                    MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", args[1]));
                    return true;
                }
            }
        }

        if (args.length >= 3) {
            try {
                page = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e) {
                MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_NUMBER, Map.of("input", args[2]));
                return true;
            }
        }

        if (np == null) {
            if (isConsole) {
                MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "player"));
                return true;
            }
            else {
                np = NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId());
            }
        }

        switch (type) {
            case "all":
                listAll(sender, page);
                break;

            case "equipped":
                listEquipped(sender, np, page);
                break;

            case "owned":
                listOwned(sender, np, page);
                break;
        }

        return true;
    }

    private void listAll(CommandSender sender, int page) {
        List<Cosmetic> cosmetics = new ArrayList<>(CXYZ.cosmetics)
                .stream().sorted(Comparator.comparing(Cosmetic::getId)).collect(Collectors.toList());

        Map<String, Object> commonMap = new HashMap<>();

        if (cosmetics.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COSMETIC_LIST_ALL_BLANK, commonMap);
            return;
        }


        String format = MessageGrabber.grab(MessageKey.COSMETIC_LIST_ALL_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.COSMETIC_LIST_ALL_SEPARATOR);


        int maxEntriesPerPage = msgYML.getInt(MessageKey.COSMETIC_LIST_ALL_MAX_ENTRIES.getPath(), -1);

        MapFormatters.ListFormatter formatter = MapFormatters.cosmeticListFormatter(cosmetics, format, delimiter, maxEntriesPerPage, page);
        commonMap.putAll(formatter.getFormatMap());

        String unparsed = MessageGrabber.grab(MessageKey.COSMETIC_LIST_ALL);

        int min = 1;
        int max = formatter.getMaxPages();

        if (page < 1 || page > max) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PAGE, Map.of("min", min, "max", max, "page", page));
            return;
        }

        unparsed = unparsed.replace("{cosmetics}", !formatter.getEntries().isEmpty() ? formatter.generatePage(page) : "None");
        unparsed = unparsed.replace("{size}", String.valueOf(cosmetics.size()));

        commonMap.put("page", page);
        commonMap.put("previous-page", page - 1);
        commonMap.put("next-page", page + 1);
        commonMap.put("max-pages", max);

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
    }

    private void listEquipped(CommandSender sender, NetworkPlayer np, int page) {
        List<Cosmetic> cosmetics = new ArrayList<>(np.getEquippedCosmetics())
                .stream().sorted(Comparator.comparing(Cosmetic::getId)).collect(Collectors.toList());

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (cosmetics.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COSMETIC_LIST_EQUIPPED_BLANK, commonMap);
            return;
        }


        String format = MessageGrabber.grab(MessageKey.COSMETIC_LIST_EQUIPPED_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.COSMETIC_LIST_EQUIPPED_SEPARATOR);


        int maxEntriesPerPage = msgYML.getInt(MessageKey.COSMETIC_LIST_EQUIPPED_MAX_ENTRIES.getPath(), -1);

        MapFormatters.ListFormatter formatter = MapFormatters.cosmeticListFormatter(cosmetics, format, delimiter, maxEntriesPerPage, page);
        commonMap.putAll(formatter.getFormatMap());

        String unparsed = MessageGrabber.grab(MessageKey.COSMETIC_LIST_EQUIPPED);

        int min = 1;
        int max = formatter.getMaxPages();

        if (page < 1 || page > max) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PAGE, Map.of("min", min, "max", max, "page", page));
            return;
        }

        unparsed = unparsed.replace("{cosmetics}", !formatter.getEntries().isEmpty() ? formatter.generatePage(page) : "None");
        unparsed = unparsed.replace("{size}", String.valueOf(cosmetics.size()));

        commonMap.put("page", page);
        commonMap.put("previous-page", page - 1);
        commonMap.put("next-page", page + 1);
        commonMap.put("max-pages", max);

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
    }

    private void listOwned(CommandSender sender, NetworkPlayer np, int page) {
        List<Cosmetic> cosmetics = new ArrayList<>(np.getOwnedCosmetics())
                .stream().sorted(Comparator.comparing(Cosmetic::getId)).collect(Collectors.toList());

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);

        if (cosmetics.isEmpty()) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COSMETIC_LIST_OWNED_BLANK, commonMap);
            return;
        }


        String format = MessageGrabber.grab(MessageKey.COSMETIC_LIST_OWNED_FORMAT);
        String delimiter = MessageGrabber.grab(MessageKey.COSMETIC_LIST_OWNED_SEPARATOR);


        int maxEntriesPerPage = msgYML.getInt(MessageKey.COSMETIC_LIST_OWNED_MAX_ENTRIES.getPath(), -1);

        MapFormatters.ListFormatter formatter = MapFormatters.cosmeticListFormatter(cosmetics, format, delimiter, maxEntriesPerPage, page);
        commonMap.putAll(formatter.getFormatMap());

        String unparsed = MessageGrabber.grab(MessageKey.COSMETIC_LIST_OWNED);

        int min = 1;
        int max = formatter.getMaxPages();

        if (page < 1 || page > max) {
            MessageUtils.sendParsedMessage(sender, MessageKey.INVALID_PAGE, Map.of("min", min, "max", max, "page", page));
            return;
        }

        unparsed = unparsed.replace("{cosmetics}", !formatter.getEntries().isEmpty() ? formatter.generatePage(page) : "None");
        unparsed = unparsed.replace("{size}", String.valueOf(cosmetics.size()));

        commonMap.put("page", page);
        commonMap.put("previous-page", page - 1);
        commonMap.put("next-page", page + 1);
        commonMap.put("max-pages", max);

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
    }
}
