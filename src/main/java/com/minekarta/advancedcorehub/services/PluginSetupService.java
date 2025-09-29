package com.minekarta.advancedcorehub.services;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.features.antiworlddownloader.AntiWorldDownloaderListener;
import com.minekarta.advancedcorehub.cosmetics.PlayerMoveListener;
import com.minekarta.advancedcorehub.features.chatprotection.ChatProtectionListener;
import com.minekarta.advancedcorehub.features.doublejump.DoubleJumpListener;
import com.minekarta.advancedcorehub.listeners.*;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Service class responsible for handling the registration and unregistration
 * of plugin components like listeners and channels.
 */
public class PluginSetupService {

    private final AdvancedCoreHub plugin;

    public PluginSetupService(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommandManager().registerCommands();
    }

    public void registerListeners() {
        PluginManager pm = plugin.getServer().getPluginManager();

        // Core Listeners
        pm.registerEvents(new PlayerConnectionListener(plugin), plugin);
        pm.registerEvents(new WorldEventListeners(plugin), plugin);
        pm.registerEvents(new WorldListener(plugin), plugin);
        pm.registerEvents(new MenuListener(plugin), plugin);
        pm.registerEvents(new ChatListener(plugin), plugin);
        pm.registerEvents(new MovementItemListener(plugin), plugin);
        pm.registerEvents(new ItemActionListener(plugin), plugin);
        pm.registerEvents(new ItemProtectionListener(plugin), plugin);
        pm.registerEvents(new PlayerMoveListener(plugin), plugin);
        pm.registerEvents(plugin.getPlayerManager(), plugin);

        // Feature Listeners
        if (plugin.getPluginConfig().doubleJump.enabled) {
            pm.registerEvents(new DoubleJumpListener(plugin), plugin);
            plugin.getLogger().info("Double Jump feature enabled.");
        }
        if (plugin.getPluginConfig().chatProtection.antiSwear.enabled || plugin.getPluginConfig().chatProtection.commandBlocker.enabled) {
            pm.registerEvents(new ChatProtectionListener(plugin), plugin);
            plugin.getLogger().info("Chat Protection feature enabled.");
        }
    }

    public void unregisterListeners() {
        HandlerList.unregisterAll(plugin);
    }

    public void registerChannels() {
        Messenger messenger = plugin.getServer().getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
        messenger.registerIncomingPluginChannel(plugin, "BungeeCord", plugin.getServerInfoManager());

        if (plugin.getPluginConfig().antiWorldDownloader.enabled) {
            PluginMessageListener wdlListener = new AntiWorldDownloaderListener(plugin);
            messenger.registerIncomingPluginChannel(plugin, "wdl:init", wdlListener);
            messenger.registerIncomingPluginChannel(plugin, "wdl:request", wdlListener);
            messenger.registerIncomingPluginChannel(plugin, "worlddownloader:init", wdlListener);
            plugin.getLogger().info("Anti-World Downloader feature enabled.");
        }
    }

    public void unregisterChannels() {
        Messenger messenger = plugin.getServer().getMessenger();
        messenger.unregisterIncomingPluginChannel(plugin);
        messenger.unregisterOutgoingPluginChannel(plugin);
    }
}