package com.minekarta.advancedcorehub.util;

/**
 * Utility class to hold all permission constants.
 */
public final class Permissions {

    private Permissions() {}

    public static final String PREFIX = "advancedcorehub.";

    // Command Permissions
    public static final String CMD_RELOAD = PREFIX + "command.reload";
    public static final String CMD_GIVE = PREFIX + "command.give";
    public static final String CMD_WORLDS = PREFIX + "command.worlds";
    public static final String CMD_SPAWN = PREFIX + "command.spawn";
    public static final String CMD_SETSPAWN = PREFIX + "command.setspawn";
    public static final String CMD_FLY = PREFIX + "command.fly";
    public static final String CMD_FLY_OTHERS = PREFIX + "command.fly.others";
    public static final String CMD_CLEARCHAT = PREFIX + "command.clearchat";
    public static final String CMD_LOCKCHAT = PREFIX + "command.lockchat";
    public static final String CMD_BOSSBAR = PREFIX + "command.bossbar";
    public static final String CMD_HELP = PREFIX + "command.help";
    public static final String CMD_VERSION = PREFIX + "command.version";

    // Item Usage Permissions
    public static final String ITEM_USE_TRIDENT = PREFIX + "item.use.trident";
    public static final String ITEM_USE_GRAPPLING_HOOK = PREFIX + "item.use.grappling_hook";
    public static final String ITEM_USE_AOTE = PREFIX + "item.use.aote";
    public static final String ITEM_USE_ENDERBOW = PREFIX + "item.use.enderbow";

    // Bypass Permissions
    public static final String BYPASS_COOLDOWN = PREFIX + "bypass.cooldown";
    public static final String BYPASS_CHAT_LOCK = PREFIX + "chat.bypasslock";

}
