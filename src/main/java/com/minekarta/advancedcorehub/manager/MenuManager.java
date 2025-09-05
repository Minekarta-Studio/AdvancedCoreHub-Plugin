package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import com.minekarta.advancedcorehub.util.MenuHolder;
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
        loadMenu("selector");
        loadMenu("socials");
    }

    private void loadMenu(String menuId) {
        FileConfiguration config = plugin.getFileManager().getConfig("menus/" + menuId + ".yml");
        if (config == null) {
            plugin.getLogger().warning("Menu configuration for '" + menuId + "' not found.");
            return;
        }

        Component title = plugin.getLocaleManager().getComponent(config.getString("title", "&cInvalid Title"), null);
        int size = config.getInt("size", 27);
        MenuHolder holder = new MenuHolder(menuId);
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inventory);

        Map<Integer, List<String>> actions = new HashMap<>();

        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
                if (itemConfig == null) continue;

                // For now, we build items with static text. A future refactor would parse placeholders on open.
                Material material = Material.valueOf(itemConfig.getString("material", "STONE").toUpperCase());
                ItemBuilder builder = new ItemBuilder(material);
                builder.setDisplayName(itemConfig.getString("display-name", " "));
                if (itemConfig.contains("lore")) {
                    builder.setLore(itemConfig.getStringList("lore"));
                }
                ItemStack itemStack = builder.build();

                List<String> itemActions = itemConfig.getStringList("actions");

                if (itemConfig.contains("slot")) {
                    int slot = itemConfig.getInt("slot");
                    inventory.setItem(slot, itemStack);
                    if (!itemActions.isEmpty()) actions.put(slot, itemActions);
                } else if (itemConfig.contains("slots")) {
                    for (int slot : itemConfig.getIntegerList("slots")) {
                        inventory.setItem(slot, itemStack);
                        if (!itemActions.isEmpty()) actions.put(slot, itemActions);
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
        // A proper implementation would parse per-player placeholders here.
        // For now, we just open the pre-built inventory.
        player.openInventory(menu);
    }

    public Map<Integer, List<String>> getActions(String menuId) {
        return menuActions.get(menuId);
    }
}
