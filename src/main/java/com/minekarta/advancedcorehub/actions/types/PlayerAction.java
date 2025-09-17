package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerAction implements Action {

    private final AdvancedCoreHub plugin;

    public PlayerAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String command;
        if (data instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> args = (List<String>) data;
            if (args.size() < 2) return;
            command = args.subList(1, args.size()).stream().map(String::trim).collect(Collectors.joining(" "));
        } else if (data instanceof String) {
            command = ((String) data).trim();
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
