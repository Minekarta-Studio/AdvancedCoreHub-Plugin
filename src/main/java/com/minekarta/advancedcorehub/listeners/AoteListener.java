package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class AoteListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final Set<Material> transparentBlocks = new HashSet<>();

    public AoteListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        // Add materials that don't cause suffocation
        transparentBlocks.add(Material.AIR);
        transparentBlocks.add(Material.CAVE_AIR);
        transparentBlocks.add(Material.VOID_AIR);
        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.LAVA);
        // Add more as needed: signs, grass, etc.
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.hasItemMeta() || !itemInHand.getItemMeta().getPersistentDataContainer().has(PersistentKeys.AOTE_KEY, PersistentDataType.BYTE)) {
            return;
        }

        event.setCancelled(true);

        // Check world
        if (plugin.getDisabledWorlds().isDisabled(player.getWorld().getName())) return;

        // Check cooldown
        if (plugin.getCooldownManager().hasCooldown(player, "aote")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "aote");
            plugin.getLocaleManager().sendMessage(player, "item-cooldown", remaining);
            return;
        }

        int distance = plugin.getConfig().getInt("movement_items.aote.distance", 8);
        Vector direction = player.getEyeLocation().getDirection().normalize();
        Location targetLocation = player.getEyeLocation().add(direction.multiply(distance));

        // Simple safe teleport: find the last safe block
        for (int i = 0; i < distance; i++) {
            Location loc = player.getEyeLocation().add(direction.clone().multiply(i));
            if (!transparentBlocks.contains(loc.getBlock().getType())) {
                targetLocation = loc.subtract(direction); // Go back one block
                break;
            }
        }

        // Ensure the player has room
        Block blockAbove = targetLocation.clone().add(0, 1, 0).getBlock();
        if (!transparentBlocks.contains(targetLocation.getBlock().getType()) || !transparentBlocks.contains(blockAbove.getType())) {
            plugin.getLocaleManager().sendMessage(player, "aote-no-space");
             return;
        }

        player.teleport(targetLocation);

        // Set cooldown
        int cooldownSeconds = plugin.getConfig().getInt("movement_items.aote.cooldown", 2);
        plugin.getCooldownManager().setCooldown(player, "aote", cooldownSeconds);
    }
}
