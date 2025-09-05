package com.minekarta.advancedcorehub;

import com.minekarta.advancedcorehub.commands.AdvancedCoreHubCommand;
import com.minekarta.advancedcorehub.commands.standalone.ClearChatCommand;
import com.minekarta.advancedcorehub.commands.standalone.FlyCommand;
import com.minekarta.advancedcorehub.commands.standalone.LockChatCommand;
import com.minekarta.advancedcorehub.listeners.ChatListener;
import com.minekarta.advancedcorehub.listeners.MenuListener;
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


    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Enabling AdvancedCoreHub v" + getDescription().getVersion());

        // Initialize managers
        this.fileManager = new FileManager(this);
        this.fileManager.setup(); // Must be first

        this.localeManager = new LocaleManager(this, this.fileManager);
        this.localeManager.load();

        this.itemsManager = new ItemsManager(this);
        this.itemsManager.loadItems();

        this.menuManager = new MenuManager(this); // Must be before ActionManager if actions use menus

        this.actionManager = new ActionManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.disabledWorlds = new DisabledWorlds(this);
        this.announcementsManager = new AnnouncementsManager(this);
        this.announcementsManager.load();
        this.bossBarManager = new BossBarManager(this);
        this.chatManager = new ChatManager();

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
        // Main command
        AdvancedCoreHubCommand advancedCoreHubCommand = new AdvancedCoreHubCommand(this);
        getCommand("advancedcorehub").setExecutor(advancedCoreHubCommand);
        getCommand("advancedcorehub").setTabCompleter(advancedCoreHubCommand);

        // Standalone commands
        getCommand("spawn").setExecutor(new com.minekarta.advancedcorehub.commands.standalone.SpawnCommand(this));
        getCommand("setspawn").setExecutor(new com.minekarta.advancedcorehub.commands.standalone.SetSpawnCommand(this));
        getCommand("lockchat").setExecutor(new LockChatCommand(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));

        FlyCommand flyCommand = new com.minekarta.advancedcorehub.commands.standalone.FlyCommand(this);
        getCommand("fly").setExecutor(flyCommand);
        getCommand("fly").setTabCompleter(flyCommand);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldEventListeners(this), this);
        getServer().getPluginManager().registerEvents(new TridentListener(this), this);
        getServer().getPluginManager().registerEvents(new RodListener(this), this);
        getServer().getPluginManager().registerEvents(new AoteListener(this), this);
        getServer().getPluginManager().registerEvents(new EnderbowListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
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
}
