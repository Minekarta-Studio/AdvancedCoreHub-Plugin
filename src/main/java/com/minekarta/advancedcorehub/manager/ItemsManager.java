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

                Component displayName = plugin.getLocaleManager().getComponentFromString(itemConfig.getString("displayname", ""), null);
                builder.setDisplayName(displayName);

                if (itemConfig.contains("lore")) {
                    List<Component> lore = itemConfig.getStringList("lore").stream()
                            .map(line -> plugin.getLocaleManager().getComponentFromString(line, null))
                            .collect(Collectors.toList());
                    builder.setLore(lore);
                }

                // Add a persistent key to identify this as a custom item from our plugin
                builder.addPdcValue(PersistentKeys.ITEM_ID, PersistentDataType.STRING, key);

                // Store actions if they exist
                if (itemConfig.contains("actions")) {
                    List<String> actions = itemConfig.getStringList("actions");
                    if (!actions.isEmpty()) {
                        // Join the list into a single string, separated by a newline character.
                        String actionString = String.join("\n", actions);
                        builder.addPdcValue(PersistentKeys.ACTIONS_KEY, PersistentDataType.STRING, actionString);
                    }
                }

                // Store movement type if it exists
                if (itemConfig.contains("movement_type")) {
                    String movementType = itemConfig.getString("movement_type");
                    if (movementType != null && !movementType.isEmpty()) {
                        builder.addPdcValue(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING, movementType);
                    }
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

    public java.util.Set<String> getItemKeys() {
        return customItems.keySet();
    }
}
