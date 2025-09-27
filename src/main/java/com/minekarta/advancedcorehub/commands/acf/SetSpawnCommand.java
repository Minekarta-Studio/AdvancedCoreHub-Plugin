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

import java.util.ArrayList;
import java.util.List;

@CommandAlias("setspawn|setlobby")
@CommandPermission(Permissions.CMD_SETSPAWN)
public class SetSpawnCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private LocaleManager localeManager;

    @Default
    public void onSetSpawn(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();

        // Direct write operations are acceptable for a command that sets configuration.
        plugin.getConfig().set("spawn.world", world.getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.yaw", location.getYaw());
        plugin.getConfig().set("spawn.pitch", location.getPitch());

        // Read from the type-safe config, then write back if modified.
        List<String> hubWorlds = new ArrayList<>(plugin.getPluginConfig().getHubWorlds());
        if (!hubWorlds.contains(world.getName())) {
            hubWorlds.add(world.getName());
            plugin.getConfig().set("hub-worlds", hubWorlds);
        }

        plugin.saveConfig();
        plugin.reloadPlugin(); // Reload all configs and managers to ensure consistency

        localeManager.sendMessage(player, "spawn-set-success");
    }
}