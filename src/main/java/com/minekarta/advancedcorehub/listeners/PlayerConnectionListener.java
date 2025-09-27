package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.manager.InventoryManager;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class PlayerConnectionListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final PluginConfig config;
    private final InventoryManager inventoryManager;

    public PlayerConnectionListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        this.inventoryManager = plugin.getInventoryManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 1. Teleport to spawn
        if (config.spawn.spawnOnJoinEnabled) {
            PluginConfig.SpawnConfig spawnConfig = config.spawn;
            World world = plugin.getServer().getWorld(spawnConfig.world);
            if (world != null) {
                player.teleport(new Location(world, spawnConfig.x, spawnConfig.y, spawnConfig.z, spawnConfig.yaw, spawnConfig.pitch));
            } else {
                plugin.getLogger().warning("Spawn world '" + spawnConfig.world + "' not found!");
            }
        }

        // 2. Handle Join Message
        event.joinMessage(plugin.getLocaleManager().getComponent("join-message", player));

        // 3. Execute actions_on_join
        List<Map<?, ?>> joinActions = config.getActionsOnJoin();
        if (!joinActions.isEmpty()) {
            plugin.getActionManager().executeMapActions(player, joinActions);
        }

        // 4. Handle Hub Inventory
        if (inventoryManager.isHubWorld(player.getWorld().getName())) {
            if (inventoryManager.isSaveAndRestoreEnabled()) {
                inventoryManager.savePlayerInventory(player);
            }
            inventoryManager.setupHubInventory(player);
        } else {
            inventoryManager.giveJoinItems(player);
        }

        // 5. Handle Boss Bar
        if (config.bossBar.showOnJoin) {
            PluginConfig.BossBarConfig bossBarConfig = config.bossBar;
            try {
                BarColor color = BarColor.valueOf(bossBarConfig.color.toUpperCase());
                BarStyle style = BarStyle.valueOf(bossBarConfig.style.toUpperCase());
                plugin.getBossBarManager().createBossBar(player, bossBarConfig.title, color, style, bossBarConfig.duration);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid Boss Bar color or style in config.yml.");
            }
        }

        // 6. Check for persistent timed flight
        if (player.getPersistentDataContainer().has(PersistentKeys.FLY_EXPIRATION, PersistentDataType.LONG)) {
            long expirationTime = player.getPersistentDataContainer().get(PersistentKeys.FLY_EXPIRATION, PersistentDataType.LONG);
            long currentTime = System.currentTimeMillis();

            if (currentTime >= expirationTime) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);
            } else {
                long remainingTicks = (expirationTime - currentTime) / 50;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline() && player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);
                        plugin.getLocaleManager().sendMessage(player, "fly-expired");
                    }
                }, remainingTicks);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (inventoryManager.hasSavedInventory(player)) {
            inventoryManager.restorePlayerInventory(player);
        }
    }
}