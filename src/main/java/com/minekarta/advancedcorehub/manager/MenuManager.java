package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuManager {

    private final AdvancedCoreHub plugin;
    // Cache for menu actions to avoid file reads on every click
    private final Map<String, Map<Integer, List<String>>> menuActionsCache = new HashMap<>();

    public MenuManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void loadMenus() {
        // This method now simply clears the action cache on reload.
        menuActionsCache.clear();
        plugin.getLogger().info("Menu action cache cleared.");
    }

    public void openMenu(Player player, String menuId) {
        FileConfiguration config = plugin.getFileManager().getConfig("menus/" + menuId + ".yml");
        if (config == null) {
            plugin.getLogger().warning("Menu configuration for '" + menuId + ".yml' not found.");
            plugin.getLocaleManager().sendMessage(player, "menu-not-found", menuId);
            return;
        }

        // Parse title with player-specific placeholders
        Component title = plugin.getLocaleManager().getComponentFromString(config.getString("title", "<red>Invalid Title"), player);
        int size = config.getInt("size", 27);

        // Create inventory with our custom holder
        Inventory inventory = Bukkit.createInventory(new MenuHolder(menuId), size, title);

        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig == null) continue;

                try {
                    String materialString = itemConfig.getString("material", "STONE");
                    ItemBuilder builder = new ItemBuilder(materialString);

                    // Parse display name and lore with player-specific placeholders
                    Component displayName = plugin.getLocaleManager().getComponentFromString(itemConfig.getString("display-name", " "), player);
                    builder.setDisplayName(displayName);

                    if (itemConfig.contains("lore")) {
                        List<Component> lore = itemConfig.getStringList("lore").stream()
                                .map(line -> plugin.getLocaleManager().getComponentFromString(line, player))
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

                    ItemStack itemStack = builder.build();

                    // Place item(s) in the inventory
                    if (itemConfig.contains("slot")) {
                        inventory.setItem(itemConfig.getInt("slot"), itemStack);
                    } else if (itemConfig.contains("slots")) {
                        for (int slot : itemConfig.getIntegerList("slots")) {
                            inventory.setItem(slot, itemStack);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material '" + itemConfig.getString("material") + "' in menu '" + menuId + "' for item '" + key + "'.");
                }
            }
        }
        player.openInventory(inventory);
    }

    public Map<Integer, List<String>> getActionsForMenu(String menuId) {
        // Check cache first
        if (menuActionsCache.containsKey(menuId)) {
            return menuActionsCache.get(menuId);
        }

        FileConfiguration config = plugin.getFileManager().getConfig("menus/" + menuId + ".yml");
        if (config == null) {
            return null; // Or an empty map
        }

        Map<Integer, List<String>> actions = new HashMap<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig != null && itemConfig.contains("actions")) {
                    List<String> itemActions = itemConfig.getStringList("actions");
                    if (itemConfig.contains("slot")) {
                        actions.put(itemConfig.getInt("slot"), itemActions);
                    } else if (itemConfig.contains("slots")) {
                        for (int slot : itemConfig.getIntegerList("slots")) {
                            actions.put(slot, itemActions);
                        }
                    }
                }
            }
        }

        menuActionsCache.put(menuId, actions);
        return actions;
    }
}
