package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class LinkAction implements Action {

    private final AdvancedCoreHub plugin;

    public LinkAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        @SuppressWarnings("unchecked")
        List<String> args = (List<String>) data;

        // Re-join the arguments from index 1 to handle spaces in message/hover text
        String fullData = args.subList(1, args.size()).stream().collect(Collectors.joining(" "));
        String[] parts = fullData.split(":", 3);

        if (parts.length < 3) {
            plugin.getLogger().warning("[LinkAction] Invalid data format. Expected: [LINK] <message>:<hoverText>:<link>");
            return;
        }

        String messageText = parts[0].trim();
        String hoverText = parts[1].trim();
        String link = parts[2].trim();

        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            link = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, link);
        }

        Component messageComponent = plugin.getLocaleManager().getComponentFromString(messageText, player)
                .hoverEvent(HoverEvent.showText(plugin.getLocaleManager().getComponentFromString(hoverText, player)))
                .clickEvent(ClickEvent.openUrl(link));

        player.sendMessage(messageComponent);
    }
}
