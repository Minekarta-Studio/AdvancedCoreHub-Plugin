package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisabledWorlds {

    private final AdvancedCoreHub plugin;
    private final Set<String> disabledWorlds = new HashSet<>();

    public DisabledWorlds(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        disabledWorlds.clear();
        List<String> worlds = plugin.getConfig().getStringList("disabled_worlds");
        disabledWorlds.addAll(worlds);
    }

    public boolean isDisabled(String worldName) {
        return disabledWorlds.contains(worldName);
    }

    public void addWorld(String worldName) {
        disabledWorlds.add(worldName);
        save();
    }

    public void removeWorld(String worldName) {
        disabledWorlds.remove(worldName);
        save();
    }

    public void listWorlds(CommandSender sender) {
        if (disabledWorlds.isEmpty()) {
            plugin.getLocaleManager().sendMessage(sender, "worlds-list-empty");
            return;
        }
        String worldList = String.join(", ", disabledWorlds);
        plugin.getLocaleManager().sendMessage(sender, "worlds-list", worldList);
    }

    private void save() {
        plugin.getConfig().set("disabled_worlds", List.copyOf(disabledWorlds));
        plugin.saveConfig();
    }
}
