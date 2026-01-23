# CXYZ
Named after my abbreviated server IP (cerrot.xyz), this is a core plugin that provides all non-negotiable features for any serious [Bukkit](https://dev.bukkit.org/) or [Spigot](https://hub.spigotmc.org) based server. Rather than weakly stitching together features from various plugins, allow CXYZ to authoritively handle chat, ranks, parties, custom commands, moderation, and cosmetics across multiple servers.


---
  
## Key Features

#### Player Identity
- Native support for persistent network-wide player profiles allows for more flexibility when players are offline
- A single NetworkPlayer object represents a player across all servers no matter if they are online or not
- NetworkPlayer handles an individual players UUID, username, nickname, ranks, coins, xp, level, privacy settings, cosmetics, and much more
![/whois command](https://i.imgur.com/Tln0qRu.png "Plugin /whois command")
  
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

  
#### Chat Channels
- Multi channel chat engine with custom permissions and formatting.
- Easily switch chat channels with `/chat <channel>` 
- Core channels (public, party, and message) work out of the box.
- Custom channels can be created just by opening the config and defining a channel.
- Great for private staff channels, or even server announcements with a read-only channel

#### Shorthand Commands
- Easy implementation of custom commands using config.yml
- Simply define trigger commands and run actions.
- Reference the sender player and all their attributes using the {sender} placeholder.

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
