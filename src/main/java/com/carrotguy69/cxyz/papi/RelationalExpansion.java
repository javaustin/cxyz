package com.carrotguy69.cxyz.papi;

import com.carrotguy69.cxyz.CXYZ;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.entity.Player;


public class RelationalExpansion extends Expansion implements Relational {
    public RelationalExpansion(CXYZ plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderRequest(Player player1, Player player2, String s) {
        if (player1 == null || player2 == null) {
            return null;
        }

        // Implement:
        //      - p1 visible to p2 -> (return online/offline)
        //      - see np for more

        return null;


    }
}
