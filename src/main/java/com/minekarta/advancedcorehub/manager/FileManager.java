package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class FileManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private final Map<String, File> files = new HashMap<>();

    public FileManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        // Create plugin data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Load main config
        loadConfigFile("config.yml");

        // Load other configs
        loadConfigFile("items.yml");
        loadConfigFile("cosmetics.yml");
        loadConfigFile("menus/selector.yml");
        loadConfigFile("menus/socials.yml");
        loadConfigFile("menus/vip_gadget.yml");
        loadConfigFile("menus/info_menu.yml");

        // Load language files
        loadConfigFile("languages/en.yml");
        loadConfigFile("languages/pt.yml");
    }

    public void loadConfigFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            // Ensure parent directories exist
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            }
        }

        files.put(fileName, file);
        configs.put(fileName, YamlConfiguration.loadConfiguration(file));
    }

    public FileConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public void reloadAll() {
        configs.clear();
        files.clear();
        setup();
    }

    public void saveConfig(String name) {
        File file = files.get(name);
        FileConfiguration config = configs.get(name);
        if (file == null || config == null) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config '" + name + "', it was not loaded.");
            return;
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file, e);
        }
    }
}
