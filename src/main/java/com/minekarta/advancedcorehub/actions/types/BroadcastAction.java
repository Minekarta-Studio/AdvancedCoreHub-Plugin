package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class BroadcastAction implements Action {

    private final AdvancedCoreHub plugin;

    public BroadcastAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String message;
        if (data instanceof List) {
            List<String> args = (List<String>) data;
            if (args.size() < 2) return; // Need at least [BROADCAST, message]
            // Remove the action name and join the rest
            message = String.join(":", args.subList(1, args.size()));
        } else if (data instanceof String) {
            message = (String) data;
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
