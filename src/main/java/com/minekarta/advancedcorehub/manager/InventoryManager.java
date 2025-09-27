package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    private final AdvancedCoreHub plugin;
    private final PluginConfig.InventoryManagementConfig config;
    private List<String> hubWorlds;

    private final Map<UUID, ItemStack[]> playerInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> playerArmor = new HashMap<>();

    public InventoryManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().inventoryManagement;
        loadConfig();
    }

    public void loadConfig() {
        if (config.enable) {
            this.hubWorlds = plugin.getPluginConfig().getHubWorlds();
        } else {
            this.hubWorlds.clear();
        }
    }

    public void setupHubInventory(Player player) {
        if (config.clearOnEnter) {
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

            plugin.getItemsManager().giveItem(player, itemName, amount, slot);
        }
    }

    public boolean isHubWorld(String worldName) {
        return hubWorlds != null && hubWorlds.contains(worldName);
    }

    public boolean isSaveAndRestoreEnabled() {
        return config.saveAndRestore;
    }

    public void savePlayerInventory(Player player) {
        playerInventories.put(player.getUniqueId(), player.getInventory().getContents());
        playerArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
    }

    public void restorePlayerInventory(Player player) {
        ItemStack[] inventory = playerInventories.remove(player.getUniqueId());
        ItemStack[] armor = playerArmor.remove(player.getUniqueId());

        if (inventory != null) {
            player.getInventory().clear();
            player.getInventory().setContents(inventory);
        }

        if (armor != null) {
            player.getInventory().setArmorContents(armor);
        }
    }

    public boolean hasSavedInventory(Player player) {
        return playerInventories.containsKey(player.getUniqueId());
    }
}