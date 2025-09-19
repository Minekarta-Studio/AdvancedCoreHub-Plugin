package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.cosmetics.Gadget;
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

        // Special handling for the dynamic gadget menu
        if (menuId.equalsIgnoreCase("vip_gadget")) {
            populateGadgetMenu(player, inventory);
        } else {
            // Standard static menu population
            populateStaticMenu(player, inventory, config, menuId);
        }

        player.openInventory(inventory);
    }

    private void populateStaticMenu(Player player, Inventory inventory, FileConfiguration config, String menuId) {
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig == null) continue;

                try {
                    ServerInfoManager serverInfoManager = plugin.getServerInfoManager();
                    String serverName = itemConfig.getString("server-name");
                    int playerCount = -1;

                    if (serverName != null && !serverName.isEmpty()) {
                        playerCount = serverInfoManager.getPlayerCount(serverName);
                    }

                    String materialString = itemConfig.getString("material", "STONE");
                    ItemBuilder builder = new ItemBuilder(materialString);

                    if (serverName != null && !serverName.isEmpty()) {
                        builder.setMaterial(playerCount >= 0 ? Material.LIME_WOOL : Material.RED_WOOL);
                    }

                    Component displayName = plugin.getLocaleManager().getComponentFromString(itemConfig.getString("display-name", " "), player);
                    builder.setDisplayName(displayName);

                    if (itemConfig.contains("lore")) {
                        final String finalServerName = serverName; // Capture server name for lambda
                        List<Component> lore = itemConfig.getStringList("lore").stream()
                                .map(line -> {
                                    String processedLine = line;
                                    if (finalServerName != null && !finalServerName.isEmpty()) {
                                        processedLine = processedLine.replace("%players%", "%advancedcorehub_players_" + finalServerName + "%")
                                                                     .replace("%status%", "%advancedcorehub_status_" + finalServerName + "%");
                                    }
                                    return plugin.getLocaleManager().getComponentFromString(processedLine, player);
                                })
                                .collect(Collectors.toList());
                        builder.setLore(lore);
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
                    plugin.getLogger().warning("Invalid material '" + itemConfig.getString("material") + "' in menu '" + menuId + "' for item '" + key + "'.");
                }
            }
        }
    }

    private void populateGadgetMenu(Player player, Inventory inventory) {
        Map<String, Gadget> gadgets = plugin.getGadgetManager().getPlayerGadgets(player);
        int slot = 10; // Starting slot for gadgets
        for (Gadget gadget : gadgets.values()) {
            if (slot > 16) break; // Limit to one row for now

            ItemBuilder builder = new ItemBuilder(gadget.material());
            builder.setDisplayName(plugin.getLocaleManager().getComponentFromString(gadget.displayName(), player));
            List<Component> lore = gadget.lore().stream()
                    .map(line -> plugin.getLocaleManager().getComponentFromString(line, player))
                    .collect(Collectors.toList());
            builder.setLore(lore);
            builder.addPdcValue(PersistentKeys.GADGET_ID, PersistentDataType.STRING, gadget.id());

            inventory.setItem(slot, builder.build());
            slot++;
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
