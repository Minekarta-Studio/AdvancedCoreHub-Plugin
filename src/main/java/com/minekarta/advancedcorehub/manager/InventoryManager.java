package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class InventoryManager {

    private final AdvancedCoreHub plugin;
    private List<String> hubWorlds;
    private boolean clearOnEnter;

    public InventoryManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("inventory_management");
        if (config != null) {
            this.hubWorlds = config.getStringList("hub-worlds");
            this.clearOnEnter = config.getBoolean("clear-on-enter", true);
        }
    }

    public void setupHubInventory(Player player) {
        if (clearOnEnter) {
            clearPlayerInventory(player);
        }
        giveJoinItems(player);
    }

    public void clearPlayerInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        player.setExp(0);
        player.setLevel(0);
    }

    public void giveJoinItems(Player player) {
        List<java.util.Map<?, ?>> joinItemsList = plugin.getFileManager().getConfig("items.yml").getMapList("join_items");
        if (joinItemsList.isEmpty()) return;

        String playerWorld = player.getWorld().getName();

        for (java.util.Map<?, ?> itemData : joinItemsList) {
            String itemName = (String) itemData.get("item_name");
            if (itemName == null) continue;

            // --- Condition Checks ---

            // 1. Check permission
            if (itemData.containsKey("permission")) {
                String permission = (String) itemData.get("permission");
                if (!player.hasPermission(permission)) {
                    continue;
                }
            }

            // 2. Check world
            if (itemData.containsKey("worlds")) {
                List<String> requiredWorlds = (List<String>) itemData.get("worlds");
                if (!requiredWorlds.contains(playerWorld)) {
                    continue;
                }
            }

            // --- Give Item ---
            int amount = itemData.get("amount") != null ? (int) itemData.get("amount") : 1;
            int slot = itemData.get("slot") != null ? (int) itemData.get("slot") : -1;
            boolean force = itemData.get("force") != null ? (boolean) itemData.get("force") : false;

            // Prevent item duplication if not forced
            if (!force && player.getInventory().contains(plugin.getItemsManager().getItem(itemName))) {
                continue;
            }

            plugin.getItemsManager().giveItem(player, itemName, amount, slot);
        }
    }

    public boolean isHubWorld(String worldName) {
        return hubWorlds != null && hubWorlds.contains(worldName);
    }
}
