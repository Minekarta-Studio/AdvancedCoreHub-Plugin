package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

public class LinkAction implements Action {

    private final AdvancedCoreHub plugin;

    public LinkAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || ((String) data).isEmpty()) return;

        String linkData = (String) data;
        String[] parts = linkData.split(";", 3);
        if (parts.length < 3) {
            plugin.getLogger().warning("[LinkAction] Invalid data format. Expected: message;hoverText;link");
            return;
        }

        // The link part should not be formatted, so we just replace placeholders.
        String link = parts[2];
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            link = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, link);
        }

        Component message = plugin.getLocaleManager().getComponentFromString(parts[0], player)
                .hoverEvent(HoverEvent.showText(plugin.getLocaleManager().getComponentFromString(parts[1], player)))
                .clickEvent(ClickEvent.openUrl(link));

        player.sendMessage(message);
    }
}
