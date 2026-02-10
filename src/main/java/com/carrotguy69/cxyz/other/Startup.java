package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.cmd.admin.Debug;
import com.carrotguy69.cxyz.cmd.general.privacy.ignore._IgnoreExecutor;
import com.carrotguy69.cxyz.http.Listener;
import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.cmd.admin.*;
import com.carrotguy69.cxyz.cmd.admin.Print;
import com.carrotguy69.cxyz.cmd.admin.channel.ChannelLock;
import com.carrotguy69.cxyz.cmd.admin.channel.ChannelUnlock;
import com.carrotguy69.cxyz.cmd.admin.coins._CoinsExecutor;
import com.carrotguy69.cxyz.cmd.admin.level._LevelExecutor;
import com.carrotguy69.cxyz.cmd.admin.rank._RankExecutor;
import com.carrotguy69.cxyz.cmd.general.Equip;
import com.carrotguy69.cxyz.cmd.general.Parse;
import com.carrotguy69.cxyz.cmd.admin.xp._XPExecutor;
import com.carrotguy69.cxyz.cmd.general.*;
import com.carrotguy69.cxyz.cmd.general.Buy;
import com.carrotguy69.cxyz.cmd.general.ChatColor;
import com.carrotguy69.cxyz.cmd.general.Nickname;
import com.carrotguy69.cxyz.cmd.general.Timezone;
import com.carrotguy69.cxyz.cmd.general.Unequip;
import com.carrotguy69.cxyz.cmd.general.privacy.ignore.Unignore;
import com.carrotguy69.cxyz.cmd.general.channel._ChannelExecutor;
import com.carrotguy69.cxyz.cmd.general.friend._FriendExecutor;
import com.carrotguy69.cxyz.cmd.general.message.MessageReply;
import com.carrotguy69.cxyz.cmd.general.message.MessageSend;
import com.carrotguy69.cxyz.cmd.general.privacy.FriendPrivacy;
import com.carrotguy69.cxyz.cmd.general.privacy.MessagePrivacy;
import com.carrotguy69.cxyz.cmd.general.privacy.PartyPrivacy;
import com.carrotguy69.cxyz.cmd.mod.*;
import com.carrotguy69.cxyz.cmd.general.party._PartyExecutor;
import com.carrotguy69.cxyz.cmd.mod.Ban;
import com.carrotguy69.cxyz.cmd.mod.Unban;
import com.carrotguy69.cxyz.cmd.mod.Unmute;
import com.carrotguy69.cxyz.cmd.mod.punishment._PunishmentExecutor;
import com.carrotguy69.cxyz.tabCompleters.*;

import java.io.IOException;
import java.net.BindException;
import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.other.Tasks.*;

public class Startup {

    public static void doThings() {
        registerCommands();
        registerEvents();
        startTasks();
    }

    public static void startEndpoints() throws IOException {
        try {
            listener = new Listener(this_port);
        }
        catch (IOException ex) {

            if (ex instanceof BindException) {
                Logger.severe("BindException, HTTP endpoint not registered! Use /port to set a new port and then restart this plugin!");
            }
            else {
                Logger.logStackTrace(ex);
            }
            throw new RuntimeException();
        }
    }

    public static void registerCommands() {
        // ADMIN //
            Objects.requireNonNull(plugin.getCommand("coins")).setExecutor(new _CoinsExecutor());
            Objects.requireNonNull(plugin.getCommand("coins")).setTabCompleter(new CoinsXPLevel());

            Objects.requireNonNull(plugin.getCommand("rank")).setExecutor(new _RankExecutor());
            Objects.requireNonNull(plugin.getCommand("rank")).setTabCompleter(new Rank());

            Objects.requireNonNull(plugin.getCommand("broadcast")).setExecutor(new Broadcast());
            Objects.requireNonNull(plugin.getCommand("broadcast")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("port")).setExecutor(new Port());
            Objects.requireNonNull(plugin.getCommand("port")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("debug")).setExecutor(new Debug());
            Objects.requireNonNull(plugin.getCommand("debug")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Debug());

            Objects.requireNonNull(plugin.getCommand("ping")).setExecutor(new Ping());
            Objects.requireNonNull(plugin.getCommand("ping")).setTabCompleter(new OnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("show")).setExecutor(new Show());
            Objects.requireNonNull(plugin.getCommand("show")).setTabCompleter(new OnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("test")).setExecutor(new Test());
            Objects.requireNonNull(plugin.getCommand("test")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("level")).setExecutor(new _LevelExecutor());
            Objects.requireNonNull(plugin.getCommand("level")).setTabCompleter(new CoinsXPLevel());

            Objects.requireNonNull(plugin.getCommand("lock")).setExecutor(new ChannelLock());
            Objects.requireNonNull(plugin.getCommand("lock")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Lock());

            Objects.requireNonNull(plugin.getCommand("motd")).setExecutor(new Motd());
            Objects.requireNonNull(plugin.getCommand("motd")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("parse")).setExecutor(new Parse());
            Objects.requireNonNull(plugin.getCommand("parse")).setTabCompleter(new Blank());

            Objects.requireNonNull(plugin.getCommand("print")).setExecutor(new Print());
            Objects.requireNonNull(plugin.getCommand("print")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Print());

            Objects.requireNonNull(plugin.getCommand("unlock")).setExecutor(new ChannelUnlock());
            Objects.requireNonNull(plugin.getCommand("unlock")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unlock());

            Objects.requireNonNull(plugin.getCommand("xp")).setExecutor(new _XPExecutor());
            Objects.requireNonNull(plugin.getCommand("xp")).setTabCompleter(new CoinsXPLevel());


        // GENERAL //
            Objects.requireNonNull(plugin.getCommand("buy")).setExecutor(new Buy());
            Objects.requireNonNull(plugin.getCommand("buy")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Buy());

            Objects.requireNonNull(plugin.getCommand("channel")).setExecutor(new _ChannelExecutor());
            Objects.requireNonNull(plugin.getCommand("channel")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.ChatChannel());

            Objects.requireNonNull(plugin.getCommand("chatcolor")).setExecutor(new ChatColor());
            Objects.requireNonNull(plugin.getCommand("chatcolor")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.ChatColor());

            Objects.requireNonNull(plugin.getCommand("equip")).setExecutor(new Equip());
            Objects.requireNonNull(plugin.getCommand("equip")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Equip());

            Objects.requireNonNull(plugin.getCommand("info")).setExecutor(new Info());
            Objects.requireNonNull(plugin.getCommand("info")).setTabCompleter(new AnyPlayer()); // just a cosmetic tab completer

            Objects.requireNonNull(plugin.getCommand("friend")).setExecutor(new _FriendExecutor());
            Objects.requireNonNull(plugin.getCommand("friend")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Friend());

            Objects.requireNonNull(plugin.getCommand("message")).setExecutor(new MessageSend());
            Objects.requireNonNull(plugin.getCommand("message")).setTabCompleter(new OnlinePlayer());

            Objects.requireNonNull(plugin.getCommand("nickname")).setExecutor(new Nickname());
            Objects.requireNonNull(plugin.getCommand("nickname")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Nickname());

            Objects.requireNonNull(plugin.getCommand("party")).setExecutor(new _PartyExecutor());
            Objects.requireNonNull(plugin.getCommand("party")).setTabCompleter(new Party());

            // privacy settings
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



            Objects.requireNonNull(plugin.getCommand("reply")).setExecutor(new MessageReply());
            Objects.requireNonNull(plugin.getCommand("reply")).setTabCompleter(new Blank()); // blank tab completer, to stop Bukkit from automatically filling arguments

            Objects.requireNonNull(plugin.getCommand("timezone")).setExecutor(new Timezone());
            Objects.requireNonNull(plugin.getCommand("timezone")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Timezone());

            Objects.requireNonNull(plugin.getCommand("unequip")).setExecutor(new Unequip());
            Objects.requireNonNull(plugin.getCommand("unequip")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unequip());



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

            Objects.requireNonNull(plugin.getCommand("punishment")).setExecutor(new _PunishmentExecutor());
            Objects.requireNonNull(plugin.getCommand("punishment")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Punishment());

    }

    public static void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(plugin, plugin);
    }

    public static void startTasks() {
        handlePartyExpires();
        handlePartyInvites();
//        fixChannels();
        fixOnlinePlayers();
        updateLastOnlineValues();
        deleteOfflineParties();

    }
//
//    public static void setWorldDefaults(World w) {
//        w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
//        w.setGameRule(GameRule.DISABLE_RAIDS, true);
//        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
//        w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
//        w.setGameRule(GameRule.DO_FIRE_TICK, false);
//        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
//        w.setDifficulty(Difficulty.NORMAL);
//    }

    public static void requestCacheShipments() {
        Request.postRequest(apiEndpoint + "/cache", gson.toJson(Map.of("body", "good morning inanimate server!"))); // The API does not even look at this body, it just wants the request in post because it feels nicer.
     }

}
