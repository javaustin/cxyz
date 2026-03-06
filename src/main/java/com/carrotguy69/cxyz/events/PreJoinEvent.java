package com.carrotguy69.cxyz.events;


import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.models.db.Punishment;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Map;

import static com.carrotguy69.cxyz.CXYZ.f;
import static com.carrotguy69.cxyz.CXYZ.isInitialized;
import static com.carrotguy69.cxyz.messages.MessageParser.unescape;

public class PreJoinEvent {

    public static void onPreJoin(AsyncPlayerPreLoginEvent e) {
        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);

        if (!isInitialized()) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            e.setKickMessage("Server is not finished initializing. Please wait!");
            return;
        }

        NetworkPlayer np;
        try {
            np = NetworkPlayer.getPlayerByUUID(e.getUniqueId());
        }
        catch (RuntimeException ex) {
            return;
        }

        if (np.isBanned()) {
            Punishment ban = Punishment.getActivePunishment(np, Punishment.PunishmentType.BAN);
            Map<String, Object> map = MapFormatters.punishmentFormatter(np.getPlayer(), ban);

            if (ban.isPermanent())
                e.setKickMessage(f(String.join("\n", unescape(MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_PLAYER_MESSAGE_PERMANENT, map)))));
            else
                e.setKickMessage(f(String.join("\n", unescape(MessageGrabber.grab(MessageKey.PUNISHMENT_BAN_PLAYER_MESSAGE, map)))));

            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);

            return;
        }


    }


}
