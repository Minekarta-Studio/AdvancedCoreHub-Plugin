package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

    private final AdvancedCoreHub plugin;

    public CommandListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        ConfigurationSection commandBlockerConfig = plugin.getConfig().getConfigurationSection("chat_and_commands.command_blocker");

        if (commandBlockerConfig == null || !commandBlockerConfig.getBoolean("enabled", true)) {
            return;
        }

        if (player.hasPermission("advancedcorehub.bypass.filter")) {
            return;
        }

        String command = event.getMessage().substring(1).toLowerCase().split(" ")[0];
        List<String> blockedCommands = commandBlockerConfig.getStringList("blocked_commands");

        if (blockedCommands.contains(command)) {
            event.setCancelled(true);
            String warningMessage = commandBlockerConfig.getString("warning_message", "<red>You are not allowed to use that command.</red>");
            plugin.getLocaleManager().sendMessage(player, "prefix", warningMessage);
        }
    }
}
