# CXYZ
  
Named after my abbreviated Minecraft server IP (cerrot.xyz), this is a core plugin that provides all non-negotiable features for any serious [Bukkit](https://dev.bukkit.org/) or [Spigot](https://hub.spigotmc.org) based server. Rather than weakly stitching together features from various plugins, allow CXYZ to authoritively handle chat, ranks, parties, custom commands, moderation, and cosmetics across multiple servers.
  
This plugin is built to integrate with an external API [(see here)](https://github.com/javaustin/cxyz/edit/main/README.md#%EF%B8%8F-important-notes) because many of its systems are network-wide by nature, not tied to a single server instance. Using an API allows the plugin to remain fast, consistent, and scalable without sacrificing simplicity.
<br></br>
#### ⚠️ Important Notes
- This plugin is meant to work with a custom API and database setup. Please refer to my [cxyzAPI](https://github.com/javaustin/cxyzAPI) project.
- This plugin is IN PROGRESS as of January 2026. Please do not expect support as the plugin has not reached a finalized state.
<br></br>
---
## Design Philosophy
##### Cohesion over modularity
- Systems rely on each other internally (e.g.: chat must know ranks, parties must know players, player must know chat channel)
##### Safe defaults
- Core objects (player, ranks, channels) always exist. Even when not defined in config, the plugin creates safe defaults to prevent breakage.
##### Cachce locally, own data centrally
- All game servers cache the API and recieve copies of all relevant tables. When values are modified on game servers, they are pushed to the API and back.
##### Player-friendly, admin-friendly, and developer-friendly
- The plugin is designed for fast interactions, powerful configuration and infinite customization, predictable behavior, and native extensibility for developers
---     
## Key Features

#### Player identity
- Native support for persistent network-wide player profiles allows for more flexibility when players are offline
- A single NetworkPlayer object represents a player across all servers no matter if they are online or not
- NetworkPlayer handles an individual players UUID, username, nickname, ranks, coins, xp, level, privacy settings, cosmetics, and many more attributes  
![/whois command](https://i.imgur.com/Tln0qRu.png "Plugin /whois command")

---
    
#### Ranks
- An authoritative rank system with guaranteed defaults (no 'rank-less' players)
- A fixed rank hierarchy system where hierarchy values must be assigned (i.e.: admin=4 > mod=3 > vip=2 > default=1) 
- Ranks can have configurable names, prefixes, chat colors, chat cooldowns.

```yml
# config.yml

  default:
    prefix: '&7'
    color: '&7'
    defaultChatColor: '&f'
    chat-cooldown: 3
    hierarchy: 0

  vip:
    prefix: '&f[&aVIP&f] '
    color: '&a'
    default-chat-color: '&f'
    chat-cooldown: 1
    hierarchy: 1
  
  admin:
    prefix: '&f[&cAdmin&f] '
    color: '&c'
    default-chat-color: '&f'
    chat-cooldown: 0
    hierarchy: 2
```
  
---
  
#### Chat channels
- Multi channel chat engine with custom permissions and formatting.
- Easily switch chat channels with `/chat <channel>` 
- Core channels (public, party, and message) work out of the box.
- Custom channels can be created just by opening the config and defining a channel.
- Great for private staff channels, or even server announcements with a read-only channel
```yml
# config.yml
chat:
  core-channels:
    # Creating new public channels are not supported, try using custom private channels instead.
    all:
      prefix: '&ePUBLIC' # Only used for chat channel GUI's like /channel <set | ignore | lock>. NOT used for prefixing the chat format, you must do that manually.
      read-only: false
      console: true
      ignorable: false # Can this channel be ignored? Probably not
      webhook-url: ''
      chat-format: '{player-tag}{player-rank-prefix}{player-rank-color}{player-display-name}: {player-chat-color}{message}' # Directly applicable to player chat
      trigger-prefix: ""
      aliases: [public, pub, a]
      lockable: true
      locked: false

  custom-channels:

    staff:
      prefix: '&c[Staff] '
      read-only: false
      console: true
      ignorable: true
      webhook-url: ''
      chat-format: '{channel-prefix}{player-rank-color}{player-display-name}: &f{content}'
      trigger-prefix: '#' # Do not use the same value across channels for trigger prefixes, or you the plugin won't know what channel you want!
      aliases: [s]
      lockable: true
      locked: false

```
---
  
#### Shorthand Commands
- Easy implementation of custom commands using config.yml
- Simply define trigger commands and run actions
- Reference the sender player and all their attributes using the {sender} placeholder
- Nest shorthand commands together to create complex menus with ease

```yml
# config.yml

shorthand-commands:
  sc:
    description: "Enter the staff channel"
    trigger: sc
    actions:
      - channel staff

  permban:
    description: "Permanently ban a player from the server"
    trigger: permban {player} {reason}
    actions:
      - ban {player} permanent {reason}

  rules:
    description: "View the server rules"
    trigger: rules
    actions:
      - "show {sender-username} &aHere are the rules for you {sender}:\n&c1. No hacking\n&62. No being frowny\n&e3. Have fun!"

  hi:
    description: "Say hi!"
    trigger: hi
    actions:
      - show {sender-username} Hello!
```
  
---

#### Parties
- Fully command-based party system (no GUI dependency)
- Create, invite, join, leave, disband, promote leader, toggle public/private, and warp
- Automatic cleanup for offline players and expired invites
- Designed to be simple, fast, predictable, and power-user friendly
- Configure admin settings by enabling/disabling parties, setting party size limits, or setting expire/autokick times
![Plugin party system](https://i.imgur.com/awZj8Yt.png "Plugin party system")
---

#### Unlimited customization
- Control every plugin message using messages.yml
- Support for 1.16+ RGB colors and legacy colors in the same string
- Natively support placeholders (players, ranks, channels, etc.) in every command message
- Add hover text and click actions while keeping the text readable.
```yml
commands:
  party:
    invite-received: |
      (&f----------------------------------------------------
      {inviter-rank-prefix}{inviter-rank-color}{inviter-display-name} &r&7has invited you to their party.
      &e&lClick to accept!
      &f----------------------------------------------------)[HOVER:&eClick to join!][RUN_COMMAND:/party join {inviter-display-name}]
  # ...

  punishment:
    ban:
      mod-message: "&aSuccessfully applied ban to {player}!"
      player-message: |
        &cYou have been banned from this server for: {reason}!
    
        &r&fID: #&7{case-id}
        &r&fPlayer: {player-rank-color}{player} 
        &r&fModerator: {mod-rank-color}{mod}
        &r&fIssued on: &7{date}
        &r&fEffective Until: &7{effective-until}
        
        &4You cannot rejoin until your ban expires in {effective-until-countdown}.
        &rAppeal at: https://example.com/appeal
```
![Plugin party invite message](https://i.imgur.com/lHhs25E.png "Plugin party invite message")
![Plugin ban message](https://i.imgur.com/IrYNJf8.png "Plugin ban message")

---
## Message syntax help
CXYZ uses a custom message parser that supports both legacy and 1.16+ colors, custom text components, and tons of native placeholders.

### Syntax examples
---

#### Placeholder example
Almost all plugin messages support placeholders for messaging, allowing you to fully customize messaging. This message uses the {sender} placeholder to represent the sending player.
```yml
example-message: "&aAn example legacy color message that is green. {sender-rank-color}{sender}&a will see their name when it is sent."
```
![Plugin example message](https://i.imgur.com/oZX5cwf.png "Plugin example message")

---
#### RGB code example
The plugin supports RGB colors in the default Bukkit message format. An RGB code is prepended by the code `&x`, and each bit is separated by `&` (e.g., #FF0000 -> &x&F&F&0&0&0&0). 
```yml
example-message-2: "&x&8&F&F&B&9&6An example message with the custom RGB code: #8FFB96"
```
![Plugin example message 2](https://i.imgur.com/lXMDzZj.png "Plugin example message 2")

---

#### Clickable component example
Brackets followed by parenthesis are assumed to be text component blocks. A text component block follows the format `[text](ACTION:actionText)`.   
If you wish to use brackets or parenthesis outside of this, you should double them ("[" -> "[[") so the parser ignores them.   
Valid actions are `RUN_COMMAND`, `SUGGEST_COMMAND`, `COPY_TO_CLIPBOARD`, `OPEN_URL`, `HOVER`.   
```yml
example-message-3: "[&x&3&E&4&C&F&BAn RGB clickable example message](HOVER:&eClick me)"
```
![Plugin example message 3](https://i.imgur.com/lF3HmFF.png "Plugin example message 3")

---
#### Gradient example
Gradients are supported by the legacy parser by appending each color code before each character. [Here](https://minecraft.menu/minecraft-rgb-generator) is a handy tool to generate gradients easily.  
```yml
example-message-4: "&x&F&B&C&4&1&Eg&x&F&B&B&E&2&2r&x&F&B&B&9&2&6a&x&F&B&B&3&2&9d&x&F&B&A&D&2&Di&x&F&B&A&7&3&1e&x&F&B&A&2&3&5n&x&F&B&9&C&3&9t&x&F&B&9&6&3&Cs &x&F&B&9&0&4&0a&x&F&B&8&B&4&4r&x&F&B&8&5&4&8e &x&F&B&7&F&4&Cs&x&F&B&7&9&4&Fu&x&F&B&7&4&5&3p&x&F&B&6&E&5&7p&x&F&B&6&8&5&Bo&x&F&B&6&2&5&Er&x&F&B&5&D&6&2t&x&F&B&5&7&6&6e&x&F&B&5&1&6&Ad &x&F&B&4&B&6&Et&x&F&B&4&6&7&1o&x&F&B&4&0&7&5o&x&F&B&3&A&7&9!"
```
![Plugin example message 4](https://i.imgur.com/lFeFJqi.png "Plugin example message 4")

---
