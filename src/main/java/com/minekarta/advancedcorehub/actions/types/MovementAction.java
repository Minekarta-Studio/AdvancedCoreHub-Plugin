package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.util.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MovementAction implements Action {

    private final AdvancedCoreHub plugin;

    public MovementAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || !((String) data).equalsIgnoreCase("aote")) {
            return;
        }

        if (handleCooldown(player, "aote", "movement_items.aote.cooldown", 2)) {
            return;
        }
        Location targetLocation = player.getTargetBlock(null, plugin.getConfig().getInt("movement_items.aote.distance", 8)).getLocation();
        TeleportUtil.safeTeleport(player, targetLocation.add(0.5, 1, 0.5));
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
