package com.minekarta.advancedcorehub;

import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.cosmetics.CosmeticsManager;
import com.minekarta.advancedcorehub.database.DataManager;
import com.minekarta.advancedcorehub.database.IDataManager;
import com.minekarta.advancedcorehub.manager.*;
import com.minekarta.advancedcorehub.player.IPlayerManager;
import com.minekarta.advancedcorehub.player.PlayerManager;
import com.minekarta.advancedcorehub.services.PluginSetupService;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public class AdvancedCoreHub extends JavaPlugin {

    private static AdvancedCoreHub instance;

    // Config
    private PluginConfig pluginConfig;

    // Services
    private PluginSetupService setupService;

    // Managers (using interfaces where available)
    private FileManager fileManager;
    private IDataManager dataManager;
    private IPlayerManager playerManager;
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

        // Initialize core services and managers first
        this.setupService = new PluginSetupService(this);
        this.fileManager = new FileManager(this);
        this.dataManager = new DataManager(this);

        // Asynchronously load all configurations and then initialize other components
        this.fileManager.setup().thenRun(() -> {
            // This block runs after all files are loaded.
            // We need to run the rest of the setup on the main server thread.
            getServer().getScheduler().runTask(this, this::initializePluginComponents);
        }).exceptionally(ex -> {
            getLogger().log(Level.SEVERE, "Failed to load initial configurations. Plugin will not enable correctly.", ex);
            return null;
        });
    }

    private void initializePluginComponents() {
        // This method is called on the main thread after configs are loaded
        getLogger().info("Configurations loaded. Initializing plugin components...");

        this.pluginConfig = new PluginConfig(this.fileManager.getConfig("config.yml"));

        // Initialize all other managers that depend on configs
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
        this.playerManager = new PlayerManager(this);

        // Initialize database
        this.dataManager.initDatabase();

        // Register everything using the setup service
        this.setupService.registerCommands();
        this.setupService.registerListeners();
        this.setupService.registerChannels();

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
        // Cancel tasks and clean up managers
        if (announcementsManager != null) announcementsManager.cancelTasks();
        if (bossBarManager != null) bossBarManager.cleanup();
        if (placeholderManager != null) placeholderManager.unregister();

        // Close database connections
        if (dataManager != null) {
            dataManager.closeDataSource();
        }

        // Unregister plugin channels
        if (setupService != null) {
            setupService.unregisterChannels();
        }

        getLogger().info("AdvancedCoreHub has been disabled.");
    }

    public void reloadPlugin() {
        getLogger().info("Reloading AdvancedCoreHub...");

        // 1. Cancel all tasks and clear state
        if (announcementsManager != null) announcementsManager.cancelTasks();
        if (bossBarManager != null) bossBarManager.cleanup();
        if (serverInfoManager != null) {
            BukkitTask updateTask = serverInfoManager.getUpdateTask();
            if (updateTask != null) updateTask.cancel();
        }
        setupService.unregisterListeners();
        setupService.unregisterChannels();

        // 2. Asynchronously reload configurations from disk
        this.fileManager.reloadAll().thenRun(() -> {
            // 3. Re-initialize managers with new config values on the main thread
            getServer().getScheduler().runTask(this, () -> {
                this.pluginConfig = new PluginConfig(this.fileManager.getConfig("config.yml"));
                this.localeManager.load();
                this.itemsManager.loadItems();
                this.menuManager.loadMenus();
                this.actionManager = new ActionManager(this);
                this.hubWorldManager.load();
                this.cosmeticsManager.loadCosmetics();
                this.announcementsManager.load();
                this.serverInfoManager.reload();

                // 4. Re-register components with the new configuration
                setupService.registerListeners();
                setupService.registerChannels();

                getLogger().info("Reload complete.");
            });
        }).exceptionally(ex -> {
            getLogger().log(Level.SEVERE, "An error occurred while reloading the plugin.", ex);
            return null;
        });
    }

    // --- Getters ---

    public static AdvancedCoreHub getInstance() {
        return instance;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public PluginSetupService getSetupService() {
        return setupService;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public IDataManager getDataManager() {
        return dataManager;
    }

    public IPlayerManager getPlayerManager() {
        return playerManager;
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

    public CommandManager getCommandManager() {
        return commandManager;
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