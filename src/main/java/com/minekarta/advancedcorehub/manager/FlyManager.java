package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyManager implements Listener {

    private final AdvancedCoreHub plugin;
    private final Map<UUID, BukkitTask> temporaryFlyTasks = new HashMap<>();

    public FlyManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void toggleFlight(Player target, int duration) {
        // If flight is currently enabled, turn it off regardless of whether it was temp or not.
        if (target.getAllowFlight()) {
            disableFlight(target);
        } else {
            // Otherwise, enable it, either permanently or temporarily.
            enableFlight(target, duration);
        }
    }

    private void enableFlight(Player target, int duration) {
        // Cancel any existing temp flight task before starting a new one.
        cancelTask(target.getUniqueId());

        target.setAllowFlight(true);
        target.setFlying(true);

        if (duration > 0) {
            BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (target.isOnline() && target.getAllowFlight()) {
                    disableFlight(target);
                    plugin.getLocaleManager().sendMessage(target, "fly-temp-expired");
                }
            }, duration * 20L);
            temporaryFlyTasks.put(target.getUniqueId(), task);
        }
    }

    public void disableFlight(Player target) {
        cancelTask(target.getUniqueId());
        target.setAllowFlight(false);
        target.setFlying(false);
    }

    private void cancelTask(UUID uuid) {
        if (temporaryFlyTasks.containsKey(uuid)) {
            temporaryFlyTasks.remove(uuid).cancel();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Ensure tasks are cancelled if a player with temp fly logs out.
        cancelTask(event.getPlayer().getUniqueId());
    }

    public boolean isTempFlying(Player player) {
        return temporaryFlyTasks.containsKey(player.getUniqueId());
    }
}
