package com.carrotguy69.cxyz.messages;

public enum MessageKey {

    // =========================
    // Errors - Command
    // =========================
    COMMAND_RESTRICTED("errors.command.restricted"),
    COMMAND_NO_ACCESS("errors.command.no-access"),
    COMMAND_WRONG_SERVER("errors.command.wrong-server"),
    COMMAND_PLAYER_ONLY("errors.command.player-only"),

    // =========================
    // Errors - Player
    // =========================
    PLAYER_IS_SELF("errors.player.is-self"),
    PLAYER_NOT_FOUND("errors.player.not-found"),
    PLAYER_OUTRANKS_SENDER("errors.player.outranks-sender"),
    PLAYER_IS_OFFLINE("errors.player.is-offline"),

    // =========================
    // Errors - Args
    // =========================
    MISSING_GENERAL("errors.args.missing.general"),
    MISSING_CONTENT("errors.args.missing.content"),

    INVALID_BOOLEAN("errors.args.invalid.boolean"),
    INVALID_CHANNEL("errors.args.invalid.channel"),
    INVALID_COLOR("errors.args.invalid.color"),
    INVALID_COSMETIC("errors.args.invalid.cosmetic"),
    INVALID_DEBUG("errors.args.invalid.debug"),
    INVALID_DURATION("errors.args.invalid.duration"),
    INVALID_ITEM("errors.args.invalid.item"),
    INVALID_NUMBER("errors.args.invalid.number"),
    INVALID_PAGE("errors.args.invalid.page"),
    INVALID_PLAYER("errors.args.invalid.player"),
    INVALID_PUNISHMENT("errors.args.invalid.punishment"),
    INVALID_RANK("errors.args.invalid.rank"),
    INVALID_FRIEND_PRIVACY_VALUE("errors.args.invalid.friend-privacy-value"),
    INVALID_MESSAGE_PRIVACY_VALUE("errors.args.invalid.message-privacy-value"),
    INVALID_PARTY_PRIVACY_VALUE("errors.args.invalid.party-privacy-value"),
    INVALID_TIMEZONE("errors.args.invalid.timezone"),
    INVALID_NICKNAME_LENGTH("errors.args.invalid.nickname-length"),
    INVALID_NICKNAME_CHARACTERS("errors.args.invalid.nickname-characters"),
    INVALID_NICKNAME_TAKEN("errors.args.invalid.nickname-taken"),



    // =========================
    // Errors - Other
    // =========================
    API_ERROR("errors.other.api-error"),


    // =========================
    // Chat
    // =========================
    CHAT_COOLDOWN("chat.cooldown-message"),


    // =========================
    // Commands - Buy
    // =========================
    BUY_SUCCESS("commands.buy.success"),
    BUY_ERROR_INSUFFICIENT_RANK("commands.buy.error.insufficient-rank"),
    BUY_ERROR_INSUFFICIENT_LEVEL("commands.buy.error.insufficient-level"),
    BUY_ERROR_INSUFFICIENT_COINS("commands.buy.error.insufficient-coins"),
    BUY_ERROR_DUPLICATE_ITEM("commands.buy.error.duplicate-item"),
    BUY_ERROR_DISABLED_ITEM("commands.buy.error.disabled-item"),

    // =========================
    // Commands - Chat Channel
    // =========================
    CHAT_CHANNEL_AVAILABLE_SUBCOMMANDS("commands.chat-channel.available-subcommands"),
    CHAT_CHANNEL_SET("commands.chat-channel.set"),
    CHAT_CHANNEL_VIEW("commands.chat-channel.view"),
    CHAT_CHANNEL_IS_LOCKED("commands.chat-channel.is-locked"),
    CHAT_CHANNEL_READ_ONLY("commands.chat-channel.read-only"),
    CHAT_CHANNEL_NO_ACCESS("commands.chat-channel.no-access"),
    CHAT_CHANNEL_IGNORED("commands.chat-channel.muted"),
    CHAT_CHANNEL_UNIGNORED("commands.chat-channel.unmuted"),
    CHAT_CHANNEL_NOT_IGNORED("commands.chat-channel.not-muted"),
    CHAT_CHANNEL_NOT_IGNORABLE("commands.chat-channel.error.not-mutable"),
    CHAT_CHANNEL_ALREADY_IGNORED("commands.chat-channel.already-muted"),
    CHAT_CHANNEL_IS_MUTED("commands.chat-channel.channel-muted"),
    CHAT_CHANNEL_LOCK_FORBIDDEN("commands.chat-channel.lock-forbidden"),
    CHAT_CHANNEL_LOCKED("commands.chat-channel.locked"),
    CHAT_CHANNEL_UNLOCKED("commands.chat-channel.unlocked"),
    CHAT_CHANNEL_ALREADY_LOCKED("commands.chat-channel.already-locked"),
    CHAT_CHANNEL_ALREADY_UNLOCKED("commands.chat-channel.already-unlocked"),
    CHAT_CHANNEL_LIST_IGNORED("commands.chat-channel.ignore-list.message"),
    CHAT_CHANNEL_LIST_IGNORED_NONE("commands.chat-channel.ignore-list.blank"),
    CHAT_CHANNEL_LIST_CHANNEL_FORMAT("commands.chat-channel.ignore-list.entry-format"),
    CHAT_CHANNEL_LIST_CHANNEL_SEPARATOR("commands.chat-channel.ignore-list.separator"),
    CHAT_CHANNEL_LIST_CHANNEL_MAX_ENTRIES("commands.chat-channel.ignore-list.max-entries-per-page"),

    // =========================
    // Commands - Chat Color
    // =========================
    CHAT_COLOR_AVAILABLE_SUBCOMMANDS("commands.chat-color.available-subcommands"),
    CHAT_COLOR_SET("commands.chat-color.set"),
    CHAT_COLOR_RESET("commands.chat-color.reset"),
    CHAT_COLOR_VIEW("commands.chat-color.view"),
    CHAT_COLOR_DUPLICATE_STATE("commands.chat-color.duplicate-state"),

    // =========================
    // Commands - Coins
    // =========================
    COINS_AVAILABLE_SUBCOMMANDS("commands.coins.available-subcommands"),
    COINS_ADDED("commands.coins.added"),
    COINS_REMOVED("commands.coins.removed"),
    COINS_SET("commands.coins.set"),
    COINS_VIEW("commands.coins.view"),


    // =========================
    // Commands - Debug
    // =========================

    DEBUG_SET("commands.debug.set"),
    DEBUG_UNSET("commands.debug.unset"),
    DEBUG_VIEW("commands.debug.view"),

    // =========================
    // Commands - (un)Equip
    // =========================
    EQUIP_COSMETIC_ALREADY_EQUIPPED("commands.equip.error.already-equipped"),
    EQUIP_COSMETIC_NOT_OWNED("commands.equip.error.not-owned"),
    EQUIP_COSMETIC_INSUFFICIENT_RANK("commands.equip.error.insufficient-rank"),
    EQUIP_COSMETIC_SUCCESS("commands.equip.success"),

    UNEQUIP_COSMETIC_NOT_EQUIPPED("commands.unequip.error.not-equipped"),
    UNEQUIP_COSMETIC_NOT_OWNED("commands.unequip.error.not-owned"),
    UNEQUIP_COSMETIC_SUCCESS("commands.unequip.success"),

    // =========================
    // Commands - Friend
    // =========================
    FRIEND_ERROR_ALREADY_FRIENDS("commands.friend.error.already-friends"),
    FRIEND_ERROR_NOT_FRIENDS("commands.friend.error.not-friends"),
    FRIEND_ERROR_DUPLICATE_REQUEST("commands.friend.error.duplicate-request"),
    FRIEND_ERROR_NO_REQUEST("commands.friend.error.no-request"),
    FRIEND_ERROR_REQUEST_EXPIRED("commands.friend.error.request-expired"),

    FRIEND_AVAILABLE_SUBCOMMANDS("commands.friend.available-subcommands"),
    FRIEND_REQUEST_SENT("commands.friend.request-sent"),
    FRIEND_REQUEST_RECEIVED("commands.friend.request-received"),
    FRIEND_REQUEST_ACCEPTED("commands.friend.request-accepted"),
    FRIEND_REQUEST_ACCEPT_RECEIVED("commands.friend.request-accepted-received"),
    FRIEND_REQUEST_DENIED("commands.friend.request-denied"),

    FRIEND_REMOVED("commands.friend.removed"),
    FRIEND_REMOVED_RECEIVED("commands.friend.removed-received"),

    FRIEND_LIST("commands.friend.friend-list.message"),
    FRIEND_LIST_NONE("commands.friend.friend-list.blank"),
    FRIEND_LIST_PLAYER_FORMAT("commands.friend.friend-list.entry-format"),
    FRIEND_LIST_PLAYER_SEPARATOR("commands.friend.friend-list.separator"),
    FRIEND_LIST_MAX_ENTRIES("commands.friend.friend-list.max-entries-per-page"),

    // =========================
    // Commands - Info
    // =========================
    INFO("commands.info"),


    // =========================
    // Commands - Ignore
    // =========================

    IGNORE_PLAYER("commands.privacy.ignore-player"),
    UNIGNORE_PLAYER("commands.privacy.unignore-player"),
    ALREADY_IGNORED_PLAYER("commands.privacy.already-ignored-player"),
    NOT_IGNORED_PLAYER("commands.privacy.not-ignored-player"),

    IGNORE_LIST("commands.privacy.ignore-list.message"),
    IGNORE_LIST_NONE("commands.privacy.ignore-list.blank"),
    IGNORE_LIST_FORMAT("commands.privacy.ignore-list.entry-format"),
    IGNORE_LIST_SEPARATOR("commands.privacy.ignore-list.separator"),
    IGNORE_LIST_MAX_ENTRIES("commands.privacy.ignore-list.max-entries-per-page"),


    // =========================
    // Commands - Message (DMs)
    // =========================
    MESSAGE_FAIL("commands.message.fail"),
    MESSAGE_OPENED("commands.message.opened"),
    MESSAGE_RECEIVED("commands.message.received"),
    MESSAGE_REPLY_FAIL("commands.message.reply-fail"),
    MESSAGE_SENT("commands.message.sent"),
    MESSAGE_CHANNEL_CHANGED("commands.message.channel-changed"),

    // =========================
    // Commands - MOTD
    // =========================
    MOTD_SET("commands.motd.set"),
    MOTD_VIEW("commands.motd.view"),

    // =========================
    // Commands - Nickname
    // =========================
    NICKNAME_SET("commands.nickname.set"),
    NICKNAME_RESET("commands.nickname.reset"),
    NICKNAME_VIEW("commands.nickname.view"),

    // =========================
    // Commands - Level
    // =========================
    LEVEL_AVAILABLE_SUBCOMMANDS("commands.level.available-subcommands"),
    LEVEL_ADDED("commands.level.added"),
    LEVEL_REMOVED("commands.level.removed"),
    LEVEL_SET("commands.level.set"),
    LEVEL_VIEW("commands.level.view"),

    // =========================
    // Commands - Parse
    // =========================
    PARSE_ERROR("commands.parse.error"),

    // =========================
    // Commands - Party
    // =========================
    PARTY_AVAILABLE_SUBCOMMANDS("commands.party.available-subcommands"),
    PARTY_CREATED("commands.party.created"),
    PARTY_DISBAND("commands.party.disband"),
    PARTY_DISABLED("commands.party.disabled"),
    PARTY_DISBAND_INACTIVE("commands.party.disband-inactive"),
    PARTY_DISBAND_SOLO("commands.party.disband-solo"),
    PARTY_INVITE_EXPIRED("commands.party.invite-expired"),
    PARTY_INVITE_FAIL("commands.party.invite-fail"),
    PARTY_INVITE_SENT("commands.party.invite-sent"),
    PARTY_INVITE_RECEIVED("commands.party.invite-received"),
    PARTY_PLAYER_DISCONNECT("commands.party.player-disconnect"),
    PARTY_PLAYER_RECONNECT("commands.party.player-reconnect"),
    PARTY_FULL("commands.party.party-full"),
    PARTY_FULL_ANNOUNCEMENT("commands.party.party-full-announcement"),
    PARTY_LIST("commands.party.party-list.message"),
    PARTY_LIST_PLAYER_FORMAT("commands.party.party-list.entry-format"),
    PARTY_LIST_PLAYER_SEPARATOR("commands.party.party-list.separator"),
    PARTY_LIST_MAX_ENTRIES("commands.party.party-list.max-entries-per-page"),
    PARTY_JOIN("commands.party.join"),
    PARTY_JOIN_ANNOUNCEMENT("commands.party.join-announcement"),
    PARTY_LEFT("commands.party.left"),
    PARTY_LEFT_ANNOUNCEMENT("commands.party.left-announcement"),
    PARTY_SET_PUBLIC_TRUE("commands.party.set-public-true"),
    PARTY_SET_PUBLIC_FALSE("commands.party.set-public-false"),
    PARTY_REMOVE("commands.party.remove"),
    PARTY_REMOVE_ANNOUNCEMENT("commands.party.remove-announcement"),
    PARTY_REMOVE_INACTIVE_ANNOUNCEMENT("commands.party.remove-inactive-announcement"),
    PARTY_TRANSFER("commands.party.transfer"),
    PARTY_WARP_ANNOUNCEMENT("commands.party.warp-announcement"),
    PARTY_CHAT_MESSAGE("commands.party.chat-message"),

    PARTY_ERROR_IN_PARTY_JOIN("commands.party.error.in-party-join"),
    PARTY_ERROR_IN_PARTY_CREATE("commands.party.error.in-party-create"),
    PARTY_ERROR_DUPLICATE_INVITE("commands.party.error.duplicate-invite"),
    PARTY_ERROR_NOT_IN_PARTY("commands.party.error.not-in-party"),
    PARTY_ERROR_IN_PARTY("commands.party.error.in-party"),
    PARTY_ERROR_PLAYER_NOT_IN_PARTY("commands.party.error.player-not-in-party"),
    PARTY_ERROR_PARTY_NOT_EXIST("commands.party.error.party-not-exist"),
    PARTY_ERROR_INVITE_NOT_FOUND("commands.party.error.invite-not-found"),
    PARTY_ERROR_LEADER_ONLY("commands.party.error.leader-only"),
    PARTY_ERROR_MUST_DISBAND("commands.party.error.must-disband"),
    PARTY_ERROR_CHANNEL_CHANGED("commands.party.error.channel-changed"),
    PARTY_ERROR_WARP_FAILED("commands.party.error.warp-failed"),
    PARTY_ERROR_ALREADY_PRIVATE("commands.party.error.already-private"),
    PARTY_ERROR_ALREADY_PUBLIC("commands.party.error.already-private"),

    // =========================
    // Commands - Ping
    // =========================
    PING("commands.ping"),

    // =========================
    // Commands - Privacy
    // =========================

    FRIEND_PRIVACY_SET("commands.privacy.friend-privacy-set"),
    FRIEND_PRIVACY_VIEW("commands.privacy.friend-privacy-view"),
    FRIEND_PRIVACY_ALREADY_SET("commands.privacy.friend-privacy-already-set"),

    MESSAGE_PRIVACY_SET("commands.privacy.message-privacy-set"),
    MESSAGE_PRIVACY_VIEW("commands.privacy.message-privacy-view"),
    MESSAGE_PRIVACY_ALREADY_SET("commands.privacy.message-privacy-already-set"),

    PARTY_PRIVACY_SET("commands.privacy.party-privacy-set"),
    PARTY_PRIVACY_VIEW("commands.privacy.party-privacy-view"),
    PARTY_PRIVACY_ALREADY_SET("commands.privacy.party-privacy-already-set"),


    // =========================
    // Commands - Punishment
    // =========================
    PUNISHMENT_ERROR_NOT_BANNED("commands.punishment.error.not-banned"),
    PUNISHMENT_ERROR_NOT_MUTED("commands.punishment.error.not-muted"),

    PUNISHMENT_BAN_MOD_MESSAGE("commands.punishment.ban.mod-message"),
    PUNISHMENT_BAN_PLAYER_MESSAGE("commands.punishment.ban.player-message"),
    PUNISHMENT_BAN_PLAYER_MESSAGE_PERMANENT("commands.punishment.ban.player-message-permanent"),
    PUNISHMENT_BAN_LOG_MESSAGE("commands.punishment.ban.log-message"),
    PUNISHMENT_BAN_ANNOUNCEMENT("commands.punishment.ban.announcement"),

    PUNISHMENT_UNBAN_MOD_MESSAGE("commands.punishment.unban.mod-message"),
    PUNISHMENT_UNBAN_LOG_MESSAGE("commands.punishment.unban.log-message"),
    PUNISHMENT_UNBAN_ANNOUNCEMENT("commands.punishment.unban.announcement"),

    PUNISHMENT_UNMUTE_MOD_MESSAGE("commands.punishment.unmute.mod-message"),
    PUNISHMENT_UNMUTE_LOG_MESSAGE("commands.punishment.unmute.log-message"),
    PUNISHMENT_UNMUTE_ANNOUNCEMENT("commands.punishment.unmute.announcement"),


    PUNISHMENT_KICK_MOD_MESSAGE("commands.punishment.kick.mod-message"),
    PUNISHMENT_KICK_PLAYER_MESSAGE("commands.punishment.kick.player-message"),
    PUNISHMENT_KICK_LOG_MESSAGE("commands.punishment.kick.log-message"),
    PUNISHMENT_KICK_ANNOUNCEMENT("commands.punishment.kick.announcement"),
    PUNISHMENT_MUTE_MOD_MESSAGE("commands.punishment.mute.mod-message"),
    PUNISHMENT_MUTE_INITIAL_PLAYER_MESSAGE("commands.punishment.mute.initial-player-message"),
    PUNISHMENT_MUTE_PLAYER_MESSAGE("commands.punishment.mute.player-message"),
    PUNISHMENT_MUTE_LOG_MESSAGE("commands.punishment.mute.log-message"),
    PUNISHMENT_MUTE_ANNOUNCEMENT("commands.punishment.mute.announcement"),
    PUNISHMENT_WARN_MOD_MESSAGE("commands.punishment.warn.mod-message"),
    PUNISHMENT_WARN_PLAYER_MESSAGE("commands.punishment.warn.player-message"),
    PUNISHMENT_WARN_LOG_MESSAGE("commands.punishment.warn.log-message"),
    PUNISHMENT_WARN_ANNOUNCEMENT("commands.punishment.warn.announcement"),

    PUNISHMENT_AVAILABLE_SUBCOMMANDS("commands.punishment.available-subcommands"),
    PUNISHMENT_EDIT("commands.punishment.edit"),
    PUNISHMENT_DELETE("commands.punishment.delete"),
    PUNISHMENT_CLEAR("commands.punishment.clear"),
    PUNISHMENT_INFO("commands.punishment.info"),

    PUNISHMENT_HISTORY_LIST("commands.punishment.history-list.message"),
    PUNISHMENT_HISTORY_LIST_NONE("commands.punishment.history-list.blank"),
    PUNISHMENT_HISTORY_FORMAT("commands.punishment.history-list.entry-format"),
    PUNISHMENT_HISTORY_SEPARATOR("commands.punishment.history-list.separator"),
    PUNISHMENT_HISTORY_MAX_ENTRIES("commands.punishment.history-list.separator"),

    // =========================
    // Commands - Port
    // =========================
    PORT_SET("commands.port.set"),
    PORT_VIEW("commands.port.view"),

    // =========================
    // Commands - Rank
    // =========================
    RANK_AVAILABLE_SUBCOMMANDS("commands.rank.available-subcommands"),
    RANK_ADD("commands.rank.add"),
    RANK_REMOVE("commands.rank.remove"),

    RANK_LIST_PLAYER("commands.rank.rank-list.message"),
    RANK_LIST_PLAYER_NONE("commands.rank.rank-list.blank"),
    RANK_LIST_PLAYER_FORMAT("commands.rank.rank-list.entry-format"),
    RANK_LIST_PLAYER_SEPARATOR("commands.rank.rank-list.separator"),
    RANK_LIST_PLAYER_MAX_ENTRIES("commands.rank.rank-list.max-entries-per-page"),

    RANK_ERROR_HAS_RANK("commands.rank.error.has-rank"),
    RANK_ERROR_MISSING_RANK("commands.rank.error.missing-rank"),

    // =========================
    // Commands - Reload
    // =========================
    RELOAD_SUCCESS("commands.reload.success"),
    RELOAD_FAIL("commands.reload.fail"),



    TIMEZONE_SET("commands.timezone.set"),
    TIMEZONE_ERROR_DUPLICATE_STATE("commands.timezone.error.duplicate-state"),
    TIMEZONE_VIEW("commands.timezone.view"),

    // =========================
    // Commands - XP
    // =========================
    XP_AVAILABLE_SUBCOMMANDS("commands.xp.available-subcommands"),
    XP_ADDED("commands.xp.added"),
    XP_REMOVED("commands.xp.removed"),
    XP_SET("commands.xp.set"),
    XP_VIEW("commands.xp.view");

    private final String path;

    MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
