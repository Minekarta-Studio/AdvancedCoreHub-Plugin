package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.util.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class MovementAction implements Action {

    private final AdvancedCoreHub plugin;
    private final PluginConfig.MovementItemsConfig config;

    public MovementAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().movementItems;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        @SuppressWarnings("unchecked")
        List<String> args = (List<String>) data;
        if (args.size() < 2) return;

        String movementType = args.get(1).trim().toLowerCase();

        switch (movementType) {
            case "aote":
                handleAote(player);
                break;
            default:
                plugin.getLogger().warning("[MovementAction] Unknown movement type: " + movementType);
        }
    }

    private void handleAote(Player player) {
        if (handleCooldown(player, "aote", config.aote.cooldown)) {
            return;
        }

        Block targetBlock = player.getTargetBlock((Set<Material>) null, config.aote.distance);
        Location targetLocation = targetBlock.getLocation();
        targetLocation.setDirection(player.getLocation().getDirection());

        TeleportUtil.safeTeleport(player, targetLocation.add(0.5, 1, 0.5));
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