package com.minekarta.advancedcorehub;

import com.minekarta.advancedcorehub.cosmetics.CosmeticsManager;
import com.minekarta.advancedcorehub.cosmetics.PlayerMoveListener;
import com.minekarta.advancedcorehub.listeners.*;
import com.minekarta.advancedcorehub.manager.*;
import com.minekarta.advancedcorehub.util.TeleportUtil;
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
    private VanishManager vanishManager;
    private CustomCommandManager customCommandManager;
    private ScoreboardManager scoreboardManager;


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
        this.vanishManager = new VanishManager(this);
        this.customCommandManager = new CustomCommandManager(this);
        this.scoreboardManager = new ScoreboardManager(this);


        // Load other components
        registerCommands();
        registerListeners();
        registerChannels();
        this.customCommandManager.registerCustomCommands();

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
        if (scoreboardManager != null) {
            scoreboardManager.cleanup();
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
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementFeaturesListener(this), this);
        // SecurityListener is a PluginMessageListener, registered in registerChannels()
    }

    private void registerChannels() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this.serverInfoManager);

        // Register World Downloader channels
        if (getConfig().getBoolean("security.anti_world_downloader.enabled", true)) {
            SecurityListener securityListener = new SecurityListener(this);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", securityListener);
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "worlddownloader:init", securityListener);
            getLogger().info("Anti-World Downloader listener registered.");
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

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
