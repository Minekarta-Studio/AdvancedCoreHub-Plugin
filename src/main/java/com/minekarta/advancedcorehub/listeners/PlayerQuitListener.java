package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final InventoryManager inventoryManager;

    public PlayerQuitListener(AdvancedCoreHub plugin) {
        this.inventoryManager = plugin.getInventoryManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (inventoryManager.isHubWorld(player.getWorld().getName())) {
            inventoryManager.restorePlayerInventory(player);
        }
    }
}
