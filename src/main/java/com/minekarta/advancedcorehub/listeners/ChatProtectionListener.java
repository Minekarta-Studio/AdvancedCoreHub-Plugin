package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatProtectionListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final PluginConfig.ChatProtectionConfig config;

    public ChatProtectionListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().chatProtection;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasPermission(Permissions.BYPASS_CHAT_PROTECTION)) {
            return;
        }

        // Anti-Swear
        if (config.antiSwear.enabled) {
            String message = event.getMessage().toLowerCase();
            for (String blockedWord : config.antiSwear.blockedWords) {
                if (message.contains(blockedWord.toLowerCase())) {
                    event.setCancelled(true);
                    plugin.getLocaleManager().sendMessage(event.getPlayer(), "chat_protection.anti_swear.message");
                    return; // No need to check for command blocking if a swear word is found
                }
            }
        }

        // Command Blocker
        if (config.commandBlocker.enabled) {
            String message = event.getMessage().toLowerCase().trim();
            if (message.startsWith("/")) {
                String command = message.split(" ")[0].substring(1);
                // Check for variants like 'bukkit:plugins'
                if (command.contains(":")) {
                    command = command.split(":")[1];
                }

                for (String blockedCommand : config.commandBlocker.blockedCommands) {
                    if (command.equalsIgnoreCase(blockedCommand)) {
                        event.setCancelled(true);
                        plugin.getLocaleManager().sendMessage(event.getPlayer(), "chat_protection.command_blocker.message");
                        return;
                    }
                }
            }
        }
    }
}