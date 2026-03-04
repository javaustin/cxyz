package com.carrotguy69.cxyz.papi;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.messages.utils.MapFormatters;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class Expansion extends PlaceholderExpansion {
    private final CXYZ plugin;

    public Expansion(CXYZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(" ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return fulfill(player.getUniqueId(), params);
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return fulfill(player.getUniqueId(), params);
    }

    public String fulfill(UUID uuid, @NotNull String params) {
        NetworkPlayer np;
        try {
            np = NetworkPlayer.getPlayerByUUID(uuid);
        }
        catch (RuntimeException e) {
            Logger.warning(String.format("Attempted to fulfill PAPI placeholder='%s' with playerUUID='%s' whom does not exist", params, uuid.toString()));
            return null;
        }

        Map<String, Object> map = MapFormatters.playerFormatter(np);

        Object res = map.get(params.replace("_", "-"));
        // MapFormatters return a map with the keys spaced by dashes e.g. "player-tag"

        if (res != null) {
            return res.toString();
        }

        return null;
    }

}
