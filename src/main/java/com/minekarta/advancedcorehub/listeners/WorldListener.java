package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final InventoryManager inventoryManager;

    public WorldListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.inventoryManager = plugin.getInventoryManager();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String newWorld = player.getWorld().getName();
        String fromWorld = event.getFrom().getName();

        // Check if the save and restore feature is enabled first
        if (!inventoryManager.isSaveAndRestoreEnabled()) {
            return;
        }

        boolean isEnteringHub = inventoryManager.isHubWorld(newWorld) && !inventoryManager.isHubWorld(fromWorld);
        boolean isLeavingHub = !inventoryManager.isHubWorld(newWorld) && inventoryManager.isHubWorld(fromWorld);

        if (isEnteringHub) {
            // Handle inventory
            if (inventoryManager.isSaveAndRestoreEnabled()) {
                inventoryManager.savePlayerInventory(player);
                inventoryManager.setupHubInventory(player);
            }
            // Handle double jump flight
            if (plugin.getConfig().getBoolean("movement_features.double_jump.enabled", true)) {
                 if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                    player.setAllowFlight(true);
                }
            }
        } else if (isLeavingHub) {
            // Handle inventory
            if (inventoryManager.isSaveAndRestoreEnabled()) {
                inventoryManager.restorePlayerInventory(player);
            }
            // Handle double jump flight
            if (plugin.getConfig().getBoolean("movement_features.double_jump.enabled", true)) {
                if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                    player.setAllowFlight(false);
                }
            }
        }
    }
}
