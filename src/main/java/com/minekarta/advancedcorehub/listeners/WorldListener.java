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

        boolean isEnteringHub = inventoryManager.isHubWorld(newWorld) && !inventoryManager.isHubWorld(fromWorld);
        boolean isLeavingHub = !inventoryManager.isHubWorld(newWorld) && inventoryManager.isHubWorld(fromWorld);

        if (isEnteringHub) {
            inventoryManager.savePlayerInventory(player);
            inventoryManager.setupHubInventory(player);
        } else if (isLeavingHub) {
            inventoryManager.restorePlayerInventory(player);
        }
    }
}
