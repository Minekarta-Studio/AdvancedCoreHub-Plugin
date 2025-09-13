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
        List<String> worlds = plugin.getConfig().getStringList("hub-worlds");
        hubWorlds.addAll(worlds);
    }

    public boolean isHubWorld(String worldName) {
        return hubWorlds.contains(worldName);
    }

    public void addWorld(String worldName) {
        hubWorlds.add(worldName);
        save();
    }

    public void removeWorld(String worldName) {
        hubWorlds.remove(worldName);
        save();
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
        plugin.getConfig().set("hub-worlds", List.copyOf(hubWorlds));
        plugin.saveConfig();
    }
}
