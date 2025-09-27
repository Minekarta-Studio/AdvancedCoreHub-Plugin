package com.minekarta.advancedcorehub;

import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.cosmetics.CosmeticsManager;
import com.minekarta.advancedcorehub.cosmetics.PlayerMoveListener;
import com.minekarta.advancedcorehub.listeners.*;
import com.minekarta.advancedcorehub.manager.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class AdvancedCoreHub extends JavaPlugin {

    private static AdvancedCoreHub instance;

    // Config
    private PluginConfig pluginConfig;

    // Managers
    private FileManager fileManager;
    private LocaleManager localeManager;
    private ItemsManager itemsManager;
    private ActionManager actionManager;
    private CooldownManager cooldownManager;
    private HubWorldManager hubWorldManager;
    private AnnouncementsManager announcementsManager;
    private BossBarManager bossBarManager;
    private MenuManager menuManager;
    private ChatManager chatManager;
    private CommandManager commandManager;
    private InventoryManager inventoryManager;
    private ServerInfoManager serverInfoManager;
    private CosmeticsManager cosmeticsManager;
    private PlaceholderManager placeholderManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Enabling AdvancedCoreHub v" + getDescription().getVersion());

        // Initialize managers
        this.fileManager = new FileManager(this);
        this.fileManager.setup(); // Must be first
        this.pluginConfig = new PluginConfig(this.fileManager.getConfig("config.yml"));

        this.inventoryManager = new InventoryManager(this);
        this.localeManager = new LocaleManager(this, this.fileManager);
        this.localeManager.load();
        this.itemsManager = new ItemsManager(this);
        this.itemsManager.loadItems();
        this.menuManager = new MenuManager(this);
        this.menuManager.loadMenus();
        this.actionManager = new ActionManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.hubWorldManager = new HubWorldManager(this);
        this.announcementsManager = new AnnouncementsManager(this);
        this.announcementsManager.load();
        this.bossBarManager = new BossBarManager(this);
        this.chatManager = new ChatManager();
        this.commandManager = new CommandManager(this);
        this.serverInfoManager = new ServerInfoManager(this);
        this.cosmeticsManager = new CosmeticsManager(this);
        this.cosmeticsManager.loadCosmetics();

        // Load other components
        registerCommands();
        registerListeners();
        registerChannels();

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderManager = new PlaceholderManager(this);
            this.placeholderManager.register();
            getLogger().info("Registered custom placeholders with PlaceholderAPI.");
        }

        getLogger().info("AdvancedCoreHub has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling AdvancedCoreHub.");
        if (announcementsManager != null) {
            announcementsManager.cancelTasks();
        }
        if (bossBarManager != null) {
            bossBarManager.cleanup();
        }
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        getLogger().info("AdvancedCoreHub has been disabled.");
    }

    public void reloadPlugin() {
        getLogger().info("Reloading AdvancedCoreHub...");
        try {
            // 1. Cancel all tasks and clear state
            if (announcementsManager != null) announcementsManager.cancelTasks();
            if (bossBarManager != null) bossBarManager.cleanup();
            if (serverInfoManager != null && serverInfoManager.getUpdateTask() != null) serverInfoManager.getUpdateTask().cancel();
            unregisterListeners();
            unregisterChannels();

            // 2. Reload configurations from disk
            this.fileManager.reloadAll();
            this.pluginConfig = new PluginConfig(this.fileManager.getConfig("config.yml"));

            // 3. Reload managers with new config values
            this.localeManager.load();
            this.itemsManager.loadItems();
            this.menuManager.loadMenus();
            this.actionManager = new ActionManager(this);
            this.hubWorldManager.load();
            this.cosmeticsManager.loadCosmetics(); // Reload cosmetics from config
            this.announcementsManager.load();
            this.serverInfoManager.reload(); // Must be after menuManager

            // 4. Re-register components with the new configuration
            registerListeners();
            registerChannels();

            getLogger().info("Reload complete.");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while reloading the plugin.", e);
        }
    }

    private void registerCommands() {
        commandManager.registerCommands();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldEventListeners(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementItemListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemActionListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        if (getPluginConfig().doubleJump.enabled) {
            getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
            getLogger().info("Double Jump feature enabled.");
        }
        if (getPluginConfig().chatProtection.antiSwear.enabled || getPluginConfig().chatProtection.commandBlocker.enabled) {
            getServer().getPluginManager().registerEvents(new ChatProtectionListener(this), this);
            getLogger().info("Chat Protection feature enabled.");
        }
    }

    private void unregisterListeners() {
        org.bukkit.event.HandlerList.unregisterAll(this);
    }

    private void registerChannels() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this.serverInfoManager);

        if (getPluginConfig().antiWorldDownloader.enabled) {
            AntiWorldDownloaderListener wdlListener = new AntiWorldDownloaderListener(this);
            getServer().getMessenger().registerIncomingPluginChannel(this, "wdl:init", wdlListener);
            getServer().getMessenger().registerIncomingPluginChannel(this, "wdl:request", wdlListener);
            getServer().getMessenger().registerIncomingPluginChannel(this, "worlddownloader:init", wdlListener);
            getLogger().info("Anti-World Downloader feature enabled.");
        }
    }

    private void unregisterChannels() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    // --- Getters ---

    public static AdvancedCoreHub getInstance() {
        return instance;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public ItemsManager getItemsManager() {
        return itemsManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public HubWorldManager getHubWorldManager() {
        return hubWorldManager;
    }

    public AnnouncementsManager getAnnouncementsManager() {
        return announcementsManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ServerInfoManager getServerInfoManager() {
        return serverInfoManager;
    }

    public CosmeticsManager getCosmeticsManager() {
        return cosmeticsManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }
}