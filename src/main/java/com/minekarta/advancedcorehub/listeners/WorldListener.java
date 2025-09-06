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

        if (isEnteringHub) {
            // TODO: Add inventory saving logic here in the future
            inventoryManager.setupHubInventory(player);
        }

        // TODO: Add inventory restoring logic when leaving a hub world
    }
}
