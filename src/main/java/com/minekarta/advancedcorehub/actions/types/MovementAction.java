package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.util.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import org.bukkit.Material;

public class MovementAction implements Action {

    private final AdvancedCoreHub plugin;

    public MovementAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
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
            // Other movement types can be added here
            default:
                plugin.getLogger().warning("[MovementAction] Unknown movement type: " + movementType);
        }
    }

    private void handleAote(Player player) {
        if (handleCooldown(player, "aote", "movement_items.aote.cooldown", 2)) {
            return;
        }

        int distance = plugin.getConfig().getInt("movement_items.aote.distance", 8);
        Block targetBlock = player.getTargetBlock((Set<Material>) null, distance);

        Location targetLocation = targetBlock.getLocation();
        // Set player rotation to look forward after teleport
        targetLocation.setDirection(player.getLocation().getDirection());

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
