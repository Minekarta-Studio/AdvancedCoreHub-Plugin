package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEventListeners implements Listener {

    private final AdvancedCoreHub plugin;
    private final PluginConfig.WorldSettingsConfig config;

    public WorldEventListeners(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().worldSettings;
    }

    private boolean shouldCancel(Player player) {
        // Don't cancel for OPs or players with a bypass permission
        if (player.isOp() || player.hasPermission("advancedcorehub.bypass.worldguard")) {
            return false;
        }
        // Check if the world is in the hub worlds list
        return plugin.getHubWorldManager().isHubWorld(player.getWorld().getName());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (config.cancelBlockBreak && shouldCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (config.cancelBlockPlace && shouldCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (config.cancelPlayerDamage && shouldCancel(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (config.cancelHungerLoss && shouldCancel(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20); // Keep hunger full
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (plugin.getHubWorldManager().isHubWorld(event.getWorld().getName()) && config.cancelWeatherChange) {
            if (event.toWeatherState()) { // if it's starting to rain/thunder
                event.setCancelled(true);
            }
        }
    }
}