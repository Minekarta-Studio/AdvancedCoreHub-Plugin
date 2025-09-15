package com.minekarta.advancedcorehub.cosmetics;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final CosmeticsManager cosmeticsManager;

    public PlayerMoveListener(AdvancedCoreHub plugin) {
        this.cosmeticsManager = plugin.getCosmeticsManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if the player actually moved to a new block
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        ParticleTrail trail = cosmeticsManager.getActiveTrail(player);

        if (trail != null) {
            Location loc = player.getLocation();
            player.getWorld().spawnParticle(trail.getParticle(), loc, 1, 0, 0, 0, 0);
        }
    }
}
