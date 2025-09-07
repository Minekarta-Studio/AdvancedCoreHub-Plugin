package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private final AdvancedCoreHub plugin;

    public PlayerJoinListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 1. Handle Join Message (can be customized or disabled)
        event.joinMessage(plugin.getLocaleManager().getComponent("join-message", player));

        // 2. Execute actions_on_join from config.yml
        List<String> joinActions = plugin.getConfig().getStringList("actions_on_join");
        if (!joinActions.isEmpty()) {
            plugin.getActionManager().executeActions(player, joinActions);
        }

        // 3. Handle Hub Inventory
        if (player.getPersistentDataContainer().has(PersistentKeys.INVENTORY_CLEARED, PersistentDataType.BYTE)) {
            player.getPersistentDataContainer().remove(PersistentKeys.INVENTORY_CLEARED);
        } else if (plugin.getInventoryManager().isHubWorld(player.getWorld().getName())) {
            plugin.getInventoryManager().setupHubInventory(player);
        }

        // 4. Handle Boss Bar on join
        if (plugin.getConfig().getBoolean("bossbar.show_on_join", false)) {
            // Logic to be added when BossBar config is defined
        }

        // 5. Check for persistent timed flight
        if (player.getPersistentDataContainer().has(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION, org.bukkit.persistence.PersistentDataType.LONG)) {
            long expirationTime = player.getPersistentDataContainer().get(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION, org.bukkit.persistence.PersistentDataType.LONG);
            long currentTime = System.currentTimeMillis();

            if (currentTime >= expirationTime) {
                // Flight has expired while offline
                player.setAllowFlight(false);
                player.setFlying(false);
                player.getPersistentDataContainer().remove(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION);
            } else {
                // Flight is still active, restart the timer
                long remainingTicks = (expirationTime - currentTime) / 50;
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline() && player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.getPersistentDataContainer().remove(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION);
                        plugin.getLocaleManager().sendMessage(player, "fly-expired");
                    }
                }, remainingTicks);
            }
        }
    }
}
