package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.CommandRestriction;
import com.carrotguy69.cxyz.utils.CommandUtils;
import com.carrotguy69.cxyz.utils.ObjectUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Fullbright implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        /*
        SYNTAX:
            /fullbright [on | off | enable | disable | ...]
        */

        args = CommandUtils.handleSilent(args);

        String node = "fullbright";
        if (!sender.hasPermission("cxyz." + node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestriction.handleRestricted(node, sender))
            return true;

        if (!(sender instanceof Player p)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_PLAYER_ONLY, Map.of());
            return true;
        }

        if (args.length >= 1) {
            boolean value = ObjectUtils.parseCasualBoolean(args[0]);

            if (value) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false));
                MessageUtils.sendParsedMessage(p, MessageKey.FULLBRIGHT_TOGGLE_ON, Map.of());
            }
            else {
                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                MessageUtils.sendParsedMessage(p, MessageKey.FULLBRIGHT_TOGGLE_OFF, Map.of());
            }
            return true;
        }

        if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);

            MessageUtils.sendParsedMessage(p, MessageKey.FULLBRIGHT_TOGGLE_OFF, Map.of());
            return true;
        }


        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1, false, false));
        MessageUtils.sendParsedMessage(p, MessageKey.FULLBRIGHT_TOGGLE_ON, Map.of());

        return true;
    }

}
