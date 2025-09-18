package com.minekarta.advancedcorehub.commands;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomCommand extends Command {

    private final AdvancedCoreHub plugin;
    private final List<Map<?, ?>> actions;
    private final String permission;

    public CustomCommand(AdvancedCoreHub plugin, String name, String permission, List<String> aliases, List<Map<?, ?>> actions) {
        super(name);
        this.plugin = plugin;
        this.actions = actions;
        this.permission = permission;
        if (aliases != null) {
            this.setAliases(aliases);
        }
        if (permission != null && !permission.isEmpty()) {
            this.setPermission(permission);
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (permission != null && !permission.isEmpty() && !player.hasPermission(permission)) {
            plugin.getLocaleManager().sendMessage(player, "no-permission");
            return true;
        }

        plugin.getActionManager().executeMapActions(player, actions, args);
        return true;
    }
}
