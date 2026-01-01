package com.carrotguy69.cxyz.cmd.mod.punishment;

import com.carrotguy69.cxyz.classes.models.db.Punishment;
import com.carrotguy69.cxyz.classes.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.template.CommandRestrictor;
import com.carrotguy69.cxyz.template.MapFormatters;
import com.carrotguy69.cxyz.other.TimeUtils;
import com.carrotguy69.cxyz.other.messages.MessageGrabber;
import com.carrotguy69.cxyz.other.messages.MessageKey;
import com.carrotguy69.cxyz.other.messages.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.CXYZ.timezone;

public class History implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
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

        else if (args.length == 0) {
            viewHistory(sender, sender.getName());
        }

        else {
            viewHistory(sender, args[0]);
        }

        return true;
    }

    private void viewHistory(CommandSender sender, String targetPlayer) {
        NetworkPlayer np = NetworkPlayer.getPlayerByUsername(targetPlayer);

        if (np == null) {
            MessageUtils.sendParsedMessage(sender, MessageKey.PLAYER_NOT_FOUND, Map.of("username", targetPlayer));
            return;
        }

        List<Punishment> punishments = Punishment.getPlayerPunishments(np);

        punishments.sort(Comparator.comparing(Punishment::getIssuedTimestamp));

        String delimiter = MessageGrabber.grab(MessageKey.PUNISHMENT_HISTORY_SEPARATOR);

        List<String> formattedPunishments = new ArrayList<>();
        Map<String, Object> commonMap = new HashMap<>();

        buildStrings(sender, punishments, commonMap, formattedPunishments);

        String punishmentListString = String.join(delimiter, formattedPunishments); // This returns the interactive formatting of the joined punishment list.

        String unparsed = MessageGrabber.grab(MessageKey.PUNISHMENT_HISTORY_LIST);

        unparsed = unparsed.replace("{punishments}", !punishments.isEmpty() ? punishmentListString : "None");

        commonMap.putAll(MapFormatters.playerFormatter(np));

        MessageUtils.sendParsedMessage(sender, unparsed, commonMap);
    }


    private void buildStrings(CommandSender sender, List<Punishment> punishments, Map<String, Object> commonMap, List<String> formattedPunishments) {

        for (int i = 0; i < punishments.size(); i++) {
            Punishment punishment = punishments.get(i);

            String punishmentString = MessageGrabber.grab(MessageKey.PUNISHMENT_HISTORY_FORMAT);

            punishmentString = punishmentString.replace("{mod}", "{mod-" + i + "}");
            punishmentString = punishmentString.replace("{mod-nickname}", "{mod-nickname-" + i + "}");
            punishmentString = punishmentString.replace("{mod-display-name}", "{mod-display-name-" + i + "}");
            punishmentString = punishmentString.replace("{mod-uuid}", "{mod-uuid-" + i + "}");
            punishmentString = punishmentString.replace("{mod-uuid}", "{mod-uuid-" + i + "}");
            punishmentString = punishmentString.replace("{mod-rank-prefix-display}", "{mod-rank-prefix-display-" + i + "}");
            punishmentString = punishmentString.replace("{mod-color}", "{mod-color-" + i + "}");
            punishmentString = punishmentString.replace("{mod-chat-color}", "{mod-chat-color-" + i + "}");
            punishmentString = punishmentString.replace("{mod-prefix}", "{mod-prefix-" + i + "}");
            punishmentString = punishmentString.replace("{mod-tag}", "{mod-tag-" + i + "}");

            punishmentString = punishmentString.replace("{moderator}", "{moderator-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-nickname}", "{moderator-nickname-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-display-name}", "{moderator-display-name-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-uuid}", "{moderator-uuid-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-uuid}", "{moderator-uuid-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-rank-prefix-display}", "{moderator-rank-prefix-display-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-color}", "{moderator-color-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-chat-color}", "{moderator-chat-color-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-prefix}", "{moderator-prefix-" + i + "}");
            punishmentString = punishmentString.replace("{moderator-tag}", "{moderator-tag-" + i + "}");

            punishmentString = punishmentString.replace("{editor-mod}", "{editor-mod-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-nickname}", "{editor-mod-nickname-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-display-name}", "{editor-mod-display-name-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-uuid}", "{editor-mod-uuid-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-rank-prefix-display}", "{editor-mod-rank-prefix-display-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-color}", "{editor-mod-color-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-chat-color}", "{editor-mod-chat-color-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-prefix}", "{editor-mod-prefix-" + i + "}");
            punishmentString = punishmentString.replace("{editor-mod-tag}", "{editor-mod-tag-" + i + "}");

            punishmentString = punishmentString.replace("{editor-moderator}", "{editor-moderator-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-nickname}", "{editor-moderator-nickname-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-display-name}", "{editor-moderator-display-name-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-uuid}", "{editor-moderator-uuid-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-rank-prefix-display}", "{editor-moderator-rank-prefix-display-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-color}", "{editor-moderator-color-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-chat-color}", "{editor-moderator-chat-color-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-prefix}", "{editor-moderator-prefix-" + i + "}");
            punishmentString = punishmentString.replace("{editor-moderator-tag}", "{editor-moderator-tag-" + i + "}");

            punishmentString = punishmentString.replace("{case-id}", "{case-id-" + i + "}");
            punishmentString = punishmentString.replace("{type}", "{type-" + i + "}");

            punishmentString = punishmentString.replace("{date}", "{date-" + i + "}");
            punishmentString = punishmentString.replace("{date-short}", "{date-short-" + i + "}");
            punishmentString = punishmentString.replace("{effective-until}", "{effective-until-" + i + "}");
            punishmentString = punishmentString.replace("{effective-until-short}", "{effective-until-short-" + i + "}");
            punishmentString = punishmentString.replace("{effective-until-countdown}", "{effective-until-countdown-" + i + "}");
            punishmentString = punishmentString.replace("{effective-until-countdown-short}", "{effective-until-countdown-short-" + i + "}");
            punishmentString = punishmentString.replace("{expire-time}", "{expire-time-" + i + "}");
            punishmentString = punishmentString.replace("{expire-time-short}", "{expire-time-short-" + i + "}");
            punishmentString = punishmentString.replace("{expire-time-countdown}", "{expire-time-countdown-" + i + "}");
            punishmentString = punishmentString.replace("{expire-time-countdown-short}", "{expire-time-countdown-short-" + i + "}");
            punishmentString = punishmentString.replace("{reason}", "{reason-" + i + "}");


            NetworkPlayer punishedPlayer = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getUUID()));

            Map<String, Object> punishedPlayerMap = MapFormatters.playerFormatter(punishedPlayer);
            commonMap.putAll(punishedPlayerMap);

            if (!punishment.getModUsername().equalsIgnoreCase("console")) {
                NetworkPlayer mod = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getModUUID()));

                commonMap.put("mod-" + i, mod.getUsername());
                commonMap.put("mod-nickname-" + i,mod.getNickname() != null ? mod.getNickname() : "");
                commonMap.put("mod-display-name-" + i,mod.getDisplayName());
                commonMap.put("mod-uuid-" + i,mod.getUUID());
                commonMap.put("mod-rank-" + i,mod.getRank().getName());
                commonMap.put("mod-rank-prefix-display-" + i,ChatColor.stripColor(f(mod.getRank().getPrefix())).isBlank() ? mod.getRank().getColor() + mod.getRank().getName().toUpperCase() : mod.getRank().getPrefix());
                commonMap.put("mod-color-" + i,mod.getRank().getColor() != null ? mod.getRank().getColor() : "&7");
                commonMap.put("mod-chat-color-" + i,mod.getChatColor() != null && !mod.getChatColor().isBlank() ? mod.getChatColor() : mod.getRank().getDefaultChatColor());
                commonMap.put("mod-prefix-" + i,mod.getCustomRankPlate() != null && !mod.getCustomRankPlate().isBlank() ? mod.getCustomRankPlate() : mod.getRank().getPrefix());
                commonMap.put("mod-tag-" + i,mod.getChatTag() != null && !mod.getChatTag().isBlank() ? mod.getChatTag() : "");

                commonMap.put("moderator-" + i,mod.getUsername());
                commonMap.put("moderator-nickname-" + i,mod.getNickname() != null ? mod.getNickname() : "");
                commonMap.put("moderator-display-name-" + i,mod.getDisplayName());
                commonMap.put("moderator-uuid-" + i,mod.getUUID());
                commonMap.put("moderator-rank-" + i,mod.getRank().getName());
                commonMap.put("moderator-rank-prefix-display-" + i,ChatColor.stripColor(f(mod.getRank().getPrefix())).isBlank() ? mod.getRank().getColor() + mod.getRank().getName().toUpperCase() : mod.getRank().getPrefix());
                commonMap.put("moderator-color-" + i,mod.getRank().getColor() != null ? mod.getRank().getColor() : "&7");
                commonMap.put("moderator-chat-color-" + i,mod.getChatColor() != null && !mod.getChatColor().isBlank() ? mod.getChatColor() : mod.getRank().getDefaultChatColor());
                commonMap.put("moderator-prefix-" + i,mod.getCustomRankPlate() != null && !mod.getCustomRankPlate().isBlank() ? mod.getCustomRankPlate() : mod.getRank().getPrefix());
                commonMap.put("moderator-tag-" + i,mod.getChatTag() != null && !mod.getChatTag().isBlank() ? mod.getChatTag() : "");

            }
            else {

                commonMap.put("mod-" + i, "Console");
                commonMap.put("mod-nickname-" + i, "");
                commonMap.put("mod-display-name-" + i, "Console");
                commonMap.put("mod-uuid-" + i, "");
                commonMap.put("mod-rank-" + i, "");
                commonMap.put("mod-rank-prefix-display--" + i, "");
                commonMap.put("mod-color-" + i, "");
                commonMap.put("mod-chat-color-" + i, "");
                commonMap.put("mod-prefix-" + i, "");
                commonMap.put("mod-tag-" + i, "");

                commonMap.put("moderator-" + i, "Console");
                commonMap.put("moderator-nickname-" + i, "");
                commonMap.put("moderator-display-name-" + i, "Console");
                commonMap.put("moderator-uuid-" + i, "");
                commonMap.put("moderator-rank-" + i, "");
                commonMap.put("moderator-rank-prefix-display-" + i, "");
                commonMap.put("moderator-color-" + i, "");
                commonMap.put("moderator-chat-color-" + i, "");
                commonMap.put("moderator-prefix-" + i, "");
                commonMap.put("moderator-tag-" + i, "");
            }


            if (!punishment.isEnforced() && punishment.getEditorModUsername() != null) {

                if (punishment.getEditorModUsername() == null || punishment.getEditorModUUID() == null) { // blank
                    commonMap.put("editor-mod-" + i, "");
                    commonMap.put("editor-mod-nickname-" + i, "");
                    commonMap.put("editor-mod-display-name-" + i, "");
                    commonMap.put("editor-mod-uuid-" + i, "");
                    commonMap.put("editor-mod-rank-" + i, "");
                    commonMap.put("editor-mod-rank-prefix-display-" + i, "");
                    commonMap.put("editor-mod-color-" + i, "");
                    commonMap.put("editor-mod-chat-color-" + i, "");
                    commonMap.put("editor-mod-prefix-" + i, "");
                    commonMap.put("editor-mod-tag-" + i, "");

                    commonMap.put("editor-moderator-" + i, "");
                    commonMap.put("editor-moderator-nickname-" + i, "");
                    commonMap.put("editor-moderator-display-name-" + i, "");
                    commonMap.put("editor-moderator-uuid-" + i, "");
                    commonMap.put("editor-moderator-rank-" + i, "");
                    commonMap.put("editor-moderator-rank-prefix-display-" + i, "");
                    commonMap.put("editor-moderator-color-" + i, "");
                    commonMap.put("editor-moderator-chat-color-" + i, "");
                    commonMap.put("editor-moderator-prefix-" + i, "");
                    commonMap.put("editor-moderator-tag-" + i, "");
                }

                else if (!Objects.equals(punishment.getEditorModUUID(), null) && !Objects.equals(punishment.getEditorModUsername(), null) && !Objects.equals(punishment.getEditorModUUID(), "Console") && !Objects.equals(punishment.getEditorModUsername(), "Console")) {
                    commonMap.put("editor-mod-" + i, "Console");
                    commonMap.put("editor-mod-nickname-" + i, "");
                    commonMap.put("editor-mod-display-name-" + i, "Console");
                    commonMap.put("editor-mod-uuid-" + i, "");
                    commonMap.put("editor-mod-rank-" + i, "");
                    commonMap.put("editor-mod-rank-prefix-display-" + i, "");
                    commonMap.put("editor-mod-color-" + i, "");
                    commonMap.put("editor-mod-chat-color-" + i, "");
                    commonMap.put("editor-mod-prefix-" + i, "");
                    commonMap.put("editor-mod-tag-" + i, "");

                    commonMap.put("editor-moderator-" + i, "Console");
                    commonMap.put("editor-moderator-nickname-" + i, "");
                    commonMap.put("editor-moderator-display-name-" + i, "Console");
                    commonMap.put("editor-moderator-uuid-" + i, "");
                    commonMap.put("editor-moderator-rank-" + i, "");
                    commonMap.put("editor-moderator-rank-prefix-display-" + i, "");
                    commonMap.put("editor-moderator-color-" + i, "");
                    commonMap.put("editor-moderator-chat-color-" + i, "");
                    commonMap.put("editor-moderator-prefix-" + i, "");
                    commonMap.put("editor-moderator-tag-" + i, "");
                }

                else {
                    NetworkPlayer editorMod = NetworkPlayer.getPlayerByUUID(UUID.fromString(punishment.getEditorModUUID()));

                    commonMap.put("editor-mod-" + i, editorMod.getUsername());
                    commonMap.put("editor-mod-nickname-" + i, editorMod.getNickname() != null ? editorMod.getNickname() : "");
                    commonMap.put("editor-mod-display-name-" + i, editorMod.getDisplayName());
                    commonMap.put("editor-mod-uuid-" + i, editorMod.getUUID());
                    commonMap.put("editor-mod-rank-" + i, editorMod.getRank().getName());
                    commonMap.put("editor-mod-rank-prefix-display-" + i, ChatColor.stripColor(f(editorMod.getRank().getPrefix())).isBlank() ? editorMod.getRank().getColor() + editorMod.getRank().getName().toUpperCase() : editorMod.getRank().getPrefix());
                    commonMap.put("editor-mod-color-" + i, editorMod.getRank().getColor() != null ? editorMod.getRank().getColor() : "&7");
                    commonMap.put("editor-mod-chat-color-" + i, editorMod.getChatColor() != null && !editorMod.getChatColor().isBlank() ? editorMod.getChatColor() : editorMod.getRank().getDefaultChatColor());
                    commonMap.put("editor-mod-prefix-" + i, editorMod.getCustomRankPlate() != null && !editorMod.getCustomRankPlate().isBlank() ? editorMod.getCustomRankPlate() : editorMod.getRank().getPrefix());
                    commonMap.put("editor-mod-tag-" + i, editorMod.getChatTag() != null && !editorMod.getChatTag().isBlank() ? editorMod.getChatTag() : "");

                    commonMap.put("editor-moderator-" + i,editorMod.getUsername());
                    commonMap.put("editor-moderator-nickname-" + i,editorMod.getNickname() != null ? editorMod.getNickname() : "");
                    commonMap.put("editor-moderator-display-name-" + i,editorMod.getDisplayName());
                    commonMap.put("editor-moderator-uuid-" + i,editorMod.getUUID());
                    commonMap.put("editor-moderator-rank-" + i,editorMod.getRank().getName());
                    commonMap.put("editor-moderator-rank-prefix-display-" + i,ChatColor.stripColor(f(editorMod.getRank().getPrefix())).isBlank() ? editorMod.getRank().getColor() + editorMod.getRank().getName().toUpperCase() : editorMod.getRank().getPrefix());
                    commonMap.put("editor-moderator-color-" + i,editorMod.getRank().getColor() != null ? editorMod.getRank().getColor() : "&7");
                    commonMap.put("editor-moderator-chat-color-" + i,editorMod.getChatColor() != null && !editorMod.getChatColor().isBlank() ? editorMod.getChatColor() : editorMod.getRank().getDefaultChatColor());
                    commonMap.put("editor-moderator-prefix-" + i,editorMod.getCustomRankPlate() != null && !editorMod.getCustomRankPlate().isBlank() ? editorMod.getCustomRankPlate() : editorMod.getRank().getPrefix());
                    commonMap.put("editor-moderator-tag-" + i,editorMod.getChatTag() != null && !editorMod.getChatTag().isBlank() ? editorMod.getChatTag() : "");

                }
            }

            String tz = sender instanceof Player ? NetworkPlayer.getPlayerByUUID(((Player) sender).getUniqueId()).getTimezone() : timezone;


            commonMap.put("case-id-" + i, punishment.getID());
            commonMap.put("type-" + i,punishment.getType());

            commonMap.put("date-" + i, TimeUtils.dateOf(punishment.getIssuedTimestamp(), tz));
            commonMap.put("date-short-" + i, TimeUtils.dateOfShort(punishment.getIssuedTimestamp(), tz));
            commonMap.put("effective-until-" + i, TimeUtils.dateOf(punishment.getEffectiveUntilTimestamp(), tz));
            commonMap.put("effective-until-short-" + i, TimeUtils.dateOfShort(punishment.getEffectiveUntilTimestamp(), tz));
            commonMap.put("effective-until-countdown-" + i, TimeUtils.unixCountdown(punishment.getEffectiveUntilTimestamp()));
            commonMap.put("effective-until-countdown-short-" + i, TimeUtils.unixCountdownShort(punishment.getEffectiveUntilTimestamp()));

            commonMap.put("expire-time-" + i, TimeUtils.dateOf(punishment.getExpireTimestamp(), tz));
            commonMap.put("expire-time-short-" + i, TimeUtils.dateOfShort(punishment.getExpireTimestamp(), tz));
            commonMap.put("expire-time-countdown-" + i, TimeUtils.unixCountdown(punishment.getExpireTimestamp()));
            commonMap.put("expire-time-countdown-short-" + i, TimeUtils.unixCountdownShort(punishment.getExpireTimestamp()));



            commonMap.put("reason-" + i, punishment.getReason());

            formattedPunishments.add(punishmentString);
        }
    }
}
