package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class LinkAction implements Action {

    private final AdvancedCoreHub plugin;

    public LinkAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        List<String> args = (List<String>) data;

        if (args.size() < 4) {
            plugin.getLogger().warning("[LinkAction] Invalid data format. Expected: [LINK:message:hoverText:link]");
            return;
        }

        // The link part should not be formatted, so we just replace placeholders.
        String link = args.get(3);
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            link = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, link);
        }

        Component message = plugin.getLocaleManager().getComponentFromString(args.get(1), player)
                .hoverEvent(HoverEvent.showText(plugin.getLocaleManager().getComponentFromString(args.get(2), player)))
                .clickEvent(ClickEvent.openUrl(link));

        player.sendMessage(message);
    }
}
