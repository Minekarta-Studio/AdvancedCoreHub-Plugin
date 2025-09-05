package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import com.minekarta.advancedcorehub.util.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    // Handles items like Aspect of the End
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        // AOTE Logic
        if (container.has(PersistentKeys.AOTE_KEY, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            if (handleCooldown(player, "aote", "movement_items.aote.cooldown", 2)) {
                return;
            }
            // AOTE teleport logic
            Location targetLocation = player.getTargetBlock(null, plugin.getConfig().getInt("movement_items.aote.distance", 8)).getLocation();
            TeleportUtil.safeTeleport(player, targetLocation.add(0.5, 1, 0.5)); // Center on block and move up
        }
    }

    // Handles tagging projectiles like Tridents and Ender Bows
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player player = (Player) event.getEntity().getShooter();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.hasItemMeta()) return;

        PersistentDataContainer container = itemInHand.getItemMeta().getPersistentDataContainer();
        Projectile projectile = event.getEntity();

        if (projectile instanceof Trident && container.has(PersistentKeys.TRIDENT_KEY, PersistentDataType.BYTE)) {
            projectile.getPersistentDataContainer().set(PersistentKeys.TRIDENT_KEY, PersistentDataType.BYTE, (byte) 1);
        } else if (projectile instanceof Arrow && container.has(PersistentKeys.ENDERBOW_KEY, PersistentDataType.BYTE)) {
            projectile.getPersistentDataContainer().set(PersistentKeys.ENDERBOW_KEY, PersistentDataType.BYTE, (byte) 1);
        }
    }

    // Handles the result of projectiles hitting something
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (!(shooter instanceof Player)) return;
        Player player = (Player) shooter;

        PersistentDataContainer container = projectile.getPersistentDataContainer();

        // Trident Logic
        if (projectile instanceof Trident && container.has(PersistentKeys.TRIDENT_KEY, PersistentDataType.BYTE)) {
             if (handleCooldown(player, "trident", "movement_items.trident.cooldown", 5)) {
                return;
            }
            TeleportUtil.safeTeleport(player, projectile.getLocation());
            projectile.remove();
        }
        // Ender Bow Logic
        else if (projectile instanceof Arrow && container.has(PersistentKeys.ENDERBOW_KEY, PersistentDataType.BYTE)) {
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

        if (itemInHand == null || !itemInHand.hasItemMeta() || !itemInHand.getItemMeta().getPersistentDataContainer().has(PersistentKeys.GRAPPLING_HOOK_KEY, PersistentDataType.BYTE)) {
            return;
        }

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

    /**
     * A centralized method to handle cooldowns for movement items.
     *
     * @param player The player to check.
     * @param cooldownId A unique identifier for the cooldown (e.g., "trident").
     * @param configPath The path in config.yml to get the cooldown duration from.
     * @param defaultCooldown The default cooldown in seconds if the config path is not found.
     * @return true if the player is on cooldown, false otherwise.
     */
    private boolean handleCooldown(Player player, String cooldownId, String configPath, int defaultCooldown) {
        if (plugin.getCooldownManager().hasCooldown(player, cooldownId)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, cooldownId);
            plugin.getLocaleManager().sendMessage(player, "item-cooldown", remaining);
            return true;
        }
        int cooldownSeconds = plugin.getConfig().getInt(configPath, defaultCooldown);
        plugin.getCooldownManager().setCooldown(player, cooldownId, cooldownSeconds);
        return false;
    }
}
