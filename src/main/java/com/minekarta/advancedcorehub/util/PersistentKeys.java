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

    // Key to store the identifier of the item from items.yml
    public static final NamespacedKey ITEM_ID = new NamespacedKey(plugin, "item_id");

    // Key to store a list of actions for an item
    public static final NamespacedKey ACTIONS_KEY = new NamespacedKey(plugin, "actions");

    // Keys for specific click actions
    public static final NamespacedKey LEFT_CLICK_ACTIONS_KEY = new NamespacedKey(plugin, "left_click_actions");
    public static final NamespacedKey RIGHT_CLICK_ACTIONS_KEY = new NamespacedKey(plugin, "right_click_actions");

    // Key to store the type of movement for an item
    public static final NamespacedKey MOVEMENT_TYPE_KEY = new NamespacedKey(plugin, "movement_type");

    // Key for timed flight expiration
    public static final NamespacedKey FLY_EXPIRATION = new NamespacedKey(plugin, "fly_expiration");

    // Key to prevent item giving on join after inventory clear
    public static final NamespacedKey INVENTORY_CLEARED = new NamespacedKey(plugin, "inventory_cleared");

    // Key to identify a gadget in the gadget menu
    public static final NamespacedKey GADGET_ID = new NamespacedKey(plugin, "gadget_id");
}
