package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
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

    public WorldEventListeners(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    private boolean shouldCancel(Player player) {
        // Don't cancel for OPs or players with a bypass permission
        if (player.isOp() || player.hasPermission("advancedcorehub.bypass.worldguard")) {
            return false;
        }
        // Check if the world is in the disabled list
        return plugin.getDisabledWorlds().isDisabled(player.getWorld().getName());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getConfig().getBoolean("world_settings.cancel_block_break", true) && shouldCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getConfig().getBoolean("world_settings.cancel_block_place", true) && shouldCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (plugin.getConfig().getBoolean("world_settings.cancel_player_damage", true) && shouldCancel(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (plugin.getConfig().getBoolean("world_settings.cancel_hunger_loss", true) && shouldCancel(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20); // Keep hunger full
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (plugin.getDisabledWorlds().isDisabled(event.getWorld().getName()) &&
            plugin.getConfig().getBoolean("world_settings.cancel_weather_change", true)) {
            if (event.toWeatherState()) { // if it's starting to rain/thunder
                event.setCancelled(true);
            }
        }
    }
}
