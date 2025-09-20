package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.InventoryManager;
import com.minekarta.advancedcorehub.manager.PlayerVisibilityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final InventoryManager inventoryManager;
    private final PlayerVisibilityManager playerVisibilityManager;

    public PlayerQuitListener(AdvancedCoreHub plugin) {
        this.inventoryManager = plugin.getInventoryManager();
        this.playerVisibilityManager = plugin.getPlayerVisibilityManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (inventoryManager.isHubWorld(player.getWorld().getName())) {
            inventoryManager.restorePlayerInventory(player);
        }
        playerVisibilityManager.handlePlayerQuit(player);
    }
}
