package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.manager.CooldownManager;
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

    public DoubleJumpListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().doubleJump;
        this.cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (cooldownManager.hasCooldown(player, "double_jump")) {
            event.setCancelled(true);
            return;
        }

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
        if (player.getGameMode() != GameMode.CREATIVE &&
            player.getGameMode() != GameMode.SPECTATOR &&
            player.isOnGround() &&
            !player.getAllowFlight()) {
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