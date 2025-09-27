package com.minekarta.advancedcorehub.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MenuConfig {

    private final String menuId;
    private final String title;
    private final int size;
    private final List<MenuItemConfig> items;
    private final MenuItemConfig fillerItem;
    private final PluginConfig.SoundConfig openSound;
    private final PluginConfig.SoundConfig clickSound;

    public MenuConfig(String menuId, FileConfiguration config) {
        this.menuId = menuId;
        this.title = config.getString("title", "<red>Invalid Menu</red>");
        this.size = config.getInt("size", 27);
        this.items = new ArrayList<>();

        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                items.add(new MenuItemConfig(key, itemsSection.getConfigurationSection(key)));
            }
        }

        if (config.isConfigurationSection("filler-item")) {
            this.fillerItem = new MenuItemConfig("filler-item", config.getConfigurationSection("filler-item"));
        } else {
            this.fillerItem = null;
        }

        if (config.isConfigurationSection("open-sound")) {
            this.openSound = new PluginConfig.SoundConfig(config.getConfigurationSection("open-sound"));
        } else {
            this.openSound = null;
        }

        if (config.isConfigurationSection("click-sound")) {
            this.clickSound = new PluginConfig.SoundConfig(config.getConfigurationSection("click-sound"));
        } else {
            this.clickSound = null;
        }
    }

    public String getMenuId() {
        return menuId;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public List<MenuItemConfig> getItems() {
        return items;
    }

    public MenuItemConfig getFillerItem() {
        return fillerItem;
    }

    public PluginConfig.SoundConfig getOpenSound() {
        return openSound;
    }

    public PluginConfig.SoundConfig getClickSound() {
        return clickSound;
    }
}