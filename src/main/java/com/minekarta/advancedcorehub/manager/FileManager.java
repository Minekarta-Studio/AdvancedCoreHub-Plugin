package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

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

        // Dynamically load all menu configurations
        loadAllConfigsFromFolder("menus");

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

    public Map<String, FileConfiguration> getConfigs() {
        return configs;
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

    private void loadAllConfigsFromFolder(String folderName) {
        // Discover and load default configs from the JAR. This copies them to the data folder if they don't exist.
        try {
            URI codeSourceUri = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            URI jarUri = URI.create("jar:" + codeSourceUri.toString());
            FileSystem fileSystem;
            try {
                fileSystem = FileSystems.getFileSystem(jarUri);
            } catch (FileSystemNotFoundException e) {
                fileSystem = FileSystems.newFileSystem(jarUri, Collections.emptyMap());
            }

            Path folderPathInJar = fileSystem.getPath(folderName);
            if (Files.exists(folderPathInJar)) {
                try (Stream<Path> walk = Files.walk(folderPathInJar, 1)) {
                    walk.filter(path -> path.getFileName() != null && path.toString().endsWith(".yml"))
                            .forEach(path -> {
                                String resourcePath = folderName + "/" + path.getFileName().toString();
                                loadConfigFile(resourcePath); // This handles copying and loading
                            });
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not automatically discover and load configs from JAR folder: " + folderName, e);
        }

        // Also load any custom .yml files from the data folder that might not be in the JAR.
        File folderOnDisk = new File(plugin.getDataFolder(), folderName);
        if (folderOnDisk.isDirectory()) {
            File[] filesInFolder = folderOnDisk.listFiles((dir, name) -> name.endsWith(".yml"));
            if (filesInFolder != null) {
                for (File file : filesInFolder) {
                    String configPath = folderName + "/" + file.getName();
                    // If not already loaded (e.g., a custom file not in the JAR), load it.
                    if (!configs.containsKey(configPath)) {
                        loadConfigFile(configPath);
                    }
                }
            }
        }
    }
}
