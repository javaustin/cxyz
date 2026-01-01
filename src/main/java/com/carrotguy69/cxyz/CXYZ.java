package com.carrotguy69.cxyz;

import com.carrotguy69.cxyz.classes.http.Listener;
import com.carrotguy69.cxyz.classes.http.Requests;
import com.carrotguy69.cxyz.classes.models.config.Cosmetic;
import com.carrotguy69.cxyz.classes.models.config.GameServer;
import com.carrotguy69.cxyz.classes.models.config.PlayerRank;
import com.carrotguy69.cxyz.classes.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.classes.models.db.*;
import com.carrotguy69.cxyz.other.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.carrotguy69.cxyz.events.LeaveEvent.onLeave;
import static com.carrotguy69.cxyz.events.ChatEvent.handleChat;
import static com.carrotguy69.cxyz.events.JoinEvent.onJoin;
import static com.carrotguy69.cxyz.other.Startup.startEndpoints;
import static com.carrotguy69.cxyz.other.Tasks.createAnnouncementTasks;

public final class CXYZ extends JavaPlugin implements org.bukkit.event.Listener {
    public static CXYZ instance;

    public static FileConfiguration configYaml;

    public static String discord_url;
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

    public static List<String> allPlayers = new ArrayList<>();
    public static List<String> muteRestrictions = new ArrayList<>();

    // Database copies
    public static Map<UUID, NetworkPlayer> users = new ConcurrentHashMap<>();
    public static Map<Long, Punishment> punishmentIDMap = new ConcurrentHashMap<>(); // This is the real unique map that stores all punishments.
    public static Multimap<UUID, Message> messageMap = ArrayListMultimap.create(); // This also stores all punishments but is multi-keyed by UUIDs to get player punishments easier.
    // UUID in the message map represents the recipient UUID.

    public static Map<UUID, Long> lastMessage = new HashMap<>();

    public static Map<UUID, Party> parties = new ConcurrentHashMap<>();
    public static Multimap<UUID, PartyInvite> partyInvites = ArrayListMultimap.create();
    public static Map<UUID, PartyExpire> partyExpires = new HashMap<>();

    public static Multimap<UUID, FriendRequest> friendRequests = ArrayListMultimap.create(); // The first UUID is the one that sent out the friend request

    public static List<Cosmetic> cosmetics = new ArrayList<>();


    public static int partyInvitesExpireAfter;
    public static int friendRequestsExpireAfter;

    public static String timezone;
    public static String dateTimeFormat;
    public static String dateTimeShortFormat;
    public static String permanentString;

    public static Listener listener;

    public static List<Integer> taskIDs = new ArrayList<>(); // Any task that uses a timer must be able to be cancelled on disable.
    
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

    public static FileConfiguration cosmeticsYML = null;
    public static FileConfiguration msgYML = null;


    /*

    [➕] TODO:

    Fix:
    - RGB colors dont render properly (they instead default to the last singular color code)
    - Our message parser did not like whatever change we made to the forceColor function.
    - Tab complete index out of bounds (@ Privacy.java:55)
    - In /info/whois: some values will not exist or appear as blank. This is as intended in use of plugin...
      ... but looks trash in the info message. We need to ensure all values in the info message work as intended, before we scale it down.
      ... example: in the {pl}
    - Test all new privacy functions
    - Yeah whatever we've done with the new forceColor function is terrible


    Modules:
        - ✅ Friends system.
        - ✅ Nickname system.
        - ✅ Rank system.
        - Cosmetics system.
        - ✅ Moderation commands (ban, mute, warn, kick).
        - ✅ Announcements module (Use config, implement ignoring features).
        - Shorthand commands module.

    Backend:
        - ✅ Delete all messages older than 5 minutes.
        - ✅ Trim down backend code. We do not need many functions besides create, delete, and modify.
        - ✅ Add concurrency checks. No duplicate party creating.
        - Ensure for every table (where necessary), duplicate UUID's are not able to be created.


    Channels (still need to do):
    ⚠️ (test)
    - ✅ Announcement channel can be just a custom channel, it does not require any custom implementations.
    - ✅ Implement rest of chat channel methods: /channel lock (also create a MessageKey and send a message in the channel when that happens)
    - ✅ Add channel aliases to tab completers?
    - ✅ Create a task that ensures that players do not still have channels ignored that are now set to `ignorable = false`

    Unnecessary states:
    - When a player is trying to set something to a setting, but it already is at that setting, this is wasteful to allow. We need to catch these and deny these actions with a new MessageKey for each.
        Like:
            - /lock
            - /unlock
           ... plus more

    - There are not many is self checks in player interactions. We should probably fix this very very soon.
    - We will probably want to rename public to all just in config.
    - ✅ Go through all commands and ensure they have permissions enforced
    - ✅ Add task that gets all online players and sets their data as online and on this-server.
    - ✅ Allow functionality to delete a punishment. /punishments {player}, /punishment clear {player}, /punishment clear {id}
    - ✅ For message parser, it will be friendlier to provide a warning when the syntax is messed up and then return the unparsed...
      ... or even return the correctly parsed up to the point, and chop it off. Then log a warning.
    - Add page # support for list commands (including subcommand lists).
    - Add essentials commands (heal, fly, smite, repair, tpall, )
    - Add task that updates last online value every few minutes (maybe 2 minutes?)
    - Why not actually test party expires.
    - Add report command.
    - ✅ Add /messagePrivacy
    - ✅ Add /friendPrivacy
    - ✅ Add /partyPrivacy
    - Go into config.yml and define one example cosmetic from each type. Go into CosmeticUtils or create another class and physically create these cosmetics (items, trails, effects).
    - Look through Tasks and move them to the API.
    - Add some feature which allows an admin to add "-1" to an arg or something that slices the string and removes the last trailing spaces...
      ... Since we have ranks that have trailing spaces, we should also allow admins to edit the placeholders and automatically remove trailing space. This can be even be embedded in the parser or formatPlaceholder functions!
    - /skin command (could be external plugin), but we should have a functionality (maybe in users table) to store the skin name in order to apply it when joining other servers.
    - ✅ Create /ignore <player> command
    - ✅ Create /info command (can be pure text, no need for GUI yet)
    - ✅ Chat /lock command (with channel arguments -> default to all)

    [❌] ISSUES:
        - So why do I have to buy the cosmetics every time i get on the server? is something not registering properly or is something not getting saved?
        - There is annoying new line after the party list message. It is not written in the config (like "\n"), and there is no whitespace. Very confuzzling!
        - The new requests still send duplicate requests when not warranted. Consider removing the retry functionality temporarily.
        - I will often get marked as offline with: (NetworkPlayer.isOnline() == false). What causes this, the join event registers? Maybe there is a task that is setting me offline?

    [⚠️] DESIGN ISSUES/CONSIDERATIONS
          - Should we be remove disallowed options from tab completers, or should we allow all and then deny? NO! Show them everything unless its acting as a list of things you can interact with
          - Have we figured out how to handle party expires? Do they delete on the backend? Probably not.
          - I need to decide on what to call ignore channel. Is it ignore channel or mute channel? Both versions conflict with another command. Ignore channel conflicts with ignore user, mute channel conflicts with mute user.
          - Backend system for parties has to be super stress tested. If we are using the synchronous ways and lying to the client that everything is fine - it better be fine.
          - Consider messages sent to console could have the inner elements (like hover). How can you parse this cleanly for both players and the console?

          The tab completer rule:
                - Tab completers should keep players, channels, and other objects that should not be seen by any player private. It should not be returned in any case.
                - However all information should be accessible to admins.
                - On certain actions that have a specific undo command (like lock, unlock... mute, unmute),
                  Usually if an admin wants to do something to a specific somebody/something, they already know what/who it is.
                  But usually if an admin wants to undo something, they have to look up what needs to be undone, so its more confusing if that returns all.
                - So options should be returned on the regular command or else the admins will think it doesn't exist and the plugin is breaking.
                - But on the undo action, only the options that can be undone should be returned.



    [💡] FEATURE IDEAS/LAYOUT:

    - Queued message system
        When sending a message to a player, instead of denying the action because they are offline, store the message in a queue (database table).
        This should not be used for messages systems, that's way too much work for me, but server updates would be cool ("Check out this new game mode carrot!")

    - Info Command
        When ran /info cerrot:
            ------------- cerrot --------------
            Username: {username}
            Rank: {rank}
            Status: &a⏺ Online (or) &7⏺ Offline
            Server: {server}
            First Join: {first join timestamp} (#{join-position})
            Last Online: {last online timestamp}
            Level: {level} ({xp progress}/{xp levelup req})
            Playtime: {playtime}
            Game Stats: {click to view stats}

    [🔥] v1.1 UPDATE:
    - Enable Cosmetics w/ full GUI support
    - Rank up notifications (public or private) (including if a new cosmetic is available).
    - Ping sound to a player when their name is mentioned in the chat
    - On hover: "View Player". On click: open gui. options (player info, add friend, message player, invite to party, warp to server)
    - /info command to supplement this ^
    - A database table that simply logs all player actions that involve the API.
    - Figure out how to restrict certain non-command-based features to ranked users (like in MessageSend.sendMessage(), the color is stripped for non default users, add some functionality!)
    - Quest system with Quest NPC. Awards coins/xp on completion of quests. There should be only a select amount of quests available to a player weekly - and they should be generated uniquely to the player.
    */

    @Override
    public void onEnable() {
        instance = JavaPlugin.getPlugin(CXYZ.class);

        saveDefaultConfig();
        reloadConfig();

        File messageYML = new File(getDataFolder(), "messages.yml");
        if (!messageYML.exists()) {
            saveResource("messages.yml", false);
        }

        getConfig().options().copyDefaults(true); // Copies default values (values that are not set by user.) Keep this as it will be useful for version changes.


        saveConfig();

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

        Bukkit.getScheduler().runTaskLater(instance, Startup::requestCacheShipments, 1L);

        // Maybe should wait until far later, but need to account for plugin restarts with plugman.
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                onJoin(p);
            }
        }, 1L);

        createAnnouncementTasks();
    }

    @Override
    public void onDisable() {
        if (this_server != null) {
            Requests.postRequest(api_endpoint + "/sql", String.format("{\"query\" : \"UPDATE users SET online = false WHERE server = '%s'\", \"table\" : \"users\"}", this_server.getName()));
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

        Bukkit.getScheduler().cancelTasks(instance);
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