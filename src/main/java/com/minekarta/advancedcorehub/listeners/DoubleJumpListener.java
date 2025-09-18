package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DoubleJumpListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public DoubleJumpListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // Ensure this feature only works in configured hub worlds
        if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
            return;
        }

        // Feature disabled or player in creative/spectator
        if (!plugin.getConfig().getBoolean("double_jump.enabled", false) ||
            player.getGameMode() == GameMode.CREATIVE ||
            player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        // Prevent double jump from interfering with actual flight
        if (player.getAllowFlight()) {
            return;
        }

        event.setCancelled(true);

        // Cooldown check
        long cooldownTime = plugin.getConfig().getLong("double_jump.cooldown", 2) * 1000;
        if (cooldowns.containsKey(player.getUniqueId())) {
            long secondsLeft = ((cooldowns.get(player.getUniqueId()) + cooldownTime) - System.currentTimeMillis()) / 1000;
            if (secondsLeft > 0) {
                // Optionally send a message to the player
                // plugin.getLanguageManager().sendMessage(player, "double_jump_cooldown", "{time}", String.valueOf(secondsLeft));
                return;
            }
        }

        // Apply the double jump velocity
        double power = plugin.getConfig().getDouble("double_jump.power", 1.2);
        Vector jumpVector = player.getLocation().getDirection().multiply(0.1).setY(power);
        player.setVelocity(jumpVector);

        // Play a sound
        String soundName = plugin.getConfig().getString("double_jump.sound.name", "ENTITY_FIREWORK_ROCKET_LAUNCH");
        float volume = (float) plugin.getConfig().getDouble("double_jump.sound.volume", 1.0);
        float pitch = (float) plugin.getConfig().getDouble("double_jump.sound.pitch", 1.0);
        player.playSound(player.getLocation(), soundName, volume, pitch);

        // Set cooldown
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
