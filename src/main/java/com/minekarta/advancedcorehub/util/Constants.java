package com.minekarta.advancedcorehub.util;

/**
 * A utility class to hold constant values used throughout the plugin.
 * This helps to avoid "magic strings" and "magic numbers", improving maintainability.
 */
public final class Constants {

    private Constants() {
        // Private constructor to prevent instantiation
    }

    // --- Permissions ---
    public static final String PERM_ADMIN = "advancedcorehub.admin";
    public static final String PERM_RELOAD = "advancedcorehub.reload";
    public static final String PERM_DOUBLE_JUMP = "advancedcorehub.doublejump";
    public static final String PERM_FLY = "advancedcorehub.fly";


    // --- Configuration Paths ---
    public static final String CONFIG_FILE = "config.yml";
    public static final String ITEMS_FILE = "items.yml";
    public static final String COSMETICS_FILE = "cosmetics.yml";
    public static final String MENUS_FOLDER = "menus";
    public static final String LANG_FOLDER = "languages";


    // --- Plugin Messages & Prefixes ---
    public static final String PLUGIN_PREFIX = "<#1E90FF>&lA&f&lC&b&lH &8» "; // Example prefix


    // --- Database ---
    public static final String DATABASE_FILE_NAME = "playerdata.db";
    public static final String PLAYER_DATA_TABLE = "player_data";


    // --- Cooldown Keys ---
    // (Example, will be populated later)
    public static final String COOLDOWN_ENDER_PEARL = "ender_pearl";

}