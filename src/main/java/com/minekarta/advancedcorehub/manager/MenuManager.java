package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, Map<Integer, Map<String, List<String>>>> menuActionsCache = new HashMap<>();

    public MenuManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void loadMenus() {
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

        Component title = plugin.getLocaleManager().getComponentFromString(config.getString("title", "<red>Invalid Title"), player);
        int size = config.getInt("size", 27);

        Inventory inventory = Bukkit.createInventory(new MenuHolder(menuId), size, title);

        // Populate menu with filler items and then specific items
        populateMenu(player, inventory, config, menuId);

        player.openInventory(inventory);

        // Play the open sound, if configured
        ConfigurationSection openSoundSection = plugin.getConfig().getConfigurationSection("menu_sounds.open");
        if (openSoundSection != null && openSoundSection.getBoolean("enabled", false)) {
            String soundName = openSoundSection.getString("name", "ENTITY_CHICKEN_EGG").toLowerCase();
            float volume = (float) openSoundSection.getDouble("volume", 1.0);
            float pitch = (float) openSoundSection.getDouble("pitch", 1.0);
            player.playSound(player.getLocation(), soundName, volume, pitch);
        }
    }

    private void populateMenu(Player player, Inventory inventory, FileConfiguration config, String menuId) {
        // First, fill with a filler item if specified
        ConfigurationSection fillerSection = config.getConfigurationSection("filler-item");
        if (fillerSection != null) {
            try {
                ItemBuilder fillerBuilder = new ItemBuilder(fillerSection.getString("material", "GRAY_STAINED_GLASS_PANE"));
                fillerBuilder.setDisplayName(plugin.getLocaleManager().getComponentFromString(fillerSection.getString("display-name", " "), player));
                if (fillerSection.isInt("custom-model-data")) {
                    fillerBuilder.setCustomModelData(fillerSection.getInt("custom-model-data"));
                }
                ItemStack fillerStack = fillerBuilder.build();
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, fillerStack);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material for filler item in menu '" + menuId + "'. Error: " + e.getMessage());
            }
        }


        // Then, populate the specific items
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig == null) continue;

                try {
                    ServerInfoManager serverInfoManager = plugin.getServerInfoManager();
                    String serverName = itemConfig.getString("server-name");
                    ItemBuilder builder;

                    if (serverName != null && !serverName.isEmpty()) {
                        // Dynamic server item
                        int playerCount = serverInfoManager.getPlayerCount(serverName);
                        boolean isOnline = playerCount >= 0;
                        ConfigurationSection displayConfig = itemConfig.getConfigurationSection(isOnline ? "online-item" : "offline-item");

                        if (displayConfig == null) {
                            plugin.getLogger().warning("Menu '" + menuId + "', item '" + key + "' is missing " + (isOnline ? "online-item" : "offline-item") + " section.");
                            continue;
                        }
                        builder = new ItemBuilder(displayConfig.getString("material", "STONE"));
                        builder.setDisplayName(plugin.getLocaleManager().getComponentFromString(displayConfig.getString("display-name", " "), player));
                        builder.setLore(displayConfig.getStringList("lore").stream()
                                .map(line -> plugin.getLocaleManager().getComponentFromString(line, player))
                                .collect(Collectors.toList()));

                    } else {
                        // Static item
                        builder = new ItemBuilder(itemConfig.getString("material", "STONE"));
                        builder.setDisplayName(plugin.getLocaleManager().getComponentFromString(itemConfig.getString("display-name", " "), player));
                        if (itemConfig.contains("lore")) {
                            builder.setLore(itemConfig.getStringList("lore").stream()
                                    .map(line -> plugin.getLocaleManager().getComponentFromString(line, player))
                                    .collect(Collectors.toList()));
                        }
                    }

                    if (itemConfig.isInt("custom-model-data")) {
                        builder.setCustomModelData(itemConfig.getInt("custom-model-data"));
                    }

                    if (itemConfig.contains("enchantments")) {
                        builder.addEnchantments(itemConfig.getStringList("enchantments"));
                    }

                    ItemStack itemStack = builder.build();

                    if (itemConfig.contains("slot")) {
                        inventory.setItem(itemConfig.getInt("slot"), itemStack);
                    } else if (itemConfig.contains("slots")) {
                        for (int slot : itemConfig.getIntegerList("slots")) {
                            inventory.setItem(slot, itemStack);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in menu '" + menuId + "' for item '" + key + "'. Error: " + e.getMessage());
                }
            }
        }
    }


    public Map<Integer, Map<String, List<String>>> getActionsForMenu(String menuId) {
        if (menuActionsCache.containsKey(menuId)) {
            return menuActionsCache.get(menuId);
        }

        FileConfiguration config = plugin.getFileManager().getConfig("menus/" + menuId + ".yml");
        if (config == null) {
            return new HashMap<>();
        }

        Map<Integer, Map<String, List<String>>> menuActions = new HashMap<>();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig == null) continue;

                Map<String, List<String>> clickActions = new HashMap<>();
                if (itemConfig.contains("left-click-actions")) {
                    clickActions.put("LEFT", itemConfig.getStringList("left-click-actions"));
                }
                if (itemConfig.contains("right-click-actions")) {
                    clickActions.put("RIGHT", itemConfig.getStringList("right-click-actions"));
                }
                if (itemConfig.contains("actions")) {
                    plugin.getLogger().warning("Menu '" + menuId + "', item '" + key + "' uses deprecated 'actions' key. Please use 'left-click-actions' or 'right-click-actions'.");
                    clickActions.putIfAbsent("RIGHT", itemConfig.getStringList("actions"));
                }

                if (!clickActions.isEmpty()) {
                    if (itemConfig.contains("slot")) {
                        int slot = itemConfig.getInt("slot");
                        menuActions.put(slot, clickActions);
                    } else if (itemConfig.contains("slots")) {
                        for (int slot : itemConfig.getIntegerList("slots")) {
                            menuActions.put(slot, clickActions);
                        }
                    }
                }
            }
        }

        menuActionsCache.put(menuId, menuActions);
        return menuActions;
    }
}
