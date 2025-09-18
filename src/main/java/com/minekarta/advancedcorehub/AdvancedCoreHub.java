package com.minekarta.advancedcorehub;

import com.minekarta.advancedcorehub.cosmetics.CosmeticsManager;
import com.minekarta.advancedcorehub.cosmetics.PlayerMoveListener;
import com.minekarta.advancedcorehub.listeners.*;
import com.minekarta.advancedcorehub.manager.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class AdvancedCoreHub extends JavaPlugin {

    private static AdvancedCoreHub instance;

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
    private GadgetManager gadgetManager;
    private PlayerVisibilityManager playerVisibilityManager;


    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Enabling AdvancedCoreHub v" + getDescription().getVersion());

        // Initialize managers
        this.fileManager = new FileManager(this);
        this.fileManager.setup(); // Must be first

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
        this.gadgetManager = new GadgetManager(this);
        this.gadgetManager.loadGadgets();
        this.playerVisibilityManager = new PlayerVisibilityManager(this);


        // Load other components
        registerCommands();
        registerListeners();
        registerChannels();

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
            this.fileManager.reloadAll();
            this.localeManager.load();
            this.itemsManager.loadItems();
            this.gadgetManager.loadGadgets();
            this.menuManager.loadMenus();
            this.actionManager = new ActionManager(this); // Re-register actions
            this.hubWorldManager.load();
            this.announcementsManager.load();
            this.bossBarManager.cleanup();

            getLogger().info("Reload complete.");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while reloading the plugin.", e);
        }
    }

    private void registerCommands() {
        commandManager.registerCommands();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldEventListeners(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementItemListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemActionListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        // Register Double Jump Listener if enabled
        if (getConfig().getBoolean("double_jump.enabled", false)) {
            getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
            getLogger().info("Double Jump feature enabled.");
        }

        // Register Chat Protection Listener if either feature is enabled
        if (getConfig().getBoolean("chat_protection.anti_swear.enabled", false) ||
            getConfig().getBoolean("chat_protection.command_blocker.enabled", false)) {
            getServer().getPluginManager().registerEvents(new ChatProtectionListener(this), this);
            getLogger().info("Chat Protection feature enabled.");
        }
    }

    private void registerChannels() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this.serverInfoManager);

        // Register Anti-World Downloader channels if enabled
        if (getConfig().getBoolean("anti_world_downloader.enabled", true)) {
            AntiWorldDownloaderListener wdlListener = new AntiWorldDownloaderListener(this);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", wdlListener);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|REQUEST", wdlListener);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "worlddownloader:init", wdlListener);
            getLogger().info("Anti-World Downloader feature enabled.");
        }
    }

    // --- Getters ---

    public static AdvancedCoreHub getInstance() {
        return instance;
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

    public GadgetManager getGadgetManager() {
        return gadgetManager;
    }

    public PlayerVisibilityManager getPlayerVisibilityManager() {
        return playerVisibilityManager;
    }
}
