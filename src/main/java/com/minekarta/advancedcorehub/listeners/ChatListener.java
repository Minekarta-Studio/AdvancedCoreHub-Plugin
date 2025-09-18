package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class ChatListener implements Listener {

    private final AdvancedCoreHub plugin;

    public ChatListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Handle Chat Lock
        if (plugin.getChatManager().isChatLocked()) {
            if (!player.hasPermission(Permissions.BYPASS_CHAT_LOCK)) {
                event.setCancelled(true);
                plugin.getLocaleManager().sendMessage(player, "chat-is-locked");
                return; // Stop further processing if chat is locked
            }
        }

        // Handle Anti-Swear
        ConfigurationSection antiSwearConfig = plugin.getConfig().getConfigurationSection("chat_and_commands.anti_swear");
        if (antiSwearConfig != null && antiSwearConfig.getBoolean("enabled", true)) {
            if (!player.hasPermission("advancedcorehub.bypass.filter")) {
                String message = PlainTextComponentSerializer.plainText().serialize(event.message()).toLowerCase();
                List<String> blockedWords = antiSwearConfig.getStringList("blocked_words");

                for (String blockedWord : blockedWords) {
                    if (message.contains(blockedWord.toLowerCase())) {
                        event.setCancelled(true);
                        String warningMessage = antiSwearConfig.getString("warning_message", "<red>Please watch your language!</red>");
                        plugin.getLocaleManager().sendMessage(player, "prefix", warningMessage);
                        return; // Stop after finding one blocked word
                    }
                }
            }
        }
    }
}
