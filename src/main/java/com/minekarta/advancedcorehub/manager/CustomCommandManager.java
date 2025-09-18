package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.commands.CustomCommand;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CustomCommandManager {

    private final AdvancedCoreHub plugin;
    private CommandMap commandMap;

    public CustomCommandManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        try {
            Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(plugin.getServer().getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not access command map.", e);
        }
    }

    public void registerCustomCommands() {
        FileConfiguration config = plugin.getFileManager().getConfig("custom_commands.yml");
        if (config == null || commandMap == null) {
            plugin.getLogger().warning("Custom commands config not found or command map could not be accessed. Skipping registration.");
            return;
        }

        for (String commandName : config.getKeys(false)) {
            ConfigurationSection cmdConfig = config.getConfigurationSection(commandName);
            if (cmdConfig == null) continue;

            String permission = cmdConfig.getString("permission");
            List<String> aliases = cmdConfig.getStringList("aliases");
            List<Map<?, ?>> actions = cmdConfig.getMapList("actions");

            if (actions.isEmpty()) {
                plugin.getLogger().warning("Custom command '" + commandName + "' has no actions defined. Skipping.");
                continue;
            }

            CustomCommand customCommand = new CustomCommand(plugin, commandName, permission, aliases, actions);
            commandMap.register(plugin.getDescription().getName(), customCommand);
            plugin.getLogger().info("Registered custom command: /" + commandName);
        }
    }
}
