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
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        // Since this is a broadcast, we can't parse per-player placeholders.
        // We pass null for the player to use global placeholders if any.
        Component componentMessage = plugin.getLocaleManager().getComponentFromString(data, null);

        plugin.getServer().broadcast(componentMessage);
    }
}
