package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

        // Create a string of 100 blank lines
        StringBuilder blankLines = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            blankLines.append(" \n");
        }

        // Send the blank lines to all players without the bypass permission
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission(Permissions.CMD_CLEARCHAT_BYPASS)) {
                onlinePlayer.sendMessage(blankLines.toString());
            }
        }

        Player player = (sender instanceof Player) ? (Player) sender : null;
        plugin.getServer().broadcast(plugin.getLocaleManager().getComponent("chat-clear-broadcast", player, sender.getName()));

        return true;
    }
}
