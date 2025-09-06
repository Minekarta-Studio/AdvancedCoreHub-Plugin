package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import com.minekarta.advancedcorehub.util.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class MovementItemListener implements Listener {

    private final AdvancedCoreHub plugin;

    public MovementItemListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
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
            // Tag the projectile with the movement type so we can identify it on hit
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
            if (handleCooldown(player, "trident", "movement_items.trident.cooldown", 5)) {
                return;
            }
            TeleportUtil.safeTeleport(player, projectile.getLocation());
            projectile.remove();
        } else if ("enderbow".equalsIgnoreCase(movementType)) {
            if (handleCooldown(player, "enderbow", "movement_items.enderbow.cooldown", 3)) {
                return;
            }
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
            if (handleCooldown(player, "grappling_hook", "movement_items.grappling_hook.cooldown", 3)) {
                return;
            }

            Location hookLocation = event.getHook().getLocation();
            Location playerLocation = player.getLocation();

            Vector direction = hookLocation.toVector().subtract(playerLocation.toVector());
            double power = plugin.getConfig().getDouble("movement_items.grappling_hook.power", 1.8);
            player.setVelocity(direction.normalize().multiply(power));
        }
    }

    private boolean handleCooldown(Player player, String cooldownId, String configPath, int defaultCooldown) {
        if (plugin.getCooldownManager().hasCooldown(player, cooldownId)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, cooldownId);
            plugin.getLocaleManager().sendMessage(player, "item-cooldown", String.valueOf(remaining));
            return true;
        }
        int cooldownSeconds = plugin.getConfig().getInt(configPath, defaultCooldown);
        plugin.getCooldownManager().setCooldown(player, cooldownId, cooldownSeconds);
        return false;
    }
}
