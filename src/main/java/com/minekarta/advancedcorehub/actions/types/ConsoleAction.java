package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class ConsoleAction implements Action {

    private final AdvancedCoreHub plugin;

    public ConsoleAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        String command = data;
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
    }
}
