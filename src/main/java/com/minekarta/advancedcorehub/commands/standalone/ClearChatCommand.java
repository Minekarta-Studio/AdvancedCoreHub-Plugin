package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {

    private final AdvancedCoreHub plugin;

    public ClearChatCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(Permissions.CMD_CLEARCHAT)) {
            plugin.getLocaleManager().sendMessage(sender, "no-permission");
            return true;
        }

        for (int i = 0; i < 100; i++) {
            plugin.getServer().broadcastMessage(" ");
        }

        plugin.getServer().broadcast(plugin.getLocaleManager().getComponent("chat-clear-broadcast", sender.getName()));

        return true;
    }
}
