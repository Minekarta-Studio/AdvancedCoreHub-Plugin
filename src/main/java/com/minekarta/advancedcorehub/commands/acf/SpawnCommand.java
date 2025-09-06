package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
@CommandPermission(Permissions.CMD_SPAWN)
public class SpawnCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private LocaleManager localeManager;

    @Default
    public void onSpawn(Player player) {
        try {
            String worldName = plugin.getConfig().getString("spawn.world");
            if (worldName == null || plugin.getServer().getWorld(worldName) == null) {
                localeManager.sendMessage(player, "spawn-not-set");
                return;
            }
            World world = plugin.getServer().getWorld(worldName);
            double x = plugin.getConfig().getDouble("spawn.x");
            double y = plugin.getConfig().getDouble("spawn.y");
            double z = plugin.getConfig().getDouble("spawn.z");
            float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
            float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

            Location spawnLocation = new Location(world, x, y, z, yaw, pitch);
            player.teleport(spawnLocation);
            localeManager.sendMessage(player, "spawn-teleport-success");

        } catch (Exception e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not teleport player to spawn.", e);
            localeManager.sendMessage(player, "spawn-not-set");
        }
    }
}
