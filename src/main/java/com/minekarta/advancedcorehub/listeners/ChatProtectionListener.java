package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ChatProtectionListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final List<String> blockedWords;
    private final List<String> blockedCommands;

    public ChatProtectionListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        // Load and lowercase the lists for case-insensitive matching
        this.blockedWords = plugin.getConfig().getStringList("chat_protection.anti_swear.blocked_words")
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        this.blockedCommands = plugin.getConfig().getStringList("chat_protection.command_blocker.blocked_commands")
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("chat_protection.anti_swear.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("advancedcorehub.bypass.antiswear")) {
            return;
        }

        String message = event.getMessage().toLowerCase();
        for (String blockedWord : blockedWords) {
            if (message.contains(blockedWord)) {
                event.setCancelled(true);
                plugin.getLocaleManager().sendMessage(player, "chat-protection-swear-warning");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getConfig().getBoolean("chat_protection.command_blocker.enabled", false)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission("advancedcorehub.bypass.commandblocker")) {
            return;
        }

        String command = event.getMessage().toLowerCase().substring(1); // remove leading '/'
        // Handle commands with namespace, e.g. /minecraft:me
        if (command.contains(":")) {
            command = command.split(":")[1];
        }
        final String finalCommand = command;

        for (String blockedCommand : blockedCommands) {
            if (finalCommand.startsWith(blockedCommand)) {
                event.setCancelled(true);
                plugin.getLocaleManager().sendMessage(player, "chat-protection-command-warning");
                return;
            }
        }
    }
}
