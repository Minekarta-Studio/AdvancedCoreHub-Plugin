package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.CooldownManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class MovementFeaturesListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final CooldownManager cooldownManager;

    public MovementFeaturesListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.cooldownManager = plugin.getCooldownManager();
    }

    // --- Launchpad Logic ---
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ConfigurationSection launchpadConfig = plugin.getConfig().getConfigurationSection("movement_features.launchpad");
        if (launchpadConfig == null || !launchpadConfig.getBoolean("enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
            return;
        }

        Location to = event.getTo();
        if (to == null) return;

        // Check if player moved to a new block
        if (event.getFrom().getBlockX() == to.getBlockX() && event.getFrom().getBlockY() == to.getBlockY() && event.getFrom().getBlockZ() == to.getBlockZ()) {
            return;
        }

        Material plateType = Material.matchMaterial(launchpadConfig.getString("plate_type", "HEAVY_WEIGHTED_PRESSURE_PLATE"));
        Material baseBlockType = Material.matchMaterial(launchpadConfig.getString("base_block", "REDSTONE_BLOCK"));

        if (plateType == null || baseBlockType == null) return;

        if (to.getBlock().getType() == plateType) {
            if (to.getBlock().getRelative(BlockFace.DOWN).getType() == baseBlockType) {
                double powerVertical = launchpadConfig.getDouble("power_vertical", 2.0);
                double powerHorizontal = launchpadConfig.getDouble("power_horizontal", 1.0);

                Vector direction = player.getLocation().getDirection().multiply(powerHorizontal).setY(powerVertical);
                player.setVelocity(direction);

                // Play sound
                String soundString = launchpadConfig.getString("launch_sound");
                if (soundString != null && !soundString.isEmpty()) {
                    try {
                        String[] parts = soundString.split(";");
                        Sound sound = Sound.valueOf(parts[0].toUpperCase());
                        float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                        float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                        player.playSound(player.getLocation(), sound, volume, pitch);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Invalid sound format in config for launchpad: " + soundString);
                    }
                }
            }
        }
    }

    // --- Double Jump Logic ---
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        ConfigurationSection djConfig = plugin.getConfig().getConfigurationSection("movement_features.double_jump");

        if (djConfig == null || !djConfig.getBoolean("enabled", true)) {
            return;
        }

        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE || player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            return;
        }

        if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
            return;
        }

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        String cooldownKey = "double_jump";
        long cooldownTime = djConfig.getInt("cooldown", 3);

        if (cooldownManager.hasCooldown(player, cooldownKey)) {
            return;
        }

        // Apply velocity
        double powerVertical = djConfig.getDouble("power_vertical", 1.2);
        double powerHorizontal = djConfig.getDouble("power_horizontal", 0.8);
        Vector direction = player.getLocation().getDirection().multiply(powerHorizontal).setY(powerVertical);
        player.setVelocity(direction);

        // Play sound
        String soundString = djConfig.getString("jump_sound");
        if (soundString != null && !soundString.isEmpty()) {
            try {
                String[] parts = soundString.split(";");
                Sound sound = Sound.valueOf(parts[0].toUpperCase());
                float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                player.playSound(player.getLocation(), sound, volume, pitch);
            } catch (Exception e) {
                plugin.getLogger().warning("Invalid sound format in config for double_jump: " + soundString);
            }
        }

        // Show particle
        String particleString = djConfig.getString("particle_effect");
        if (particleString != null && !particleString.isEmpty()) {
             try {
                String[] parts = particleString.split(";");
                Particle particle = Particle.valueOf(parts[0].toUpperCase());
                int count = parts.length > 1 ? Integer.parseInt(parts[1]) : 20;
                double offsetX = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.5;
                double offsetY = parts.length > 3 ? Double.parseDouble(parts[3]) : 0.5;
                double offsetZ = parts.length > 4 ? Double.parseDouble(parts[4]) : 0.5;
                player.getWorld().spawnParticle(particle, player.getLocation(), count, offsetX, offsetY, offsetZ, 0);
            } catch (Exception e) {
                plugin.getLogger().warning("Invalid particle format in config for double_jump: " + particleString);
            }
        }

        cooldownManager.setCooldown(player, cooldownKey, cooldownTime);
    }

    @EventHandler
    public void onPlayerGroundMove(PlayerMoveEvent event) {
        ConfigurationSection djConfig = plugin.getConfig().getConfigurationSection("movement_features.double_jump");
        if (djConfig == null || !djConfig.getBoolean("enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE || player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            return;
        }

        if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
            return;
        }

        // Bukkit's isOnGround is not always reliable, check the block below.
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
            player.setAllowFlight(true);
        }
    }
}
