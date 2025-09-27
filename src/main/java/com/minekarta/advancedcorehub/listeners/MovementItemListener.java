package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import com.minekarta.advancedcorehub.util.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class MovementItemListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final PluginConfig.MovementItemsConfig config;

    public MovementItemListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().movementItems;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.hasItemMeta()) return;

        PersistentDataContainer itemContainer = itemInHand.getItemMeta().getPersistentDataContainer();
        if (!itemContainer.has(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING)) return;

        String movementType = itemContainer.get(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING);
        Projectile projectile = event.getEntity();

        if ((projectile instanceof Trident && "trident".equalsIgnoreCase(movementType)) ||
            (projectile instanceof Arrow && "enderbow".equalsIgnoreCase(movementType))) {
            projectile.getPersistentDataContainer().set(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING, movementType);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) return;

        PersistentDataContainer projectileContainer = projectile.getPersistentDataContainer();
        if (!projectileContainer.has(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING)) return;

        String movementType = projectileContainer.get(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING);

        if ("trident".equalsIgnoreCase(movementType)) {
            if (handleCooldown(player, "trident", config.trident.cooldown)) return;
            TeleportUtil.safeTeleport(player, projectile.getLocation());
            if (config.trident.returnTrident) {
                // Logic to return trident is handled by giving a new item, as tracking the original is complex.
                // This would typically involve a custom item manager.
            } else {
                projectile.remove();
            }
        } else if ("enderbow".equalsIgnoreCase(movementType)) {
            if (handleCooldown(player, "enderbow", config.enderbow.cooldown)) return;
            TeleportUtil.safeTeleport(player, projectile.getLocation());
            projectile.remove();
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null || !itemInHand.hasItemMeta()) return;

        PersistentDataContainer container = itemInHand.getItemMeta().getPersistentDataContainer();
        if (!container.has(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING)) return;

        String movementType = container.get(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING);
        if (!"grappling_hook".equalsIgnoreCase(movementType)) return;

        if (event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            if (handleCooldown(player, "grappling_hook", config.grapplingHook.cooldown)) return;

            Location hookLocation = event.getHook().getLocation();
            Location playerLocation = player.getLocation();

            Vector direction = hookLocation.toVector().subtract(playerLocation.toVector());
            player.setVelocity(direction.normalize().multiply(config.grapplingHook.power));
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!event.isFlying()) return;

        PlayerInventory inventory = player.getInventory();
        ItemStack chestplate = inventory.getChestplate();

        if (chestplate == null || !chestplate.hasItemMeta()) return;

        PersistentDataContainer container = chestplate.getItemMeta().getPersistentDataContainer();
        if (!container.has(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING)) return;

        String movementType = container.get(PersistentKeys.MOVEMENT_TYPE_KEY, PersistentDataType.STRING);
        if (!"custom_elytra".equalsIgnoreCase(movementType)) return;

        if (handleCooldown(player, "custom_elytra", config.customElytra.cooldown)) {
            event.setCancelled(true);
            return;
        }

        Vector direction = player.getLocation().getDirection().multiply(config.customElytra.speedBoost);
        player.setVelocity(player.getVelocity().add(direction));
    }

    private boolean handleCooldown(Player player, String cooldownId, int cooldownSeconds) {
        if (plugin.getCooldownManager().hasCooldown(player, cooldownId)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, cooldownId);
            plugin.getLocaleManager().sendMessage(player, "item-cooldown", String.valueOf(remaining));
            return true;
        }
        plugin.getCooldownManager().setCooldown(player, cooldownId, cooldownSeconds);
        return false;
    }
}