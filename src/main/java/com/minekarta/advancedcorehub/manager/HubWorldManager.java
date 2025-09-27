package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HubWorldManager {

    private final AdvancedCoreHub plugin;
    private final Set<String> hubWorlds = new HashSet<>();

    public HubWorldManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        hubWorlds.clear();
        // Load from the type-safe config object
        List<String> worlds = plugin.getPluginConfig().getHubWorlds();
        if (worlds != null) {
            hubWorlds.addAll(worlds);
        }
    }

    public boolean isHubWorld(String worldName) {
        return hubWorlds.contains(worldName);
    }

    public void addWorld(String worldName) {
        if (hubWorlds.add(worldName)) {
            save();
        }
    }

    public void removeWorld(String worldName) {
        if (hubWorlds.remove(worldName)) {
            save();
        }
    }

    public void listWorlds(CommandSender sender) {
        if (hubWorlds.isEmpty()) {
            plugin.getLocaleManager().sendMessage(sender, "worlds-list-empty");
            return;
        }
        String worldList = String.join(", ", hubWorlds);
        plugin.getLocaleManager().sendMessage(sender, "worlds-list", worldList);
    }

    private void save() {
        // This is a write operation, so it directly modifies the config file
        plugin.getConfig().set("hub-worlds", List.copyOf(hubWorlds));
        plugin.saveConfig();
        // After saving, we should reload the plugin's config to keep everything in sync
        plugin.reloadPlugin();
    }
}