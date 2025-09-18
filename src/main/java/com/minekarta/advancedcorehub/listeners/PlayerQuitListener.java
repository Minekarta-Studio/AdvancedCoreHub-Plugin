package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.InventoryManager;
import com.minekarta.advancedcorehub.manager.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final InventoryManager inventoryManager;
    private final VanishManager vanishManager;

    public PlayerQuitListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.inventoryManager = plugin.getInventoryManager();
        this.vanishManager = plugin.getVanishManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remove scoreboard
        plugin.getScoreboardManager().removeBoard(player);

        // Handle inventory for players quitting from a hub world
        if (inventoryManager.isHubWorld(player.getWorld().getName())) {
            inventoryManager.restorePlayerInventory(player);
        }

        // Handle vanish state
        if (vanishManager.isVanished(player)) {
            event.quitMessage(null); // Hide quit message
            vanishManager.setVanished(player, false); // Clean up: unvanish player on quit
        } else {
            // Set custom quit message from locale
             event.quitMessage(plugin.getLocaleManager().getComponent("quit-message", player));
        }
    }
}
