package com.minekarta.advancedcorehub.util;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.NamespacedKey;

/**
 * Utility class for all NamespacedKey constants used in PersistentDataContainers.
 */
public final class PersistentKeys {

    private PersistentKeys() {}

    private static final AdvancedCoreHub plugin = AdvancedCoreHub.getInstance();

    // Key to identify any custom item from this plugin
    public static final NamespacedKey CUSTOM_ITEM_TAG = new NamespacedKey(plugin, "custom_item");

    // Keys for specific movement items
    public static final NamespacedKey TRIDENT_KEY = new NamespacedKey(plugin, "trident");
    public static final NamespacedKey GRAPPLING_HOOK_KEY = new NamespacedKey(plugin, "grappling_hook");
    public static final NamespacedKey AOTE_KEY = new NamespacedKey(plugin, "aote");
    public static final NamespacedKey ENDERBOW_KEY = new NamespacedKey(plugin, "enderbow");

    // Key to store the identifier of the item from items.yml
    public static final NamespacedKey ITEM_ID = new NamespacedKey(plugin, "item_id");

    // Key for timed flight expiration
    public static final NamespacedKey FLY_EXPIRATION = new NamespacedKey(plugin, "fly_expiration");
}
