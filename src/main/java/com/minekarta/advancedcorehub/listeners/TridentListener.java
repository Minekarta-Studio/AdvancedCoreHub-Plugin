package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

public class TridentListener implements Listener {

    private final AdvancedCoreHub plugin;

    public TridentListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTridentLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player player = (Player) event.getEntity().getShooter();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.hasItemMeta()) {
            PersistentDataContainer container = itemInHand.getItemMeta().getPersistentDataContainer();
            if (container.has(PersistentKeys.TRIDENT_KEY, PersistentDataType.BYTE)) {
                // Tag the projectile so we can identify it on hit
                event.getEntity().getPersistentDataContainer().set(PersistentKeys.TRIDENT_KEY, PersistentDataType.BYTE, (byte)1);
            }
        }
    }

    @EventHandler
    public void onTridentHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident)) return;

        Trident trident = (Trident) event.getEntity();
        if (!trident.getPersistentDataContainer().has(PersistentKeys.TRIDENT_KEY, PersistentDataType.BYTE)) {
            return;
        }

        ProjectileSource shooter = trident.getShooter();
        if (!(shooter instanceof Player)) return;

        Player player = (Player) shooter;

        // Check for cooldown
        if (plugin.getCooldownManager().hasCooldown(player, "trident")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "trident");
            plugin.getLocaleManager().sendMessage(player, "item-cooldown", remaining);
            return;
        }

        // Teleport player
        Location hitLocation = trident.getLocation();
        // A proper safe teleport utility should be used here. For now, this is a basic teleport.
        player.teleport(hitLocation);
        trident.remove();

        // Return trident if configured
        if (plugin.getConfig().getBoolean("movement_items.trident.return_trident", true)) {
            ItemStack tridentItem = plugin.getItemsManager().getItem("trident_item"); // Assuming this is the ID in items.yml
            if (tridentItem != null) {
                player.getInventory().addItem(tridentItem);
            }
        }

        // Set cooldown
        int cooldownSeconds = plugin.getConfig().getInt("movement_items.trident.cooldown", 5);
        plugin.getCooldownManager().setCooldown(player, "trident", cooldownSeconds);
    }
}
