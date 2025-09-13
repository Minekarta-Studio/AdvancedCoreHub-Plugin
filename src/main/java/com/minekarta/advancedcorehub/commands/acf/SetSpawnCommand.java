package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.HubWorldManager;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("setspawn|setlobby")
@CommandPermission(Permissions.CMD_SETSPAWN)
public class SetSpawnCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private LocaleManager localeManager;

    @Dependency
    private HubWorldManager hubWorldManager;

    @Default
    public void onSetSpawn(Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();

        // Set the spawn location in the config
        plugin.getConfig().set("spawn.world", world.getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.yaw", location.getYaw());
        plugin.getConfig().set("spawn.pitch", location.getPitch());

        // Add the world to the hub-worlds list if it's not already there
        List<String> hubWorlds = plugin.getConfig().getStringList("hub-worlds");
        if (!hubWorlds.contains(world.getName())) {
            hubWorlds.add(world.getName());
            plugin.getConfig().set("hub-worlds", hubWorlds);
        }

        // Save the config and reload relevant managers
        plugin.saveConfig();
        hubWorldManager.load(); // Reload the worlds list in the manager
        plugin.getInventoryManager().loadConfig(); // Reload inventory manager config

        localeManager.sendMessage(player, "spawn-set-success");
    }
}
