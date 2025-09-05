package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    private final AdvancedCoreHub plugin;

    public SpawnCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getLocaleManager().sendMessage(sender, "players-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Permissions.CMD_SPAWN)) {
            plugin.getLocaleManager().sendMessage(player, "no-permission");
            return true;
        }

        try {
            String worldName = plugin.getConfig().getString("spawn.world");
            if (worldName == null || plugin.getServer().getWorld(worldName) == null) {
                plugin.getLocaleManager().sendMessage(player, "spawn-not-set");
                return true;
            }
            org.bukkit.World world = plugin.getServer().getWorld(worldName);
            double x = plugin.getConfig().getDouble("spawn.x");
            double y = plugin.getConfig().getDouble("spawn.y");
            double z = plugin.getConfig().getDouble("spawn.z");
            float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
            float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

            org.bukkit.Location spawnLocation = new org.bukkit.Location(world, x, y, z, yaw, pitch);
            player.teleport(spawnLocation);
            plugin.getLocaleManager().sendMessage(player, "spawn-teleport-success");

        } catch (Exception e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not teleport player to spawn.", e);
            plugin.getLocaleManager().sendMessage(player, "spawn-not-set");
        }

        return true;
    }
}
