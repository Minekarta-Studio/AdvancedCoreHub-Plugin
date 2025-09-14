package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.util.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;

public class TitleAction implements Action {

    private final AdvancedCoreHub plugin;

    public TitleAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof Map)) {
            plugin.getLogger().warning("[TitleAction] Invalid data type. Expected a Map.");
            return;
        }
        Map<String, Object> dataMap = (Map<String, Object>) data;

        String titleStr = (String) dataMap.get("title");
        String subtitleStr = (String) dataMap.get("subtitle");

        if (titleStr == null && subtitleStr == null) {
            plugin.getLogger().warning("[TitleAction] Title and subtitle are both missing.");
            return;
        }

        Component title = titleStr != null ? plugin.getLocaleManager().getComponentFromString(titleStr, player) : Component.empty();
        Component subtitle = subtitleStr != null ? plugin.getLocaleManager().getComponentFromString(subtitleStr, player) : Component.empty();

        long fadeIn = Formatter.parseInt(dataMap.getOrDefault("fade-in", 10).toString(), 10);
        long stay = Formatter.parseInt(dataMap.getOrDefault("stay", 70).toString(), 70);
        long fadeOut = Formatter.parseInt(dataMap.getOrDefault("fade-out", 20).toString(), 20);

        Title.Times times = Title.Times.times(Duration.ofMillis(fadeIn * 50), Duration.ofMillis(stay * 50), Duration.ofMillis(fadeOut * 50));
        Title finalTitle = Title.title(title, subtitle, times);

        player.showTitle(finalTitle);
    }
}
