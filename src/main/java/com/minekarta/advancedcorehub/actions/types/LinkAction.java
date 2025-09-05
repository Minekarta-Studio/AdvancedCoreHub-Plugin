package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class LinkAction implements Action {

    private final AdvancedCoreHub plugin;

    public LinkAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        String[] parts = data.split(";", 3);
        if (parts.length < 3) {
            plugin.getLogger().warning("[LinkAction] Invalid data format. Expected: message;hoverText;link");
            return;
        }

        String messageStr = plugin.getLocaleManager().get(parts[0], player);
        String hoverStr = plugin.getLocaleManager().get(parts[1], player);
        String link = plugin.getLocaleManager().get(parts[2], player);

        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(messageStr)
                .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(hoverStr)))
                .clickEvent(ClickEvent.openUrl(link));

        player.sendMessage(message);
    }
}
