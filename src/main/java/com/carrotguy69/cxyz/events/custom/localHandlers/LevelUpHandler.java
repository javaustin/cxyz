package com.carrotguy69.cxyz.events.custom.localHandlers;

import com.carrotguy69.cxyz.events.custom.LevelUpEvent;
import com.carrotguy69.cxyz.events.custom.base.EventHandler;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.utils.BroadcastUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class LevelUpHandler implements EventHandler<LevelUpEvent> {
    @Override
    public boolean handle(LevelUpEvent event) {
        NetworkPlayer np = event.getPlayer();
        int prevLevel = event.getPreviousLevel();
        int newLevel = event.getNewLevel();

        Player p = np.getPlayer();

        if (p == null)
            return false;

        Map<String, Object> commonMap = MapFormatters.playerFormatter(np);
        commonMap.put("level-up-previous-level", prevLevel);
        commonMap.put("level-up-new-level", newLevel);

        BroadcastUtils.playSound(List.of(p), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        MessageUtils.sendParsedMessage(p, MessageKey.LEVELUP, commonMap);

        return false;
    }
}
