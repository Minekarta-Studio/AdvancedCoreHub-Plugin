package com.minekarta.advancedcorehub.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuConfig {

    private final String menuId;
    private final String title;
    private final int size;
    private final List<MenuItemConfig> items;
    private final MenuItemConfig fillerItem;

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
}