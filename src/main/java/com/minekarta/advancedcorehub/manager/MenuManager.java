package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.MenuConfig;
import com.minekarta.advancedcorehub.config.MenuItemConfig;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
    private final Map<String, MenuConfig> menuConfigs = new HashMap<>();

    public MenuManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void loadMenus() {
        menuConfigs.clear();
        FileManager fileManager = plugin.getFileManager();
        for (Map.Entry<String, FileConfiguration> entry : fileManager.getConfigs().entrySet()) {
            String configName = entry.getKey();
            if (configName.startsWith("menus/")) {
                String menuId = configName.replace("menus/", "").replace(".yml", "");
                FileConfiguration fileConfig = entry.getValue();
                if (fileConfig != null) {
                    MenuConfig menuConfig = new MenuConfig(menuId, fileConfig);
                    menuConfigs.put(menuId, menuConfig);
                    plugin.getLogger().info("Loaded menu: " + menuId);
                }
            }
        }
    }

    public void openMenu(Player player, String menuId) {
        MenuConfig menuConfig = menuConfigs.get(menuId);
        if (menuConfig == null) {
            plugin.getLogger().warning("Menu configuration for '" + menuId + "' not found or failed to load.");
            plugin.getLocaleManager().sendMessage(player, "menu-not-found", menuId);
            return;
        }

        Component title = plugin.getLocaleManager().getComponentFromString(menuConfig.getTitle(), player);
        Inventory inventory = Bukkit.createInventory(new MenuHolder(menuId), menuConfig.getSize(), title);

        populateMenu(player, inventory, menuConfig);
        player.openInventory(inventory);
        playSound(player, "open");
    }

    private void populateMenu(Player player, Inventory inventory, MenuConfig menuConfig) {
        // Fill with filler item first
        if (menuConfig.getFillerItem() != null) {
            ItemStack fillerStack = createMenuItem(player, menuConfig.getFillerItem());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, fillerStack);
            }
        }

        // Place the actual items
        for (MenuItemConfig itemConfig : menuConfig.getItems()) {
            ItemStack itemStack = createMenuItem(player, itemConfig);
            if (itemConfig.slot != -1) {
                inventory.setItem(itemConfig.slot, itemStack);
            }
            if (itemConfig.slots != null && !itemConfig.slots.isEmpty()) {
                for (int slot : itemConfig.slots) {
                    inventory.setItem(slot, itemStack);
                }
            }
        }
    }

    private ItemStack createMenuItem(Player player, MenuItemConfig itemConfig) {
        ItemBuilder builder;

        if (itemConfig.isDynamic()) {
            int playerCount = plugin.getServerInfoManager().getPlayerCount(itemConfig.serverName);
            boolean isOnline = playerCount >= 0;
            MenuItemConfig.DynamicItemDisplay display = isOnline ? itemConfig.onlineItem : itemConfig.offlineItem;

            if (display == null) return null;

            builder = new ItemBuilder(display.material);
            builder.setDisplayName(plugin.getLocaleManager().getComponentFromString(display.displayName, player));
            builder.setLore(display.lore.stream()
                    .map(line -> plugin.getLocaleManager().getComponentFromString(line, player))
                    .collect(Collectors.toList()));
        } else {
            builder = new ItemBuilder(itemConfig.material);
            builder.setDisplayName(plugin.getLocaleManager().getComponentFromString(itemConfig.displayName, player));
            builder.setLore(itemConfig.lore.stream()
                    .map(line -> plugin.getLocaleManager().getComponentFromString(line, player))
                    .collect(Collectors.toList()));
        }

        if (itemConfig.customModelData > 0) {
            builder.setCustomModelData(itemConfig.customModelData);
        }
        if (itemConfig.enchantments != null && !itemConfig.enchantments.isEmpty()) {
            builder.addEnchantments(itemConfig.enchantments);
        }

        return builder.build();
    }

    public MenuItemConfig getMenuItem(String menuId, int slot) {
        MenuConfig menuConfig = menuConfigs.get(menuId);
        if (menuConfig == null) return null;

        for (MenuItemConfig item : menuConfig.getItems()) {
            if (item.slot == slot || (item.slots != null && item.slots.contains(slot))) {
                return item;
            }
        }
        return null;
    }

    public void playSound(Player player, String soundType) {
        PluginConfig.MenuSoundsConfig soundsConfig = plugin.getPluginConfig().menuSounds;
        PluginConfig.SoundConfig soundConfig = soundType.equalsIgnoreCase("click") ? soundsConfig.click : soundsConfig.open;

        if (soundConfig != null && soundConfig.enabled) {
            try {
                player.playSound(player.getLocation(), soundConfig.name, soundConfig.volume, soundConfig.pitch);
            } catch (Exception e) {
                plugin.getLogger().warning("Invalid sound name in menu_sounds." + soundType + ": " + soundConfig.name);
            }
        }
    }
}