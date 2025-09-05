package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class BroadcastAction implements Action {

    private final AdvancedCoreHub plugin;

    public BroadcastAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        String message = plugin.getLocaleManager().get(data, player);
        // Using getLocaleManager().get() already processes placeholders and colors
        // We just need to convert it to a component for modern broadcasting

        String miniMessageStr = toMiniMessage(message);
        Component componentMessage = MiniMessage.miniMessage().deserialize(miniMessageStr);

        plugin.getServer().broadcast(componentMessage);
    }

    private String toMiniMessage(String legacyText) {
        return legacyText.replace('§', '&').replaceAll("&([0-9a-fk-or])", "<$1>");
    }
}
