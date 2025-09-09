package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final InventoryManager inventoryManager;

    public PlayerConnectionListener(AdvancedCoreHub plugin) {
        this.inventoryManager = plugin.getInventoryManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (inventoryManager.isHubWorld(player.getWorld().getName())) {
            inventoryManager.savePlayerInventory(player);
            inventoryManager.setupHubInventory(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (inventoryManager.isHubWorld(player.getWorld().getName())) {
            inventoryManager.restorePlayerInventory(player);
        }
    }
}
