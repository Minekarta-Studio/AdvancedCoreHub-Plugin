package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlayerAction implements Action {

    private final AdvancedCoreHub plugin;

    public PlayerAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || ((String) data).isEmpty()) return;

        String command = (String) data;
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }

        player.performCommand(command);
    }
}
