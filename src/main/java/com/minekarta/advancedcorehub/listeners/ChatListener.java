package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final AdvancedCoreHub plugin;

    public ChatListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.getChatManager().isChatLocked()) {
            if (!player.hasPermission(Permissions.BYPASS_CHAT_LOCK)) {
                event.setCancelled(true);
                plugin.getLocaleManager().sendMessage(player, "chat-is-locked");
            }
        }
    }
}
