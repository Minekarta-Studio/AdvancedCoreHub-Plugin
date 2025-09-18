package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class SecurityListener implements PluginMessageListener {

    private final AdvancedCoreHub plugin;

    public SecurityListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("security.anti_world_downloader");
        if (config == null || !config.getBoolean("enabled", true)) {
            return;
        }

        if (channel.equalsIgnoreCase("WDL|INIT") || channel.equalsIgnoreCase("worlddownloader:init")) {
            String kickMessage = config.getString("kick_message", "<red>Using a world downloader is not allowed on this server.");
            Component kickComponent = plugin.getLocaleManager().getComponentFromString(kickMessage, player);

            // Use a short delay to ensure the message is sent before the kick
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.kick(kickComponent);
            });

            plugin.getLogger().info("Kicked player " + player.getName() + " for attempting to use a world downloader.");
        }
    }
}
