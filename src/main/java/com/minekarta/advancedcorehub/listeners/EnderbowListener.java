package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

public class EnderbowListener implements Listener {

    private final AdvancedCoreHub plugin;

    public EnderbowListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getProjectile() instanceof Arrow)) return;

        ItemStack bow = event.getBow();
        if (bow == null || !bow.hasItemMeta()) return;

        PersistentDataContainer container = bow.getItemMeta().getPersistentDataContainer();
        if (container.has(PersistentKeys.ENDERBOW_KEY, PersistentDataType.BYTE)) {
            // Tag the arrow
            event.getProjectile().getPersistentDataContainer().set(PersistentKeys.ENDERBOW_KEY, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getEntity();
        if (!arrow.getPersistentDataContainer().has(PersistentKeys.ENDERBOW_KEY, PersistentDataType.BYTE)) {
            return;
        }

        ProjectileSource shooter = arrow.getShooter();
        if (!(shooter instanceof Player)) return;

        Player player = (Player) shooter;
        arrow.remove(); // Remove arrow on hit

        // Check world
        if (plugin.getDisabledWorlds().isDisabled(player.getWorld().getName())) return;

        // Check cooldown
        if (plugin.getCooldownManager().hasCooldown(player, "enderbow")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "enderbow");
            plugin.getLocaleManager().sendMessage(player, "item-cooldown", remaining);
            return;
        }

        Location hitLocation = arrow.getLocation();
        player.teleport(hitLocation);

        // Set cooldown
        int cooldownSeconds = plugin.getConfig().getInt("movement_items.enderbow.cooldown", 3);
        plugin.getCooldownManager().setCooldown(player, "enderbow", cooldownSeconds);
    }
}
