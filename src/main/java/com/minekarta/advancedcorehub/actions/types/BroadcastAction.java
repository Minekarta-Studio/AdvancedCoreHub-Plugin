package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class BroadcastAction implements Action {

    private final AdvancedCoreHub plugin;

    public BroadcastAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || ((String) data).isEmpty()) return;

        String message = (String) data;

        // Since this is a broadcast, we can't parse per-player placeholders.
        // We pass null for the player to use global placeholders if any.
        Component componentMessage = plugin.getLocaleManager().getComponentFromString(message, null);

        plugin.getServer().broadcast(componentMessage);
    }
}
