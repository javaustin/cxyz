package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.cmd.Broadcast;
import com.carrotguy69.cxyz.cmd.ChatColor;
import com.carrotguy69.cxyz.cmd.Craft;
import com.carrotguy69.cxyz.cmd.Debug;
import com.carrotguy69.cxyz.cmd.Dispose;
import com.carrotguy69.cxyz.cmd.Enchant;
import com.carrotguy69.cxyz.cmd.EnchantTable;
import com.carrotguy69.cxyz.cmd.Enderchest;
import com.carrotguy69.cxyz.cmd.Fly;
import com.carrotguy69.cxyz.cmd.Fullbright;
import com.carrotguy69.cxyz.cmd.Heal;
import com.carrotguy69.cxyz.cmd.Info;
import com.carrotguy69.cxyz.cmd.InventorySee;
import com.carrotguy69.cxyz.cmd.Item;
import com.carrotguy69.cxyz.cmd.Location;
import com.carrotguy69.cxyz.cmd.Mend;
import com.carrotguy69.cxyz.cmd.Nickname;
import com.carrotguy69.cxyz.cmd.Parse;
import com.carrotguy69.cxyz.cmd.Ping;
import com.carrotguy69.cxyz.cmd.Port;
import com.carrotguy69.cxyz.cmd.PowerTool;
import com.carrotguy69.cxyz.cmd.Print;
import com.carrotguy69.cxyz.cmd.SQL;
import com.carrotguy69.cxyz.cmd.Smite;
import com.carrotguy69.cxyz.cmd.Sudo;
import com.carrotguy69.cxyz.cmd.Test;
import com.carrotguy69.cxyz.cmd.Timezone;
import com.carrotguy69.cxyz.cmd.UUID;
import com.carrotguy69.cxyz.cmd.Vanish;
import com.carrotguy69.cxyz.cmd.channel._ChannelExecutor;
import com.carrotguy69.cxyz.cmd.coins._CoinsExecutor;
import com.carrotguy69.cxyz.cmd.cosmetic._CosmeticExecutor;
import com.carrotguy69.cxyz.cmd.friend._FriendExecutor;
import com.carrotguy69.cxyz.cmd.ignore.Unignore;
import com.carrotguy69.cxyz.cmd.ignore._IgnoreExecutor;
import com.carrotguy69.cxyz.cmd.level._LevelExecutor;
import com.carrotguy69.cxyz.cmd.message.MessageReply;
import com.carrotguy69.cxyz.cmd.message.MessageSend;
import com.carrotguy69.cxyz.cmd.party._PartyExecutor;
import com.carrotguy69.cxyz.cmd.privacy.FriendPrivacy;
import com.carrotguy69.cxyz.cmd.privacy.MessagePrivacy;
import com.carrotguy69.cxyz.cmd.privacy.PartyPrivacy;
import com.carrotguy69.cxyz.cmd.punishment.Ban;
import com.carrotguy69.cxyz.cmd.punishment.Kick;
import com.carrotguy69.cxyz.cmd.punishment.Mute;
import com.carrotguy69.cxyz.cmd.punishment.Unban;
import com.carrotguy69.cxyz.cmd.punishment.Unmute;
import com.carrotguy69.cxyz.cmd.punishment.Warn;
import com.carrotguy69.cxyz.cmd.punishment.manager._PunishmentExecutor;
import com.carrotguy69.cxyz.cmd.rank._RankExecutor;
import com.carrotguy69.cxyz.cmd.xp._XPExecutor;
import com.carrotguy69.cxyz.events.custom.LevelUpEvent;
import com.carrotguy69.cxyz.events.custom.PublicChatEvent;
import com.carrotguy69.cxyz.events.custom.base.Priority;
import com.carrotguy69.cxyz.events.custom.localHandlers.ChatFallbackHandler;
import com.carrotguy69.cxyz.events.custom.localHandlers.LevelUpHandler;
import com.carrotguy69.cxyz.events.custom.service.EventService;
import com.carrotguy69.cxyz.http.Listener;
import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.papi.Expansion;
import com.carrotguy69.cxyz.papi.RelationalExpansion;
import com.carrotguy69.cxyz.tabCompleters.AnyPlayer;
import com.carrotguy69.cxyz.tabCompleters.Blank;
import com.carrotguy69.cxyz.tabCompleters.CoinsXPLevel;
import com.carrotguy69.cxyz.tabCompleters.LocalOnlineAllPlayer;
import com.carrotguy69.cxyz.tabCompleters.LocalOnlinePlayer;
import com.carrotguy69.cxyz.tabCompleters.OnlinePlayer;
import com.carrotguy69.cxyz.tabCompleters.Show;
import com.carrotguy69.cxyz.tabCompleters.Party;
import com.carrotguy69.cxyz.tabCompleters.Privacy;
import com.carrotguy69.cxyz.tabCompleters.Rank;
import com.carrotguy69.cxyz.tabCompleters.LocalOnlinePlayerAndToggle;
import com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter;
import com.carrotguy69.cxyz.tabCompleters.Toggle;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.carrotguy69.cxyz.CXYZ.apiEndpoint;
import static com.carrotguy69.cxyz.CXYZ.gson;
import static com.carrotguy69.cxyz.CXYZ.initializedMap;
import static com.carrotguy69.cxyz.CXYZ.listener;
import static com.carrotguy69.cxyz.CXYZ.plugin;
import static com.carrotguy69.cxyz.CXYZ.thisPort;
import static com.carrotguy69.cxyz.other.Tasks.clearPartyInvites;
import static com.carrotguy69.cxyz.other.Tasks.deleteOfflineParties;
import static com.carrotguy69.cxyz.other.Tasks.fixOnlinePlayers;
import static com.carrotguy69.cxyz.other.Tasks.handlePartyExpires;
import static com.carrotguy69.cxyz.other.Tasks.runPunishmentSeq;
import static com.carrotguy69.cxyz.other.Tasks.updateLastOnlineValues;

public class Startup {

    public static void doThings() {
        registerCommands();
        registerEvents();
        startTasks();
        registerPlaceholders();
    }

    public static void startEndpoints() throws IOException {
        listener = new Listener(thisPort);
    }

    public static void registerCommands() {
            Objects.requireNonNull(plugin.getCommand("coins")).setExecutor(new SilentCommandExecutor(new _CoinsExecutor()));
            Objects.requireNonNull(plugin.getCommand("coins")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new CoinsXPLevel()));

            Objects.requireNonNull(plugin.getCommand("rank")).setExecutor(new SilentCommandExecutor(new _RankExecutor()));
            Objects.requireNonNull(plugin.getCommand("rank")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new Rank()));

            Objects.requireNonNull(plugin.getCommand("broadcast")).setExecutor(new Broadcast());
            Objects.requireNonNull(plugin.getCommand("broadcast")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("port")).setExecutor(new Port());
            Objects.requireNonNull(plugin.getCommand("port")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("debug")).setExecutor(new Debug());
            Objects.requireNonNull(plugin.getCommand("debug")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Debug());

            Objects.requireNonNull(plugin.getCommand("ping")).setExecutor(new Ping());
            Objects.requireNonNull(plugin.getCommand("ping")).setTabCompleter(new OnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("show")).setExecutor(new com.carrotguy69.cxyz.cmd.Show());
            Objects.requireNonNull(plugin.getCommand("show")).setTabCompleter(new Show());

            Objects.requireNonNull(plugin.getCommand("test")).setExecutor(new Test());
            Objects.requireNonNull(plugin.getCommand("test")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("level")).setExecutor(new SilentCommandExecutor(new _LevelExecutor()));
            Objects.requireNonNull(plugin.getCommand("level")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new CoinsXPLevel()));

            Objects.requireNonNull(plugin.getCommand("parse")).setExecutor(new Parse());
            Objects.requireNonNull(plugin.getCommand("parse")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("print")).setExecutor(new Print());
            Objects.requireNonNull(plugin.getCommand("print")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Print());

            Objects.requireNonNull(plugin.getCommand("sudo")).setExecutor(new SilentCommandExecutor(new Sudo()));
            Objects.requireNonNull(plugin.getCommand("sudo")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Sudo()));

            Objects.requireNonNull(plugin.getCommand("xp")).setExecutor(new SilentCommandExecutor(new _XPExecutor()));
            Objects.requireNonNull(plugin.getCommand("xp")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new CoinsXPLevel()));

            Objects.requireNonNull(plugin.getCommand("location")).setExecutor(new Location());
            Objects.requireNonNull(plugin.getCommand("location")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Location());

            Objects.requireNonNull(plugin.getCommand("cosmetic")).setExecutor(new _CosmeticExecutor());
            Objects.requireNonNull(plugin.getCommand("cosmetic")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Cosmetic());

            Objects.requireNonNull(plugin.getCommand("channel")).setExecutor(new SilentCommandExecutor(new _ChannelExecutor()));
            Objects.requireNonNull(plugin.getCommand("channel")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.ChatChannel()));

            Objects.requireNonNull(plugin.getCommand("chatcolor")).setExecutor(new SilentCommandExecutor(new ChatColor()));
            Objects.requireNonNull(plugin.getCommand("chatcolor")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.ChatColor()));

            Objects.requireNonNull(plugin.getCommand("info")).setExecutor(new Info());
            Objects.requireNonNull(plugin.getCommand("info")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Info());

            Objects.requireNonNull(plugin.getCommand("item")).setExecutor(new SilentCommandExecutor(new Item()));
            Objects.requireNonNull(plugin.getCommand("item")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Item()));

            Objects.requireNonNull(plugin.getCommand("invsee")).setExecutor(new SilentCommandExecutor(new InventorySee()));
            Objects.requireNonNull(plugin.getCommand("invsee")).setTabCompleter(new SilentTabCompleter(new LocalOnlinePlayer()));

            Objects.requireNonNull(plugin.getCommand("heal")).setExecutor(new SilentCommandExecutor(new Heal()));
            Objects.requireNonNull(plugin.getCommand("heal")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlineAllPlayer()));

            Objects.requireNonNull(plugin.getCommand("enderchest")).setExecutor(new Enderchest());
            Objects.requireNonNull(plugin.getCommand("enderchest")).setTabCompleter(new LocalOnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("fly")).setExecutor(new SilentCommandExecutor(new Fly()));
            Objects.requireNonNull(plugin.getCommand("fly")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlinePlayerAndToggle()));

            Objects.requireNonNull(plugin.getCommand("fullbright")).setExecutor(new SilentCommandExecutor(new Fullbright()));
            Objects.requireNonNull(plugin.getCommand("fullbright")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new Toggle()));

            Objects.requireNonNull(plugin.getCommand("friend")).setExecutor(new _FriendExecutor());
            Objects.requireNonNull(plugin.getCommand("friend")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Friend());

            Objects.requireNonNull(plugin.getCommand("message")).setExecutor(new MessageSend());
            Objects.requireNonNull(plugin.getCommand("message")).setTabCompleter(new OnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("nickname")).setExecutor(new SilentCommandExecutor(new Nickname()));
            Objects.requireNonNull(plugin.getCommand("nickname")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Nickname()));

            Objects.requireNonNull(plugin.getCommand("party")).setExecutor(new _PartyExecutor());
            Objects.requireNonNull(plugin.getCommand("party")).setTabCompleter(new Party());

            Objects.requireNonNull(plugin.getCommand("messageprivacy")).setExecutor(new MessagePrivacy());
            Objects.requireNonNull(plugin.getCommand("messageprivacy")).setTabCompleter(new Privacy());
            Objects.requireNonNull(plugin.getCommand("partyprivacy")).setExecutor(new PartyPrivacy());
            Objects.requireNonNull(plugin.getCommand("partyprivacy")).setTabCompleter(new Privacy());
            Objects.requireNonNull(plugin.getCommand("friendprivacy")).setExecutor(new FriendPrivacy());
            Objects.requireNonNull(plugin.getCommand("friendprivacy")).setTabCompleter(new Privacy());

            Objects.requireNonNull(plugin.getCommand("ignore")).setExecutor(new _IgnoreExecutor());
            Objects.requireNonNull(plugin.getCommand("ignore")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Ignore());
            Objects.requireNonNull(plugin.getCommand("unignore")).setExecutor(new Unignore());
            Objects.requireNonNull(plugin.getCommand("unignore")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unignore());

            Objects.requireNonNull(plugin.getCommand("smite")).setExecutor(new SilentCommandExecutor(new Smite()));
            Objects.requireNonNull(plugin.getCommand("smite")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlineAllPlayer()));

            Objects.requireNonNull(plugin.getCommand("reply")).setExecutor(new MessageReply());
            Objects.requireNonNull(plugin.getCommand("reply")).setTabCompleter(new Blank()); // blank tab completer, to stop Bukkit from automatically filling arguments

            Objects.requireNonNull(plugin.getCommand("timezone")).setExecutor(new SilentCommandExecutor(new Timezone()));
            Objects.requireNonNull(plugin.getCommand("timezone")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Timezone()));

            Objects.requireNonNull(plugin.getCommand("uuid")).setExecutor(new UUID());
            Objects.requireNonNull(plugin.getCommand("uuid")).setTabCompleter(new AnyPlayer());


            Objects.requireNonNull(plugin.getCommand("mend")).setExecutor(new SilentCommandExecutor(new Mend()));
            Objects.requireNonNull(plugin.getCommand("mend")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlinePlayer()));

            Objects.requireNonNull(plugin.getCommand("dispose")).setExecutor(new SilentCommandExecutor(new Dispose()));
            Objects.requireNonNull(plugin.getCommand("dispose")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlinePlayer()));

            Objects.requireNonNull(plugin.getCommand("craft")).setExecutor(new SilentCommandExecutor(new Craft()));
            Objects.requireNonNull(plugin.getCommand("craft")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlinePlayer()));

            Objects.requireNonNull(plugin.getCommand("enchant")).setExecutor(new SilentCommandExecutor(new Enchant()));
            Objects.requireNonNull(plugin.getCommand("enchant")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Enchant()));

            Objects.requireNonNull(plugin.getCommand("enchanttable")).setExecutor(new SilentCommandExecutor(new EnchantTable()));
            Objects.requireNonNull(plugin.getCommand("enchanttable")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlinePlayer()));


            Objects.requireNonNull(plugin.getCommand("powertool")).setExecutor(new SilentCommandExecutor(new PowerTool()));
            Objects.requireNonNull(plugin.getCommand("powertool")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.PowerTool()));

            // MOD //
            Objects.requireNonNull(plugin.getCommand("ban")).setExecutor(new Ban());
            Objects.requireNonNull(plugin.getCommand("ban")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Ban());

            Objects.requireNonNull(plugin.getCommand("mute")).setExecutor(new Mute());
            Objects.requireNonNull(plugin.getCommand("mute")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Ban()); // ban command follows same syntax as mute

            Objects.requireNonNull(plugin.getCommand("kick")).setExecutor(new Kick());
            Objects.requireNonNull(plugin.getCommand("kick")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.OnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("unban")).setExecutor(new Unban());
            Objects.requireNonNull(plugin.getCommand("unban")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unban());

            Objects.requireNonNull(plugin.getCommand("unmute")).setExecutor(new Unmute());
            Objects.requireNonNull(plugin.getCommand("unmute")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unmute());

            Objects.requireNonNull(plugin.getCommand("warn")).setExecutor(new Warn());
            Objects.requireNonNull(plugin.getCommand("warn")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.OnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("punishment")).setExecutor(new SilentCommandExecutor(new _PunishmentExecutor()));
            Objects.requireNonNull(plugin.getCommand("punishment")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Punishment()));

            Objects.requireNonNull(plugin.getCommand("vanish")).setExecutor(new SilentCommandExecutor(new Vanish()));
            Objects.requireNonNull(plugin.getCommand("vanish")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new LocalOnlinePlayerAndToggle()));

            Objects.requireNonNull(plugin.getCommand("sql")).setExecutor(new SilentCommandExecutor(new SQL()));
            Objects.requireNonNull(plugin.getCommand("sql")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.SilentTabCompleter(new Blank())); // for security purposes we cannot give any tab completions

    }

    public static void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(plugin, plugin);

        EventService.registerHandler(PublicChatEvent.class, new ChatFallbackHandler(), Priority.LOWEST);
        EventService.registerHandler(LevelUpEvent.class, new LevelUpHandler(), Priority.LOWEST);
    }

    public static void startTasks() {
        handlePartyExpires();
        clearPartyInvites();
        fixOnlinePlayers();
        updateLastOnlineValues();
        deleteOfflineParties();
        runPunishmentSeq();
    }

    public static void requestCacheShipments() {


        List<String> list = new ArrayList<>();

        for (Map.Entry<String, Boolean> entry : initializedMap.entrySet()) {
            if (!entry.getValue()) {
                list.add(entry.getKey());
            }
        }

        Request.postRequest(apiEndpoint + "/cache", gson.toJson(Map.of("tables", gson.toJson(list))));

    }

    public static void registerPlaceholders() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Logger.info("Could not find PlaceholderAPI on this server. Ignoring...");
            return;
        }

        new Expansion(plugin).register();
        new RelationalExpansion(plugin).register();
    }

}
