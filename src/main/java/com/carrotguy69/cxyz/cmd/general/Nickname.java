package com.carrotguy69.cxyz.cmd.general;

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

public class Nickname implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // If the player does not have an adequate rank or level, isRestricted will auto-deny them. No further logic needed.
        if (CommandRestrictor.handleRestricted(command, sender)) // This also handles Player and CommandSender, if it is a non player, the command is not restricted.
            return true;

        String node = "cxyz.general.nickname";
        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
                return true;
            }

            Player player = (Player) sender;

            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(player.getUniqueId());

            MessageUtils.sendParsedMessage(player, MessageKey.NICKNAME_VIEW, MapFormatters.playerFormatter(np));
            return true;
        }


        if (args.length > 0) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
                return true;
            }

            Player player = (Player) sender;

            NetworkPlayer np = NetworkPlayer.getPlayerByUUID(player.getUniqueId());

//            nickname-length: "&cNickname must be no longer than 16 characters." # format {input}
//            nickname-characters: "(&cA nickname must be made up of letters, numbers, and underscores only.)[HOVER:Invalid: {invalid-characters}]" # format {invalid-characters}
//            nickname-taken: "&cThis nickname is taken!" # format {input}

            String nickname = args[0];

            if (nickname.equalsIgnoreCase("reset")) {
                np.setNickname("");
                np.getPlayer().setDisplayName(np.getUsername());
                np.getPlayer().setPlayerListName(np.getDisplayName());

                np.sync();

                MessageUtils.sendParsedMessage(player, MessageKey.NICKNAME_RESET, MapFormatters.playerFormatter(np));
                return true;
            }

            if (nickname.length() < 3|| nickname.length() > 16) {
                MessageUtils.sendParsedMessage(player, MessageKey.INVALID_NICKNAME_LENGTH, Map.of("input", args[0]));
                return true;
            }

            if (!nickname.matches("^[A-Za-z0-9_]+$")) {
                MessageUtils.sendParsedMessage(player, MessageKey.INVALID_NICKNAME_CHARACTERS, Map.of("input", args[0]));
                return true;
            }

            NetworkPlayer possiblePlayer = NetworkPlayer.getPlayerByUsername(args[0]);

            if (possiblePlayer != null) {
                MessageUtils.sendParsedMessage(player, MessageKey.INVALID_NICKNAME_TAKEN, Map.of("input", args[0]));
                return true;
            }

            np.setNickname(args[0]);
            np.getPlayer().setDisplayName(np.getRank().getColor() + args[0]);
            np.getPlayer().setPlayerListName(np.getDisplayName());
            np.sync();

            MessageUtils.sendParsedMessage(player, MessageKey.NICKNAME_SET, MapFormatters.playerFormatter(np));
        }

        return true;
    }
}
