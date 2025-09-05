package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class RodListener implements Listener {

    private final AdvancedCoreHub plugin;

    public RodListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.hasItemMeta() || !itemInHand.getItemMeta().getPersistentDataContainer().has(PersistentKeys.GRAPPLING_HOOK_KEY, PersistentDataType.BYTE)) {
            return;
        }

        if (event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            // Check world
            if (plugin.getDisabledWorlds().isDisabled(player.getWorld().getName())) {
                return;
            }

            // Check cooldown
            if (plugin.getCooldownManager().hasCooldown(player, "grappling_hook")) {
                long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "grappling_hook");
                plugin.getLocaleManager().sendMessage(player, "item-cooldown", remaining);
                return;
            }

            Location hookLocation = event.getHook().getLocation();
            Location playerLocation = player.getLocation();

            Vector direction = hookLocation.toVector().subtract(playerLocation.toVector());
            double power = plugin.getConfig().getDouble("movement_items.grappling_hook.power", 1.5);
            player.setVelocity(direction.normalize().multiply(power));

            // Set cooldown
            int cooldownSeconds = plugin.getConfig().getInt("movement_items.grappling_hook.cooldown", 3);
            plugin.getCooldownManager().setCooldown(player, "grappling_hook", cooldownSeconds);
        }
    }
}
