package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
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
    private final Map<String, com.minekarta.advancedcorehub.config.MenuItemConfig> itemConfigs = new HashMap<>();
    private final Set<String> protectedItemIds = new HashSet<>();

    public ItemsManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void loadItems() {
        itemConfigs.clear();
        protectedItemIds.clear();
        ConfigurationSection itemsSection = plugin.getFileManager().getConfig("items.yml").getConfigurationSection("items");
        if (itemsSection == null) {
            plugin.getLogger().warning("No 'items' section found in items.yml. No custom items will be loaded.");
            return;
        }

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemConfigSection = itemsSection.getConfigurationSection(key);
            if (itemConfigSection == null) continue;

            try {
                com.minekarta.advancedcorehub.config.MenuItemConfig menuItemConfig = new com.minekarta.advancedcorehub.config.MenuItemConfig(key, itemConfigSection);
                itemConfigs.put(key, menuItemConfig);

                if (itemConfigSection.getBoolean("protected", false)) {
                    protectedItemIds.add(key);
                }
                plugin.getLogger().info("Loaded item configuration: " + key);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load item configuration with key '" + key + "' from items.yml", e);
            }
        }
    }

    public ItemStack getItem(String key, Player player) {
        com.minekarta.advancedcorehub.config.MenuItemConfig itemConfig = itemConfigs.get(key);
        if (itemConfig == null) {
            return null;
        }

        ItemBuilder builder = new ItemBuilder(itemConfig.material);

        // Set display name and lore with player-specific placeholders
        Component displayName = plugin.getLocaleManager().getComponentFromString(itemConfig.displayName, player);
        builder.setDisplayName(displayName);

        List<Component> lore = itemConfig.lore.stream()
                .map(line -> plugin.getLocaleManager().getComponentFromString(line, player))
                .collect(Collectors.toList());
        builder.setLore(lore);

        // Add custom model data, enchantments, etc.
        if (itemConfig.customModelData > 0) {
            builder.setCustomModelData(itemConfig.customModelData);
        }
        if (itemConfig.headTexture != null && !itemConfig.headTexture.isEmpty()) {
            builder.setHeadTexture(itemConfig.headTexture);
        } else if (itemConfig.skullOwner != null && !itemConfig.skullOwner.isEmpty()) {
            builder.setSkullOwner(itemConfig.skullOwner);
        }
        if (itemConfig.enchantments != null && !itemConfig.enchantments.isEmpty()) {
            builder.addEnchantments(itemConfig.enchantments);
        }

        // Add persistent data
        builder.addPdcValue(PersistentKeys.ITEM_ID, PersistentDataType.STRING, key);
        handleActions(itemConfig, builder);

        // Store movement type if it exists
        if (itemConfig.key.contains("movement_type")) {
            String movementType = itemConfig.key;
            if (movementType != null && !movementType.isEmpty()) {
                builder.addPdcValue(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING, movementType);
            }
        }

        // Store interact sound if it exists
        if (itemConfig.interactSound != null && itemConfig.interactSound.enabled) {
            String soundData = itemConfig.interactSound.name + ";" + itemConfig.interactSound.volume + ";" + itemConfig.interactSound.pitch;
            builder.addPdcValue(PersistentKeys.INTERACT_SOUND_KEY, PersistentDataType.STRING, soundData);
        }

        return builder.build();
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

    public void giveItem(Player player, String key, int amount, int slot) {
        ItemStack item = getItem(key, player);
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
        return itemConfigs.keySet();
    }

    private void handleActions(com.minekarta.advancedcorehub.config.MenuItemConfig itemConfig, ItemBuilder builder) {
        if (itemConfig.clickActions.containsKey("LEFT")) {
            List<String> actions = itemConfig.clickActions.get("LEFT");
            if (!actions.isEmpty()) {
                builder.addPdcValue(PersistentKeys.LEFT_CLICK_ACTIONS_KEY, PersistentDataType.STRING, String.join("\n", actions));
            }
        }
        if (itemConfig.clickActions.containsKey("RIGHT")) {
            List<String> actions = itemConfig.clickActions.get("RIGHT");
            if (!actions.isEmpty()) {
                builder.addPdcValue(PersistentKeys.RIGHT_CLICK_ACTIONS_KEY, PersistentDataType.STRING, String.join("\n", actions));
            }
        }
    }
}
