package com.carrotguy69.cxyz.other;

import com.carrotguy69.cxyz.classes.http.Listener;
import com.carrotguy69.cxyz.classes.http.Requests;
import com.carrotguy69.cxyz.cmd.admin.*;
import com.carrotguy69.cxyz.cmd.admin.Print;
import com.carrotguy69.cxyz.cmd.admin.channel.Lock;
import com.carrotguy69.cxyz.cmd.admin.channel.Unlock;
import com.carrotguy69.cxyz.cmd.admin.coins.Coins;
import com.carrotguy69.cxyz.cmd.admin.rank._RankExecutor;
import com.carrotguy69.cxyz.cmd.admin.Parse;
import com.carrotguy69.cxyz.cmd.admin.xp.XP;
import com.carrotguy69.cxyz.cmd.general.*;
import com.carrotguy69.cxyz.cmd.general.Buy;
import com.carrotguy69.cxyz.cmd.general.ChatColor;
import com.carrotguy69.cxyz.cmd.general.Ignore;
import com.carrotguy69.cxyz.cmd.general.Nickname;
import com.carrotguy69.cxyz.cmd.general.Timezone;
import com.carrotguy69.cxyz.cmd.general.Unignore;
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
            Objects.requireNonNull(instance.getCommand("coins")).setExecutor(new Coins());
            Objects.requireNonNull(instance.getCommand("coins")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Coins());

            Objects.requireNonNull(instance.getCommand("rank")).setExecutor(new _RankExecutor());
            Objects.requireNonNull(instance.getCommand("rank")).setTabCompleter(new Rank());

            Objects.requireNonNull(instance.getCommand("broadcast")).setExecutor(new Broadcast());
            Objects.requireNonNull(instance.getCommand("broadcast")).setTabCompleter(new Blank());

            Objects.requireNonNull(instance.getCommand("port")).setExecutor(new Port());
            Objects.requireNonNull(instance.getCommand("port")).setTabCompleter(new Blank());

            Objects.requireNonNull(instance.getCommand("test")).setExecutor(new Test());
            Objects.requireNonNull(instance.getCommand("test")).setTabCompleter(new Blank());

            Objects.requireNonNull(instance.getCommand("lock")).setExecutor(new Lock());
            Objects.requireNonNull(instance.getCommand("lock")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Lock());

            Objects.requireNonNull(instance.getCommand("motd")).setExecutor(new Motd());
            Objects.requireNonNull(instance.getCommand("motd")).setTabCompleter(new Blank());

            Objects.requireNonNull(instance.getCommand("parse")).setExecutor(new Parse());
            Objects.requireNonNull(instance.getCommand("parse")).setTabCompleter(new Blank());

            Objects.requireNonNull(instance.getCommand("print")).setExecutor(new Print());
            Objects.requireNonNull(instance.getCommand("print")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Print());

            Objects.requireNonNull(instance.getCommand("unlock")).setExecutor(new Unlock());
            Objects.requireNonNull(instance.getCommand("unlock")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unlock());

            Objects.requireNonNull(instance.getCommand("xp")).setExecutor(new XP());
            Objects.requireNonNull(instance.getCommand("xp")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.XP());


        // GENERAL //
            Objects.requireNonNull(instance.getCommand("buy")).setExecutor(new Buy());
            Objects.requireNonNull(instance.getCommand("buy")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Buy());

            Objects.requireNonNull(instance.getCommand("channel")).setExecutor(new _ChannelExecutor());
            Objects.requireNonNull(instance.getCommand("channel")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.ChatChannel());

            Objects.requireNonNull(instance.getCommand("chatcolor")).setExecutor(new ChatColor());
            Objects.requireNonNull(instance.getCommand("chatcolor")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.ChatColor());

            Objects.requireNonNull(instance.getCommand("discord")).setExecutor(new Discord());

            Objects.requireNonNull(instance.getCommand("equip")).setExecutor(new Equip());
            Objects.requireNonNull(instance.getCommand("equip")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Buy()); // just a cosmetic tab completer

            Objects.requireNonNull(instance.getCommand("info")).setExecutor(new Info());
            Objects.requireNonNull(instance.getCommand("info")).setTabCompleter(new AnyPlayer()); // just a cosmetic tab completer

            Objects.requireNonNull(instance.getCommand("friend")).setExecutor(new _FriendExecutor());
            Objects.requireNonNull(instance.getCommand("friend")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Friend());

            Objects.requireNonNull(instance.getCommand("message")).setExecutor(new MessageSend());
            Objects.requireNonNull(instance.getCommand("message")).setTabCompleter(new OnlinePlayer());

            Objects.requireNonNull(instance.getCommand("nickname")).setExecutor(new Nickname());
            Objects.requireNonNull(instance.getCommand("nickname")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Nickname());

            Objects.requireNonNull(instance.getCommand("party")).setExecutor(new _PartyExecutor());
            Objects.requireNonNull(instance.getCommand("party")).setTabCompleter(new Party());

            // privacy settings
            Objects.requireNonNull(instance.getCommand("messageprivacy")).setExecutor(new MessagePrivacy());
            Objects.requireNonNull(instance.getCommand("messageprivacy")).setTabCompleter(new Privacy());
            Objects.requireNonNull(instance.getCommand("partyprivacy")).setExecutor(new PartyPrivacy());
            Objects.requireNonNull(instance.getCommand("partyprivacy")).setTabCompleter(new Privacy());
            Objects.requireNonNull(instance.getCommand("friendprivacy")).setExecutor(new FriendPrivacy());
            Objects.requireNonNull(instance.getCommand("friendprivacy")).setTabCompleter(new Privacy());

            Objects.requireNonNull(instance.getCommand("ignore")).setExecutor(new Ignore());
            Objects.requireNonNull(instance.getCommand("ignore")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Ignore());
            Objects.requireNonNull(instance.getCommand("unignore")).setExecutor(new Unignore());
            Objects.requireNonNull(instance.getCommand("unignore")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unignore());



            Objects.requireNonNull(instance.getCommand("reply")).setExecutor(new MessageReply());
            Objects.requireNonNull(instance.getCommand("reply")).setTabCompleter(new Blank()); // blank tab completer, to stop Bukkit from automatically filling arguments

            Objects.requireNonNull(instance.getCommand("timezone")).setExecutor(new Timezone());
            Objects.requireNonNull(instance.getCommand("timezone")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Timezone());

            Objects.requireNonNull(instance.getCommand("unequip")).setExecutor(new Unequip());
            Objects.requireNonNull(instance.getCommand("unequip")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Buy()); // just a cosmetic tab completer



        // MOD //
            Objects.requireNonNull(instance.getCommand("ban")).setExecutor(new Ban());
            Objects.requireNonNull(instance.getCommand("ban")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Ban());

            Objects.requireNonNull(instance.getCommand("mute")).setExecutor(new Mute());
            Objects.requireNonNull(instance.getCommand("mute")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Ban()); // ban command follows same syntax as mute

            Objects.requireNonNull(instance.getCommand("kick")).setExecutor(new Kick());
            Objects.requireNonNull(instance.getCommand("kick")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.OnlinePlayer());

            Objects.requireNonNull(instance.getCommand("unban")).setExecutor(new Unban());
            Objects.requireNonNull(instance.getCommand("unban")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unban());

            Objects.requireNonNull(instance.getCommand("unmute")).setExecutor(new Unmute());
            Objects.requireNonNull(instance.getCommand("unmute")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Unmute());

            Objects.requireNonNull(instance.getCommand("warn")).setExecutor(new Warn());
            Objects.requireNonNull(instance.getCommand("warn")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.OnlinePlayer());

            Objects.requireNonNull(instance.getCommand("punishment")).setExecutor(new _PunishmentExecutor());
            Objects.requireNonNull(instance.getCommand("punishment")).setTabCompleter(new com.carrotguy69.cxyz.tabCompleters.Punishment());

    }

    public static void registerEvents() {
        instance.getServer().getPluginManager().registerEvents(instance, instance);
    }

    public static void startTasks() {
        handlePartyExpires();
        fixIgnorables();
        fixOnlinePlayers();
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
        Requests.postRequest(api_endpoint + "/cache", gson.toJson(Map.of("body", "good morning inanimate server!"))); // The API does not even look at this body, it just wants the request in post because it feels nicer.
     }

}
