package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.PlayerVisibilityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerVisibilityListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final PlayerVisibilityManager playerVisibilityManager;

    public PlayerVisibilityListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.playerVisibilityManager = plugin.getPlayerVisibilityManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (item.isSimilar(playerVisibilityManager.getVisibleItem(player)) || item.isSimilar(playerVisibilityManager.getHiddenItem(player))) {
            event.setCancelled(true);
            playerVisibilityManager.toggleVisibility(player);
        }
    }
}
