package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ItemsManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, ItemStack> customItems = new HashMap<>();

    public ItemsManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void loadItems() {
        customItems.clear();
        ConfigurationSection itemsSection = plugin.getFileManager().getConfig("items.yml").getConfigurationSection("items");
        if (itemsSection == null) {
            plugin.getLogger().warning("No 'items' section found in items.yml. No custom items will be loaded.");
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
            if (itemConfig == null) continue;

            try {
                Material material = Material.valueOf(itemConfig.getString("material", "STONE").toUpperCase());
                ItemBuilder builder = new ItemBuilder(material);

                builder.setDisplayName(itemConfig.getString("displayname", ""));
                if (itemConfig.contains("lore")) {
                    builder.setLore(itemConfig.getStringList("lore"));
                }

                // Add a persistent key to identify this as a custom item from our plugin
                builder.addPdcValue(PersistentKeys.ITEM_ID, PersistentDataType.STRING, key);

                // Add specific persistent key for movement items, etc.
                if (itemConfig.contains("persistent_key")) {
                    String persistentKeyStr = itemConfig.getString("persistent_key");
                    // This is a simple implementation. A real one might use a map or reflection.
                    if (persistentKeyStr.equalsIgnoreCase("trident")) {
                        builder.addPdcValue(PersistentKeys.TRIDENT_KEY, PersistentDataType.BYTE, (byte)1);
                    } else if (persistentKeyStr.equalsIgnoreCase("grappling_hook")) {
                        builder.addPdcValue(PersistentKeys.GRAPPLING_HOOK_KEY, PersistentDataType.BYTE, (byte)1);
                    }
                    // Add other keys here
                }

                customItems.put(key, builder.build());
                plugin.getLogger().info("Loaded item: " + key);

            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load item with key '" + key + "' from items.yml", e);
            }
        }
    }

    public ItemStack getItem(String key) {
        if (customItems.get(key) == null) return null;
        return customItems.get(key).clone();
    }

    public void giveItem(Player player, String key, int amount, int slot) {
        ItemStack item = getItem(key);
        if (item == null) {
            plugin.getLocaleManager().sendMessage(player, "item-not-found", key);
            return;
        }
        item.setAmount(amount);

        if (slot >= 0 && slot < player.getInventory().getSize()) {
            player.getInventory().setItem(slot, item);
        } else {
            player.getInventory().addItem(item);
        }
    }
}
