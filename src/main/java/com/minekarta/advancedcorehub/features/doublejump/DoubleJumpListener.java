package com.minekarta.advancedcorehub.features.doublejump;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.manager.CooldownManager;
import com.minekarta.advancedcorehub.manager.HubWorldManager;
import com.minekarta.advancedcorehub.player.PlayerManager;
import com.minekarta.advancedcorehub.util.Constants;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class DoubleJumpListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final PluginConfig.DoubleJumpConfig config;
    private final CooldownManager cooldownManager;
    private final PlayerManager playerManager;
    private final HubWorldManager hubWorldManager;

    public DoubleJumpListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().doubleJump;
        this.cooldownManager = plugin.getCooldownManager();
        this.playerManager = (PlayerManager) plugin.getPlayerManager(); // Cast from interface
        this.hubWorldManager = plugin.getHubWorldManager();
    }

    /**
     * Checks if a player is eligible to use the double jump feature.
     * This centralizes all security and state checks.
     */
    private boolean canUseDoubleJump(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }
        if (!player.hasPermission(Constants.PERM_DOUBLE_JUMP)) {
            return false;
        }
        if (!hubWorldManager.isHubWorld(player.getWorld().getName())) {
            return false;
        }
        // Check per-player setting from PlayerData
        return playerManager.getPlayerData(player.getUniqueId())
                .map(data -> data.isDoubleJumpEnabled())
                .orElse(false); // Default to false if data isn't loaded
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // Perform all security checks before executing the double jump
        if (!canUseDoubleJump(player)) {
            // If they shouldn't be able to fly, ensure flight is disabled.
            if(player.isFlying()) {
                player.setFlying(false);
            }
            if(player.getAllowFlight()) {
                player.setAllowFlight(false);
            }
            event.setCancelled(true);
            return;
        }

        if (cooldownManager.hasCooldown(player, "double_jump")) {
            event.setCancelled(true);
            return;
        }

        // Execute the double jump
        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        player.setVelocity(player.getLocation().getDirection().multiply(config.power).setY(config.power));
        playSound(player);

        cooldownManager.setCooldown(player, "double_jump", config.cooldown);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player is eligible to have their double jump "recharged"
        if (!canUseDoubleJump(player)) {
            return;
        }

        // Recharge the double jump ability only if they are on the ground and not in water.
        if (player.isOnGround() && !player.getAllowFlight() && !player.isInWater()) {
            player.setAllowFlight(true);
        }
    }

    private void playSound(Player player) {
        if (!config.sound.enabled || config.sound.name == null || config.sound.name.isEmpty()) {
            return;
        }

        try {
            Sound sound = Sound.valueOf(config.sound.name.toUpperCase());
            player.playSound(player.getLocation(), sound, config.sound.volume, config.sound.pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound name in config.yml for double jump: " + config.sound.name);
        }
    }
}