package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, Inventory> menus = new HashMap<>();
    private final Map<String, Map<Integer, List<String>>> menuActions = new HashMap<>();

    public MenuManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        loadMenus();
    }

    public void loadMenus() {
        menus.clear();
        menuActions.clear();
        // Assuming menu files are listed in config.yml, but prompt implies direct loading
        loadMenu("selector", plugin.getFileManager().getConfig("menus/selector.yml"));
        loadMenu("socials", plugin.getFileManager().getConfig("menus/socials.yml"));
    }

    private void loadMenu(String menuId, FileConfiguration config) {
        if (config == null) {
            plugin.getLogger().warning("Menu configuration for '" + menuId + "' not found.");
            return;
        }
        String title = plugin.getLocaleManager().get(config.getString("title", "&cInvalid Title"), null);
        int size = config.getInt("size", 27);

        Inventory inventory = Bukkit.createInventory(null, size, title);
        Map<Integer, List<String>> actions = new HashMap<>();

        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig == null) continue;

                Material material = Material.valueOf(itemConfig.getString("material", "STONE").toUpperCase());
                ItemBuilder builder = new ItemBuilder(material);
                builder.setDisplayName(itemConfig.getString("display-name", " "));
                if (itemConfig.contains("lore")) {
                    builder.setLore(itemConfig.getStringList("lore"));
                }
                ItemStack itemStack = builder.build();

                if (itemConfig.contains("actions")) {
                    List<String> itemActions = itemConfig.getStringList("actions");
                    if (itemConfig.contains("slot")) {
                        int slot = itemConfig.getInt("slot");
                        inventory.setItem(slot, itemStack);
                        actions.put(slot, itemActions);
                    } else if (itemConfig.contains("slots")) {
                        for (int slot : itemConfig.getIntegerList("slots")) {
                            inventory.setItem(slot, itemStack);
                            actions.put(slot, itemActions);
                        }
                    }
                } else {
                     if (itemConfig.contains("slot")) {
                        inventory.setItem(itemConfig.getInt("slot"), itemStack);
                    } else if (itemConfig.contains("slots")) {
                        for (int slot : itemConfig.getIntegerList("slots")) {
                            inventory.setItem(slot, itemStack);
                        }
                    }
                }
            }
        }
        menus.put(menuId, inventory);
        menuActions.put(menuId, actions);
    }

    public void openMenu(Player player, String menuId) {
        Inventory menu = menus.get(menuId);
        if (menu == null) {
            plugin.getLocaleManager().sendMessage(player, "menu-not-found", menuId);
            return;
        }
        // We need to clone the inventory to prevent multiple players from seeing the same instance
        // and to allow for per-player placeholders in the future.
        // For this implementation, we'll create a fresh copy.
        Inventory clonedMenu = Bukkit.createInventory(null, menu.getSize(), menu.getViewers().get(0).getOpenInventory().title());
        clonedMenu.setContents(menu.getContents());
        player.openInventory(clonedMenu);
    }

    public Map<Integer, List<String>> getActionsForMenu(String menuTitle) {
        // This is a bit of a hack. A better system would tag the inventory itself.
        for (Map.Entry<String, Inventory> entry : menus.entrySet()) {
            if (entry.getValue().getViewers().get(0).getOpenInventory().getTitle().equals(menuTitle)) {
                return menuActions.get(entry.getKey());
            }
        }
        return null;
    }
}
