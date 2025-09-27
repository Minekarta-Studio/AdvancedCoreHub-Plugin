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

    public boolean addWorld(String worldName) {
        return hubWorlds.add(worldName);
    }

    public boolean removeWorld(String worldName) {
        return hubWorlds.remove(worldName);
    }

    public void listWorlds(CommandSender sender) {
        if (hubWorlds.isEmpty()) {
            plugin.getLocaleManager().sendMessage(sender, "worlds-list-empty");
            return;
        }
        String worldList = String.join(", ", hubWorlds);
        plugin.getLocaleManager().sendMessage(sender, "worlds-list", worldList);
    }

    public Set<String> getHubWorlds() {
        return new HashSet<>(hubWorlds);
    }
}