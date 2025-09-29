package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Constants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Stream;

public class FileManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, FileConfiguration> configs = new ConcurrentHashMap<>();
    private final Map<String, File> files = new ConcurrentHashMap<>();

    public FileManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Void> setup() {
        // Ensure the plugin data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Load main configs
        futures.add(loadConfigFileAsync(Constants.CONFIG_FILE));
        futures.add(loadConfigFileAsync(Constants.ITEMS_FILE));
        futures.add(loadConfigFileAsync(Constants.COSMETICS_FILE));

        // Dynamically load all menu and language configurations
        futures.add(loadAllConfigsFromFolderAsync(Constants.MENUS_FOLDER));
        futures.add(loadAllConfigsFromFolderAsync(Constants.LANG_FOLDER));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> loadConfigFileAsync(String fileName) {
        return CompletableFuture.runAsync(() -> {
            try {
                File file = new File(plugin.getDataFolder(), fileName);
                files.put(fileName, file);

                // If the file doesn't exist, save the default from resources
                if (!file.exists()) {
                    if (file.getParentFile() != null) {
                        file.getParentFile().mkdirs();
                    }
                    if (plugin.getResource(fileName) != null) {
                        plugin.saveResource(fileName, false);
                    }
                }

                // Load the configuration from the file
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                configs.put(fileName, config);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load configuration file: " + fileName, e);
            }
        });
    }

    public CompletableFuture<Void> reloadAll() {
        plugin.getLogger().info("Starting asynchronous configuration reload...");
        configs.clear();
        files.clear();
        return setup().whenComplete((ignoredResult, error) -> {
            if (error != null) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred during configuration reload.", error);
            } else {
                plugin.getLogger().info("All configurations have been reloaded successfully.");
            }
        });
    }

    public void saveConfigAsync(String name) {
        File file = files.get(name);
        FileConfiguration config = configs.get(name);

        if (file == null || config == null) {
            plugin.getLogger().log(Level.WARNING, "Attempted to save a non-loaded config: " + name);
            return;
        }

        // The actual saving can be slow, so run it on an async thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Configuration must be saved on a single thread.
                // To avoid issues with async modifications, save to a string first.
                String dataToSave = config.saveToString();
                // Now, write the string to the file.
                Files.write(file.toPath(), dataToSave.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file.getName(), e);
            }
        });
    }

    private CompletableFuture<Void> loadAllConfigsFromFolderAsync(String folderName) {
        return CompletableFuture.runAsync(() -> {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            try {
                // 1. Discover and load default configs from the JAR resources folder.
                URI codeSourceUri = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
                if ("file".equals(codeSourceUri.getScheme())) {
                    Path jarPath = Paths.get(codeSourceUri);
                    try (FileSystem fs = FileSystems.newFileSystem(jarPath, (ClassLoader) null)) {
                        Path folderPathInJar = fs.getPath(folderName);
                        if (Files.exists(folderPathInJar)) {
                            try (Stream<Path> walk = Files.walk(folderPathInJar, 1)) {
                                walk.filter(path -> path.getFileName() != null && path.toString().endsWith(".yml"))
                                    .forEach(path -> {
                                        String resourcePath = folderName + "/" + path.getFileName().toString();
                                        futures.add(loadConfigFileAsync(resourcePath));
                                    });
                            }
                        }
                    }
                }

                // 2. Load any custom .yml files from the corresponding folder on disk.
                File folderOnDisk = new File(plugin.getDataFolder(), folderName);
                if (folderOnDisk.isDirectory()) {
                    File[] filesInFolder = folderOnDisk.listFiles((dir, name) -> name.endsWith(".yml"));
                    if (filesInFolder != null) {
                        for (File file : filesInFolder) {
                            String configPath = folderName + "/" + file.getName();
                            if (!configs.containsKey(configPath)) {
                                futures.add(loadConfigFileAsync(configPath));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Could not automatically discover/load configs from folder: " + folderName, e);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join(); // Wait for all files in this folder to be loaded
        });
    }

    public FileConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public Map<String, FileConfiguration> getConfigs() {
        return Collections.unmodifiableMap(configs);
    }
}