package com.minekarta.advancedcorehub.config;

import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MenuItemConfig {
    public final String key;
    public final String material;
    public final String displayName;
    public final List<String> lore;
    public final int customModelData;
    public final List<String> enchantments;
    public final int slot;
    public final List<Integer> slots;
    public final Map<String, List<String>> clickActions;
    public final PluginConfig.SoundConfig interactSound;
    public final String skullOwner;
    public final String headTexture;

    // For dynamic server items
    public final String serverName;
    public final DynamicItemDisplay onlineItem;
    public final DynamicItemDisplay offlineItem;

    public MenuItemConfig(String key, ConfigurationSection section) {
        this.key = key;
        this.serverName = section.getString("server-name");

        if (serverName != null && !serverName.isEmpty()) {
            // Dynamic item
            ConfigurationSection onlineSection = section.getConfigurationSection("online-item");
            ConfigurationSection offlineSection = section.getConfigurationSection("offline-item");
            this.onlineItem = onlineSection != null ? new DynamicItemDisplay(onlineSection) : null;
            this.offlineItem = offlineSection != null ? new DynamicItemDisplay(offlineSection) : null;

            // Set other fields to defaults for dynamic items as they are defined in sub-sections
            this.material = null;
            this.displayName = null;
            this.lore = null;
        } else {
            // Static item
            this.material = section.getString("material", "STONE");
            this.displayName = section.getString("display-name", " ");
            this.lore = section.getStringList("lore");
            this.onlineItem = null;
            this.offlineItem = null;
        }

        this.customModelData = section.getInt("custom-model-data", 0);
        this.enchantments = section.getStringList("enchantments");
        this.slot = section.getInt("slot", -1);
        this.slots = section.getIntegerList("slots");
        this.skullOwner = section.getString("skull-owner");
        this.headTexture = section.getString("head-texture");

        this.clickActions = new HashMap<>();
        if (section.contains("left-click-actions")) {
            clickActions.put("LEFT", section.getStringList("left-click-actions"));
        }
        if (section.contains("right-click-actions")) {
            clickActions.put("RIGHT", section.getStringList("right-click-actions"));
        }
        if (section.contains("actions")) {
            clickActions.putIfAbsent("RIGHT", section.getStringList("actions"));
        }

        if (section.isConfigurationSection("interact-sound")) {
            this.interactSound = new PluginConfig.SoundConfig(section.getConfigurationSection("interact-sound"));
        } else {
            this.interactSound = null;
        }
    }

    public boolean isDynamic() {
        return this.serverName != null && !this.serverName.isEmpty();
    }

    public static class DynamicItemDisplay {
        public final String material;
        public final String displayName;
        public final List<String> lore;

        public DynamicItemDisplay(ConfigurationSection section) {
            this.material = section.getString("material", "STONE");
            this.displayName = section.getString("display-name", " ");
            this.lore = section.getStringList("lore");
        }
    }
}