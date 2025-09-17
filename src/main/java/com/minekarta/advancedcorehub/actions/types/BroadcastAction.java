package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class BroadcastAction implements Action {

    private final AdvancedCoreHub plugin;

    public BroadcastAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String message;
        if (data instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> args = (List<String>) data;
            if (args.size() < 2) return; // Need at least [BROADCAST, message]
            // Join all arguments after the identifier, trimming each part
            message = args.subList(1, args.size()).stream().map(String::trim).collect(Collectors.joining(" "));
        } else if (data instanceof String) {
            message = ((String) data).trim();
        } else {
            return;
        }

        if (message.isEmpty()) return;

        // Since this is a broadcast, we can't parse per-player placeholders.
        // We pass null for the player to use global placeholders if any.
        Component componentMessage = plugin.getLocaleManager().getComponentFromString(message, null);
        plugin.getServer().broadcast(componentMessage);
    }
}
