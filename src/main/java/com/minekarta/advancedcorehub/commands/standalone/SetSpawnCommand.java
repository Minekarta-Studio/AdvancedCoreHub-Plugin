package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {

    private final AdvancedCoreHub plugin;

    public SetSpawnCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getLocaleManager().sendMessage(sender, "players-only");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(Permissions.CMD_SETSPAWN)) {
            plugin.getLocaleManager().sendMessage(player, "no-permission");
            return true;
        }

        // Logic to save spawn location to config
        // This will be implemented with a SpawnManager later.
        plugin.getConfig().set("spawn.world", player.getLocation().getWorld().getName());
        plugin.getConfig().set("spawn.x", player.getLocation().getX());
        plugin.getConfig().set("spawn.y", player.getLocation().getY());
        plugin.getConfig().set("spawn.z", player.getLocation().getZ());
        plugin.getConfig().set("spawn.yaw", player.getLocation().getYaw());
        plugin.getConfig().set("spawn.pitch", player.getLocation().getPitch());
        plugin.saveConfig();

        plugin.getLocaleManager().sendMessage(player, "spawn-set-success");

        return true;
    }
}
