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
        playSound(player, "open", menuConfig);
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
        if (itemConfig.headTexture != null && !itemConfig.headTexture.isEmpty()) {
            builder.setHeadTexture(itemConfig.headTexture);
        } else if (itemConfig.skullOwner != null && !itemConfig.skullOwner.isEmpty()) {
            builder.setSkullOwner(itemConfig.skullOwner);
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

    public MenuConfig getMenuConfig(String menuId) {
        return menuConfigs.get(menuId);
    }

    public void playSound(Player player, String soundType, MenuConfig menuConfig) {
        PluginConfig.SoundConfig soundConfig = null;

        if (soundType.equalsIgnoreCase("click")) {
            // Prioritize menu-specific click sound
            if (menuConfig != null && menuConfig.getClickSound() != null) {
                soundConfig = menuConfig.getClickSound();
            } else {
                // Fallback to global click sound
                soundConfig = plugin.getPluginConfig().menuSounds.click;
            }
        } else if (soundType.equalsIgnoreCase("open")) {
            // Prioritize menu-specific open sound
            if (menuConfig != null && menuConfig.getOpenSound() != null) {
                soundConfig = menuConfig.getOpenSound();
            } else {
                // Fallback to global open sound
                soundConfig = plugin.getPluginConfig().menuSounds.open;
            }
        }

        if (soundConfig != null && soundConfig.enabled) {
            try {
                // Default to chest open sound if the global open sound is not configured
                String soundName = soundConfig.name;
                if (soundType.equalsIgnoreCase("open") && (menuConfig == null || menuConfig.getOpenSound() == null)) {
                    soundName = "BLOCK_CHEST_OPEN";
                }

                player.playSound(player.getLocation(), soundName, soundConfig.volume, soundConfig.pitch);
            } catch (Exception e) {
                plugin.getLogger().warning("Invalid sound name in menu_sounds." + soundType + ": " + soundConfig.name);
            }
        }
    }

    public List<String> getDynamicServerNames() {
        return menuConfigs.values().stream()
                .flatMap(menu -> menu.getItems().stream())
                .filter(item -> item.serverName != null && !item.serverName.isEmpty())
                .map(item -> item.serverName)
                .distinct()
                .collect(Collectors.toList());
    }
}