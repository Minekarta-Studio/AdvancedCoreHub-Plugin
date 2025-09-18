package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
        // Check if the world is in the hub worlds list
        return plugin.getHubWorldManager().isHubWorld(player.getWorld().getName());
    }

    private boolean isHubWorld(String worldName) {
        return plugin.getHubWorldManager().isHubWorld(worldName);
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
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && plugin.getConfig().getBoolean("world_settings.cancel_block_interact", true) && shouldCancel(event.getPlayer())) {
            if (event.getClickedBlock() != null) {
                Material type = event.getClickedBlock().getType();
                if (type.isInteractable() && !type.name().contains("BUTTON") && !type.name().contains("PLATE")) {
                     event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (!shouldCancel(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && plugin.getConfig().getBoolean("world_settings.void_teleport_to_spawn", true)) {
            event.setCancelled(true);
            // Manually get spawn location and teleport
            ConfigurationSection spawnConfig = plugin.getConfig().getConfigurationSection("spawn");
            if (spawnConfig != null) {
                String worldName = spawnConfig.getString("world");
                World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    double x = spawnConfig.getDouble("x");
                    double y = spawnConfig.getDouble("y");
                    double z = spawnConfig.getDouble("z");
                    float yaw = (float) spawnConfig.getDouble("yaw");
                    float pitch = (float) spawnConfig.getDouble("pitch");
                    TeleportUtil.safeTeleport(player, new Location(world, x, y, z, yaw, pitch));
                }
            }
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent pvpEvent = (EntityDamageByEntityEvent) event;
            if (pvpEvent.getDamager() instanceof Player) {
                if (plugin.getConfig().getBoolean("world_settings.cancel_player_pvp", true)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (plugin.getConfig().getBoolean("world_settings.cancel_player_damage", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (plugin.getConfig().getBoolean("world_settings.cancel_hunger_loss", true) && shouldCancel(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (isHubWorld(event.getWorld().getName()) && plugin.getConfig().getBoolean("world_settings.cancel_weather_change", true)) {
            if (event.toWeatherState()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.getConfig().getBoolean("world_settings.cancel_item_drop", true) && shouldCancel(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (plugin.getConfig().getBoolean("world_settings.cancel_item_pickup", true) && shouldCancel(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            if (isHubWorld(event.getLocation().getWorld().getName()) && plugin.getConfig().getBoolean("world_settings.cancel_mob_spawning", true)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) {
            if (isHubWorld(event.getBlock().getWorld().getName()) && plugin.getConfig().getBoolean("world_settings.cancel_fire_spread", true)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (isHubWorld(event.getBlock().getWorld().getName()) && plugin.getConfig().getBoolean("world_settings.cancel_block_burn", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (isHubWorld(event.getBlock().getWorld().getName()) && plugin.getConfig().getBoolean("world_settings.cancel_leaf_decay", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (isHubWorld(event.getEntity().getWorld().getName()) && plugin.getConfig().getBoolean("world_settings.cancel_death_messages", true)) {
            event.setDeathMessage(null);
        }
    }
}
