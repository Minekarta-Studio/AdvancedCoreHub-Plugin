package com.minekarta.advancedcorehub.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TeleportUtil {

    private static final Set<Material> DANGEROUS_MATERIALS = new HashSet<>();

    static {
        DANGEROUS_MATERIALS.add(Material.LAVA);
        DANGEROUS_MATERIALS.add(Material.FIRE);
        DANGEROUS_MATERIALS.add(Material.MAGMA_BLOCK);
        DANGEROUS_MATERIALS.add(Material.CACTUS);
    }

    public static boolean isSafeLocation(Location location) {
        if (location == null) {
            return false;
        }

        Block feet = location.getBlock();
        Block head = feet.getRelative(0, 1, 0);
        Block ground = feet.getRelative(0, -1, 0);

        return isPassable(feet.getType()) && isPassable(head.getType()) &&
               ground.getType().isSolid() && !DANGEROUS_MATERIALS.contains(ground.getType());
    }

    private static boolean isPassable(Material material) {
        return material.isAir() || !material.isSolid();
    }

    public static void safeTeleport(Player player, Location location) {
        if (isSafeLocation(location)) {
            player.teleport(location);
        } else {
            // Optional: Send a message to the player that the location is not safe.
            // For now, we just won't teleport them.
        }
    }
}
