package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerAction implements Action {

    private final AdvancedCoreHub plugin;

    public PlayerAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String command;
        if (data instanceof List) {
            List<String> args = (List<String>) data;
            if (args.size() < 2) return;
            command = String.join(":", args.subList(1, args.size()));
        } else if (data instanceof String) {
            command = (String) data;
        } else {
            return;
        }

        if (command.isEmpty()) return;

        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            command = PlaceholderAPI.setPlaceholders(player, command);
        }

        player.performCommand(command);
    }
}
