package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class AntiWorldDownloaderListener implements PluginMessageListener {

    private final AdvancedCoreHub plugin;

    public AntiWorldDownloaderListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        // The content of the message doesn't matter, just the fact that we received it.
        // We run this on the main server thread to ensure thread safety with the Bukkit API.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String kickMessage = plugin.getLocaleManager().getString("anti_wdl_kick_message", player);
            player.kickPlayer(kickMessage);
            plugin.getLogger().info("Kicked player " + player.getName() + " for using a World Downloader mod (channel: " + channel + ").");
        });
    }
}
