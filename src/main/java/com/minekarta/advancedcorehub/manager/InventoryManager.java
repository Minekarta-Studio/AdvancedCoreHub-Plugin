package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {

    private final AdvancedCoreHub plugin;
    private List<String> hubWorlds;
    private boolean clearOnEnter;

    private final Map<UUID, ItemStack[]> playerInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> playerArmor = new HashMap<>();

    public InventoryManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        // Load the single source of truth for hub worlds from the root of the config
        this.hubWorlds = plugin.getConfig().getStringList("hub-worlds");

        ConfigurationSection invManagementConfig = plugin.getConfig().getConfigurationSection("inventory_management");
        boolean inventoryManagementEnabled = invManagementConfig != null && invManagementConfig.getBoolean("enable", true);

        if (inventoryManagementEnabled) {
            this.clearOnEnter = invManagementConfig.getBoolean("clear-on-enter", true);
        } else {
            this.clearOnEnter = false;
            // If the whole feature is disabled, clear the list so isHubWorld returns false
            this.hubWorlds.clear();
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
}
