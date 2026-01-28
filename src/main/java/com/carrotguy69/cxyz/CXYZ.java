package com.carrotguy69.cxyz;

import com.carrotguy69.cxyz.http.Listener;
import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.models.config.Cosmetic;
import com.carrotguy69.cxyz.models.config.GameServer;
import com.carrotguy69.cxyz.models.config.PlayerRank;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.config.shorthand.Shorthand;
import com.carrotguy69.cxyz.models.db.*;
import com.carrotguy69.cxyz.other.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.carrotguy69.cxyz.events.LeaveEvent.onLeave;
import static com.carrotguy69.cxyz.events.ChatEvent.handleChat;
import static com.carrotguy69.cxyz.events.JoinEvent.onJoin;
import static com.carrotguy69.cxyz.other.Startup.startEndpoints;
import static com.carrotguy69.cxyz.other.Tasks.createAnnouncementTasks;

public final class CXYZ extends JavaPlugin implements org.bukkit.event.Listener {
    public static CXYZ plugin;

    public static FileConfiguration configYaml;

    public static String server_name;
    public static String server_ip;
    public static String api_endpoint;
    public static String webhook_endpoint;
    public static GameServer this_server;

    // Even though these ranks are not defined yet, they will almost never be null (excluding first few milliseconds of server startup)
    public static int api_timeout;
    public static int this_port;

    public static PlayerRank defaultRank;

    public static List<PlayerRank> ranks = new ArrayList<>();
    public static List<GameServer> servers = new ArrayList<>();

    public static List<BaseChannel> channels = new ArrayList<>();

    public static List<String> muteRestrictions = new ArrayList<>();

    // Database copies
    public static Map<UUID, NetworkPlayer> users = new ConcurrentHashMap<>();
    public static Map<Long, Punishment> punishmentIDMap = new ConcurrentHashMap<>(); // This is the real unique map that stores all punishments.

    public static Map<UUID, Long> lastMessage = new HashMap<>();

    public static Map<UUID, Party> parties = new ConcurrentHashMap<>(); // <Owner, Party>
    public static Map<UUID, PartyExpire> partyExpires = new HashMap<>(); // <Player(UUID), PartyExpire>

    public static Multimap<UUID, Message> messageMap = ArrayListMultimap.create(); // UUID in the message map represents the recipient UUID (kind of backwards).
    public static Multimap<UUID, PartyInvite> partyInvites = ArrayListMultimap.create(); // The UUID is the sender (the one who sent the invite)
    public static Multimap<UUID, FriendRequest> friendRequests = ArrayListMultimap.create(); // The UUID is the sender (the one who sent the friend request)

    public static List<Cosmetic> cosmetics = new ArrayList<>();

    public static List<Shorthand> shorthandCommands = new ArrayList<>();

    public static List<String> enabledDebugs = new ArrayList<>();


    public static int partyInvitesExpireAfter;
    public static int partyAutoKickAfter;
    public static int friendRequestsExpireAfter;

    public static String timezone;
    public static String dateTimeFormat;
    public static String dateTimeShortFormat;
    public static String permanentString;

    public static Listener listener;

    public static List<Integer> taskIDs = new ArrayList<>(); // Any task that uses a timer must be able to be canceled on disable.
    
    public static Gson gson = new Gson();

    public static String f(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static Map<String, Boolean> initializedMap = new HashMap<>();

    public static boolean isInitialized() {
        for (Map.Entry<String, Boolean> entry : initializedMap.entrySet()) {
            if (entry.getValue() == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean chatFilterEnabled = false;

    public CommandMap commandMap = null;

    public static FileConfiguration cosmeticsYML = null;
    public static FileConfiguration msgYML = null;


/*

   [❌] ISSUES:

   - Fix message parser actions:
        - ✅ is: (text)[ACTION:actionText]
        - ❌ Should be: [text](ACTION:actionText)

   - Punishment edit tab completer does not work

   - What the hell is wrong with my coins system, level and xp?
   - So why do I have to buy the cosmetics every time I get on the server? is something not registering properly or is something not getting saved?

   - Editor mod not formatting in any messages

   - The way the punishment message interacts with the staff channel is odd.

   - MessageParser issue where these multiple line messages have a trailing new line and I can't figure it out
   - I will often get marked as offline with: (NetworkPlayer.isOnline() == false). What causes this, the join event registers? Maybe there is a task that is setting me offline?

   - Playtime value in /info is wrong
   - Playtime value in DB is likely wrong, we should look at that

   [🧪] TEST:
   - Shorthand commands (register with mismatching name, load with bad params {non-bracketed})
   - Punishment info subcommands (/punishment ...)



   [➕] ADD/IMPLEMENT:
   - Enable/disable parties
   - Detailed logging on startup, and do warning + continue on error instead of throwing InvalidConfigException. No null values in objects allowed, enforce it!
   - Add party max size value
   - Ensure /debug actually changes and saves config values
   - A page generator class for long list commands. We will have a string list of entries, a max entries integer, a format for each page including the header and the footer,
     and the class should be able to generate a list page given the page # and using the header, footer, and entries.
   - Auto cosmetic equip on join (if allowed), if not -> auto unequip
   - Defensive programming to defend against the evil SQL database, meaning: we need to enforce defaults whenever a player SQL entry gives us any invalid thing (rank, channel).
   - Throw a config exception in startup if core default channels are not assigned.
   - More debuggers (shorthands, NetworkPlayer actions (move to here instead of living in shipmentdelivery), )
   - Ensure that TextComponents are not being re-created (create a single TextComponent for all targets)
   - Create a ChatFilterRule class - a dumb chat filter class to block simple profanity.
   - A rule has a list of enabled channels, a list of prohibited content, and a list of command actions that are triggered.
   - A rule can be bypassed with a special permission like “chat.<channel>.bypass-filter” which bypasses filters for the entire channel.
   - Messages that violate the rule are automatically cancelled, and you can specify command actions in config.yml.
   - Report command.
   - Add QOL commands (fb, heal, fly, smite, repair, tpall, tpa)
   - /skin command (could be external plugin), but we should have a functionality (maybe in users table) to store the skin name in order to apply it when joining other servers.
   - Ensure /debug actually changes the config
   - Remove "admin", "mod", and "general" permission categories as they kind of overlap.
   - Define permissions (and defaults) in plugin.yml
   - Add page # support for list commands (including subcommand lists).
   - /punishment allhistory to view all server punishments (MUST have pages)
   - Errors, move duplicate states to errors.{something-new}
   - In invalid args errors, we can add acceptable values in the map formatter as opposed to showing the user only what they did wrong.
   - Look through Tasks and move them to the API.
   - A way to convert RGB colors to legacy colors (for hover, tab text)
   - Go into config.yml and define one example cosmetic from each type. Go into CosmeticUtils or create another class and physically create these cosmetics (items, trails, effects).
   - Levelup: Add a player facing XP and coin add message (can be left blank in config if admin desires)
   - Levelup: Add a player level up message (and play the sound)
   - Implement shorthand commands, and then with this add (/rules, /help, /allow {channel} {player})
   - Events we can subscribe: on levelup, onXPAdd, onXPSet (check if level up),

   [💡] FEATURE IDEAS/LAYOUT:

   Shorthand commands
       Config schema:
       permban:
          command: “/permban {1} {…}”
          tab-completer: "/permban {player} {...}"
          executes:
              - ban {1} permanent {…}

      placeholders:
      - {channel} (tab completes with channels)
      - {rank} (tab completes with ranks)
      - {player} (tab completes with all players)


   [🔥] v1.1 UPDATE:
   - Enable Cosmetics w/ full GUI support
   - Rank up notifications (public or private) (including if a new cosmetic is available).
   - Ping sound to a player when their name is mentioned in the chat
   - On hover: "View Player". On click: open gui. options (player info, add friend, message player, invite to party, warp to server)
   - /info command to supplement this ^
   - A database table that simply logs all player actions that involve the API.
   - Figure out how to restrict certain non-command-based features to ranked users (like in MessageSend.sendMessage(), the color is stripped for non default users, add some functionality!)
   - Quest system with Quest NPC. Awards coins/xp on completion of quests. There should be only a select amount of quests available to a player weekly - and they should be generated uniquely to the player.
   - Queued message system
       Add queued_messages column in DB table.
       NP.addQueuedMessage(String)
       removeQueuedMessage(int index)
       Queued messages should remain in original order. FIFO but enforced with an SQL friendly datatype/converter
       Mass functions to add/remove queued messages.
       Mass remove should remove the message by its content.
       {player} placeholder should be used and formatted at send time
   */

    private CommandMap getCommandMap() {
        try {
            PluginManager pm = Bukkit.getPluginManager();

            if (!(pm instanceof SimplePluginManager)) {
                throw new IllegalStateException("Unsupported PluginManager: " + pm.getClass());
            }

            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);

            CommandMap map = (CommandMap) field.get(pm);

            if (map == null) {
                throw new IllegalStateException("CommandMap is null");
            }

            return map;

        } catch (Exception e) {
            throw new RuntimeException("Failed to access CommandMap", e);
        }
    }



    @Override
    public void onEnable() {
        plugin = JavaPlugin.getPlugin(CXYZ.class);

        saveDefaultConfig();
        reloadConfig();

        File messageYML = new File(getDataFolder(), "messages.yml");
        if (!messageYML.exists()) {
            saveResource("messages.yml", false);
        }

        getConfig().options().copyDefaults(true); // Copies default values (values that are not set by user.) Keep this as it will be useful for version changes.


        saveConfig();

        commandMap = getCommandMap();

        Constants.loadConstantsFromYAML();

        Startup.doThings();


        try {
            startEndpoints();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        initializedMap.put("users", false);
        initializedMap.put("parties", false);
        initializedMap.put("partyInvites", false);
        initializedMap.put("partyExpires", false);
        initializedMap.put("messages", false);
        initializedMap.put("punishments", false);

        Bukkit.getScheduler().runTaskLater(plugin, Startup::requestCacheShipments, 1L);

        // Maybe should wait until far later, but need to account for plugin restarts with plugman.
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                onJoin(p);
            }
        }, 1L);

        createAnnouncementTasks();


    }

    @Override
    public void onDisable() {
        if (this_server != null) {
            Request.postRequest(api_endpoint + "/sql", String.format("{\"query\" : \"UPDATE users SET online = false WHERE server = '%s'\", \"table\" : \"users\"}", this_server.getName()));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            onLeave(p);
            p.kickPlayer("");
        }

        if (listener != null) {
            listener.stop();
            listener = null;
        }

        for (int n : taskIDs) {
            Bukkit.getScheduler().cancelTask(n);
        }

        Bukkit.getServer().setWhitelist(true);

        try {
            if (commandMap instanceof SimpleCommandMap) {
                SimpleCommandMap simpleMap = (SimpleCommandMap) commandMap;

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);

                Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(simpleMap);

                for (Command cmd : knownCommands.values()) {
                    cmd.unregister(simpleMap);
                    knownCommands.values().removeIf(c -> c == cmd);
                }
            }
        } catch (Exception ex) {
            Logger.logStackTrace(ex);
        }

        Bukkit.getScheduler().cancelTasks(plugin);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        // REMINDER: The purpose of this plugin is only to perform basic essential functions related to player data and network operations.
        // Per server and per game operations should be handled by the game plugins.

        Player p = e.getPlayer();

        onJoin(p); // Fixes the players row in the database. Updates null values.

        e.setJoinMessage(""); // Let other server plugins handle server specific joining
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        onLeave(p);
        e.setQuitMessage("");
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        handleChat(e);
    }

}