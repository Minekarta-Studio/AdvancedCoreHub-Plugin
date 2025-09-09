package com.minekarta.advancedcorehub;

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
    private DisabledWorlds disabledWorlds;
    private AnnouncementsManager announcementsManager;
    private BossBarManager bossBarManager;
    private MenuManager menuManager;
    private ChatManager chatManager;
    private CommandManager commandManager;
    private InventoryManager inventoryManager;


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

        this.actionManager = new ActionManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.disabledWorlds = new DisabledWorlds(this);
        this.announcementsManager = new AnnouncementsManager(this);
        this.announcementsManager.load();
        this.bossBarManager = new BossBarManager(this);
        this.chatManager = new ChatManager();
        this.commandManager = new CommandManager(this);


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
            this.menuManager.loadMenus();
            this.actionManager = new ActionManager(this); // Re-register actions
            this.disabledWorlds.load();
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
    }

    private void registerChannels() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
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

    public DisabledWorlds getDisabledWorlds() {
        return disabledWorlds;
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
}
