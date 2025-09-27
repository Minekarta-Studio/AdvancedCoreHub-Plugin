package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
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
            PluginConfig.SpawnConfig spawnConfig = plugin.getPluginConfig().spawn;
            World world = plugin.getServer().getWorld(spawnConfig.world);

            if (world == null) {
                localeManager.sendMessage(player, "spawn-not-set");
                return;
            }

            Location spawnLocation = new Location(world, spawnConfig.x, spawnConfig.y, spawnConfig.z, spawnConfig.yaw, spawnConfig.pitch);
            player.teleport(spawnLocation);
            localeManager.sendMessage(player, "spawn-teleport-success");

        } catch (Exception e) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE, "Could not teleport player to spawn.", e);
            localeManager.sendMessage(player, "spawn-not-set");
        }
    }
}