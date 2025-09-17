package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.cosmetics.Gadget;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GadgetManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, Gadget> gadgets = new HashMap<>();

    public GadgetManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void loadGadgets() {
        gadgets.clear();
        ConfigurationSection gadgetsSection = plugin.getFileManager().getConfig("gadgets.yml").getConfigurationSection("gadgets");
        if (gadgetsSection == null) {
            plugin.getLogger().info("No 'gadgets' section found in gadgets.yml. No gadgets will be loaded.");
            return;
        }

        for (String key : gadgetsSection.getKeys(false)) {
            ConfigurationSection gadgetConfig = gadgetsSection.getConfigurationSection(key);
            if (gadgetConfig == null) continue;

            try {
                Gadget gadget = new Gadget(
                        key,
                        gadgetConfig.getString("material", "STONE"),
                        gadgetConfig.getString("display-name", "<red>Unnamed Gadget"),
                        gadgetConfig.getStringList("lore"),
                        gadgetConfig.getString("permission"),
                        gadgetConfig.getInt("cooldown", 0),
                        gadgetConfig.getStringList("actions")
                );
                gadgets.put(key, gadget);
                plugin.getLogger().info("Loaded gadget: " + key);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load gadget with key '" + key + "' from gadgets.yml", e);
            }
        }
    }

    public Gadget getGadget(String id) {
        return gadgets.get(id);
    }

    public Map<String, Gadget> getPlayerGadgets(Player player) {
        if (player == null) {
            return Collections.emptyMap();
        }
        return gadgets.entrySet().stream()
                .filter(entry -> {
                    String permission = entry.getValue().permission();
                    return permission == null || permission.isEmpty() || player.hasPermission(permission);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
