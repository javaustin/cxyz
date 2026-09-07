# CXYZ

CXYZ is a core plugin that provides all the features and commands you'd need for any [Bukkit](https://dev.bukkit.org/) or [Spigot](https://hub.spigotmc.org) based server. 
It was created for and named after my own Minecraft network `cerrot.xyz`. 
It can be installed on a single server, or it could be installed on many different servers on a network.
It handles player chat, ranks, parties, moderation, leveling, and saves all this 
data on a preconfigured [backend database](https://github.com/javaustin/cxyzAPI), with no proxy needed.

---

## Important Notes
- This project depends on the [cxyzAPI](https://github.com/javaustin/cxyzAPI) for its API and database solution.
- **NO** generative AI was used to write or modify any code in this project.


---

## Features
_There are alot..._
- **Player Identity**
  - CXYZ NetworkPlayer(s) handle all the data that needs to be stored, such as ranks, nicknames, coins & xp, etc.
  - Player data is accessible anytime by the plugin, from anywhere, even if the player is offline.
- **Player Ranks**
  - A fully featured player rank system can be found in this plugin, with the ability to set custom rank prefixes, chat colors, and even cooldowns.
  - Many other systems in the project respect the rank system (e.g. you can block non VIP's from using a certain chat channel).
  - Easily add or remove ranks with the `/rank` command.
- **Unlimited Customizability**
  - You have the ability to customize every single message in the plugin by modifying `messages.yml`
  - Using the [custom message parser](https://example.com/REPLACE_ME), you can create clickable text components in any Minecraft chat message.
  - RGB colors and gradients are also supported by using the Bukkit RGB syntax before the text (e.g., #FF0000 → &x&F&F&0&0&0&0)
- **Multichannel Chat System**
  - CXYZ features a multichannel chat system in which players can switch channels to their discretion.
  - Each channel can have its own chat format, aliases, trigger prefixes, and even discord webhook integration. 
  - Easily switch channels with the `/chat <channel>` command.
  - Core channels (public, party, and message) work out of the box.
  - Custom channels (like private staff channels, or even public custom channels) can be created in the config.yml.
  - Easily set up automatic chat filters per-channel to block unwanted content. Admins can even lock a channel completely with `/channel lock`.
- **Social**
  - Allow players to create their own parties, invite players, kick, set new party leaders, and even warp players to new servers.
  - Developers can reference the CXYZ party API so their minigames respect existing parties.
  - Players can send and accept friend requests from/to each other, and see when their friends are online.
  - Any player can ignore another players messages using `/ignore`, or `/unignore`.
  - To prevent all interactions players can also set their message and party request settings with `/messageprivacy` and `/partyprivacy`.
- **Server Moderation**
  - Admins or moderators can use the full punishment suite which features bans, temp-bans, mutes, kicks, even warnings.
  - Using `config.yml` server managers can set the default durations and default reasons for any punishment type.
  - You may also use the `config.yml` file to restrict messaging commands for a player when they are muted.
  - CXYZ will automatically handle unbans and unmutes when applicable.
- **Custom Commands**
  - CXYZ supports a **shorthand command** system where new commands can be created by only specifying a trigger and the resulting action.
  - You can reference the sender player and all their attributes simply using the `{sender}` placeholder
  - Nest shorthand commands together to create complex menus with ease
- **Full Cosmetic Suite**
  - CXYZ features a cosmetic system that includes chat tags, chat colors, custom rank plates, gadgets, wearables, particle trails, and even kill effects.
  - Players can buy server cosmetics with custom implementations with server coins, and equip/unequip them as they choose.
  - Cosmetic purchasing can also respect rank requirements such as requiring a VIP rank to purchase a VIP tag.
  - Developers can use the CXYZ cosmetic API to implement equip actions and unequip actions, or effectively disallow equipping depending on a condition.
  - Cosmetics must be defined and can be modified in the `cosmetics.yml` file.
- **Leveling System**
  - To help with engagement long-term, CXYZ has a level and xp system built in to the NetworkPlayer model.
  - Network XP can be added to players using the `/xp add` command. This may be useful to give upon kills, wins, or playtime.
  - Players automatically level up when they receive enough XP.
  - Developers can use the CXYZ Event API (with Event, EventHandler) to implement custom actions that occur when a player levels up.
- **Coins**
  - Network coins can be added to players using the `/coins add` command. This may be useful to give upon kills, wins, or playtime.
  - Developers can use the CXYZ NetworkPlayer API (using NetworkPlayer:getCoins) to implement payment logic.
- **Simple Announcements**
  - Send scheduled announcements to all your players locally, or network-wide.
  - Reference a player and have the plugin automatically fill names by using the `{player}` placeholder.
- **Essential Utilities**
  - Dispose an item held in the hand with `/dispose`
  - Enchant an item held in the hand with `/enchant`
  - Get an item with `/i`
  - Force a player to run a command with `/sudo` 
  - Show a player text with the message parser using `/show`
  - _and so much more_

---

## How It Works
**Quick definitions:**
  - Game Server: A Minecraft server which is running the CXYZ plugin
  - Backend API: A web server which hosts the CXYZ central database and has a web API to interact with it.
  - Model: Any object type (e.g. NetworkPlayer (user), Party, Punishment, etc.) that is stored in the central database according to a table.

### Startup

Upon startup, the plugin (with the data provided in it's `config.yml`) requests data by sending a POST request (`/cache`) to the backend server. The request body contains the names of the database tables the plugin needs fulfilled.
  
Given the backend server is online and the request is valid (proper secret and ID), the backend will respond with a 200 OK code and prepare to send the data over. The backend then sends the data in a request to the game server endpoint matching `/{tableName}Shipment` (e.g. `/usersShipment` for users).

Now that the game server (more specifically the plugin) has all the data it requested, it can accept players.

### At Runtime

Game servers will need to modify data at their own discretion, but we also need this data to persist across all servers (via the backend database). To resolve this, any time a model is modified, it should finally be **synced** to the database.

Syncing an example NetworkPlayer to the database:
```java
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

// Get an example NetworkPlayer (user)
NetworkPlayer player = NetworkPlayer.getPlayerByUsername("Steve");

// The setNickname method will only modify the local cached object, not the database object.
player.setNickname("BigSteve");

// This sync operation pushes the new object to the database. Now our database object matches the lastest cached version.
player.sync(); 
```

The actual `sync()` function inside NetworkPlayer.
```java
public class NetworkPlayer {
  // ...
  public void sync() {
    this.version += 1; // Database rows are versioned so old values will never overwrite new values
    Request.postRequest(apiEndpoint + "/user/modify", gson.toJson(this)); // Post to the backend API
  }
  // ...
}
```

The backend will then send a "user delivery" to all servers listed in its config.json.
```txt
POST http://myminecraftserver.com/usersDelivery
(post data): [ {"username" : "cerrot", "coins" : 40} ]
```

---
## Installation
1. Download the plugin jar directly from GitHub in the [/target](https://github.com/javaustin/cxyz/tree/main/target) directory.
2. Place the `cxyz-0.0.jar` in your server's plugins directory, then restart your Minecraft server
3. The plugin will fail upon your first start, this is ok! Open `config.yml` and type in the backend API information (IP address, identifier, and secret). Ensure this matches the [API information](https://github.com/javaustin/cxyzAPI/blob/main/config.json).
4. Restart your server again.
5. If you see green checkmarks `(✅)` in the server console, this means data has been sent to your plugin!
---

## Configuration
[View config.yml](https://github.com/javaustin/cxyz/blob/main/src/main/resources/config.yml)  
[View messages.yml](https://github.com/javaustin/cxyz/blob/main/src/main/resources/messages.yml)  
[View cosmetics.yml](https://github.com/javaustin/cxyz/blob/main/src/main/resources/cosmetics.yml)  

---

## Wiki
_Wiki in progress. Thanks for your patience!_