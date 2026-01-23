# CXYZ
<p>Named after my abbreviated server IP (cerrot.xyz), this is a core plugin that provides all non-negotiable features for any serious [Bukkit](https://dev.bukkit.org/) or [Spigot](https://hub.spigotmc.org) based server. Rather than weakly stitching together features from various plugins, allow CXYZ to authoritively handle chat, ranks, parties, custom commands, moderation, and cosmetics across multiple servers.</p>
  
---
  
### Design Philosophy
<br />
Everything works together - chat knows ranks, commands know chat, parties know players. No ambiguity  
Commands first- no feature is locked down with a GUI  
Player-friendly, Admin-friendly, Developer-friendly: fast interactions, powerful configuration, predictable behavior, native extensibility  
<br />
---     
<br />
# Key Features
<br />
#### Player identity
- Native support for persistent network-wide player profiles allows for more flexibility when players are offline
- A single NetworkPlayer object represents a player across all servers no matter if they are online or not
- NetworkPlayer handles an individual players UUID, username, nickname, ranks, coins, xp, level, privacy settings, cosmetics, and much more  
![/whois command](https://i.imgur.com/Tln0qRu.png "Plugin /whois command")
<br />
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

---

#### Unlimited customization
- Control every plugin message using messages.yml
- Support for 1.16+ RGB colors and legacy colors in the same string
- Natively support placeholders (players, ranks, channels, etc.) in every command message
- Add hover text and click actions while keeping the config line readable.
```yml
commands:
  party:
    invite-sent: "&9Party> {player-rank-prefix}{player-rank-color}{player-display-name} &r&7has been invited to the party. They have 60 seconds to accept."
    invite-received: |
      (&f----------------------------------------------------
      {inviter-rank-prefix}{inviter-rank-color}{inviter-display-name} &r&7has invited you to their party.
      &e&lClick to accept!
      &f----------------------------------------------------)[HOVER:&7Click to join {inviter-rank-color}{inviter-display-name}'s &r&7party!][RUN_COMMAND:/party join {inviter-display-name}]
  ...

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
