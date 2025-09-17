package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class ItemsManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, ItemStack> customItems = new HashMap<>();
    private final Set<String> protectedItemIds = new HashSet<>();

    public ItemsManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void loadItems() {
        customItems.clear();
        protectedItemIds.clear();
        ConfigurationSection itemsSection = plugin.getFileManager().getConfig("items.yml").getConfigurationSection("items");
        if (itemsSection == null) {
            plugin.getLogger().warning("No 'items' section found in items.yml. No custom items will be loaded.");
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
            if (itemConfig == null) continue;

            try {
                String materialString = itemConfig.getString("material", "STONE");
                ItemBuilder builder = new ItemBuilder(materialString);

                Component displayName = plugin.getLocaleManager().getComponentFromString(itemConfig.getString("displayname", ""), null);
                builder.setDisplayName(displayName);

                if (itemConfig.contains("lore")) {
                    List<Component> lore = itemConfig.getStringList("lore").stream()
                            .map(line -> plugin.getLocaleManager().getComponentFromString(line, null))
                            .collect(Collectors.toList());
                    builder.setLore(lore);
                }

                // Add custom model data if it exists
                if (itemConfig.isInt("custom-model-data")) {
                    builder.setCustomModelData(itemConfig.getInt("custom-model-data"));
                }

                // Add enchantments if they exist
                if (itemConfig.contains("enchantments")) {
                    builder.addEnchantments(itemConfig.getStringList("enchantments"));
                }

                // Add a persistent key to identify this as a custom item from our plugin
                builder.addPdcValue(PersistentKeys.ITEM_ID, PersistentDataType.STRING, key);

                // Handle actions
                handleActions(itemConfig, builder);

                // Store movement type if it exists
                if (itemConfig.contains("movement_type")) {
                    String movementType = itemConfig.getString("movement_type");
                    if (movementType != null && !movementType.isEmpty()) {
                        builder.addPdcValue(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING, movementType);
                    }
                }

                // Check if the item is protected
                if (itemConfig.getBoolean("protected", false)) {
                    protectedItemIds.add(key);
                }

                customItems.put(key, builder.build());
                plugin.getLogger().info("Loaded item: " + key);

            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load item with key '" + key + "' from items.yml", e);
            }
        }
    }

    public boolean isProtected(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        String itemId = meta.getPersistentDataContainer().get(PersistentKeys.ITEM_ID, PersistentDataType.STRING);
        return itemId != null && protectedItemIds.contains(itemId);
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

    public java.util.Set<String> getItemKeys() {
        return customItems.keySet();
    }

    private void handleActions(ConfigurationSection itemConfig, ItemBuilder builder) {
        // New system: specific actions for left and right clicks
        if (itemConfig.contains("left-click-actions")) {
            List<String> actions = itemConfig.getStringList("left-click-actions");
            if (!actions.isEmpty()) {
                builder.addPdcValue(PersistentKeys.LEFT_CLICK_ACTIONS_KEY, PersistentDataType.STRING, String.join("\n", actions));
            }
        }

        if (itemConfig.contains("right-click-actions")) {
            List<String> actions = itemConfig.getStringList("right-click-actions");
            if (!actions.isEmpty()) {
                builder.addPdcValue(PersistentKeys.RIGHT_CLICK_ACTIONS_KEY, PersistentDataType.STRING, String.join("\n", actions));
            }
        }

        // Backward compatibility: handle the old 'actions' key
        // We assume old actions were for right-click, as that's the most common use for hub items.
        if (itemConfig.contains("actions")) {
            List<String> actions = itemConfig.getStringList("actions");
            if (!actions.isEmpty() && !itemConfig.contains("right-click-actions")) {
                plugin.getLogger().warning("Item '" + itemConfig.getName() + "' is using the deprecated 'actions' key. Please update to 'right-click-actions' or 'left-click-actions'.");
                builder.addPdcValue(PersistentKeys.RIGHT_CLICK_ACTIONS_KEY, PersistentDataType.STRING, String.join("\n", actions));
            }
        }
    }
}
