package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.util.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TitleAction implements Action {

    private final AdvancedCoreHub plugin;

    public TitleAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Player player, Object data) {
        String titleStr = "";
        String subtitleStr = "";
        int fadeIn = 10, stay = 70, fadeOut = 20;

        if (data instanceof Map) {
            Map<String, Object> dataMap = (Map<String, Object>) data;
            titleStr = dataMap.getOrDefault("title", "").toString().trim();
            subtitleStr = dataMap.getOrDefault("subtitle", "").toString().trim();
            fadeIn = Formatter.parseInt(dataMap.getOrDefault("fade-in", fadeIn).toString(), fadeIn);
            stay = Formatter.parseInt(dataMap.getOrDefault("stay", stay).toString(), stay);
            fadeOut = Formatter.parseInt(dataMap.getOrDefault("fade-out", fadeOut).toString(), fadeOut);
        } else if (data instanceof List) {
            List<String> args = (List<String>) data;
            if (args.size() < 2) return;

            String fullData = args.subList(1, args.size()).stream().collect(Collectors.joining(" "));
            String[] parts = fullData.split("\\|", 5);

            if (parts.length > 0) titleStr = parts[0].trim();
            if (parts.length > 1) subtitleStr = parts[1].trim();
            if (parts.length > 2) fadeIn = Formatter.parseInt(parts[2].trim(), fadeIn);
            if (parts.length > 3) stay = Formatter.parseInt(parts[3].trim(), stay);
            if (parts.length > 4) fadeOut = Formatter.parseInt(parts[4].trim(), fadeOut);
        } else {
            return;
        }

        if (titleStr.isEmpty() && subtitleStr.isEmpty()) {
            plugin.getLogger().warning("[TitleAction] Title and subtitle are both missing.");
            return;
        }

        Component title = !titleStr.isEmpty() ? plugin.getLocaleManager().getComponentFromString(titleStr, player) : Component.empty();
        Component subtitle = !subtitleStr.isEmpty() ? plugin.getLocaleManager().getComponentFromString(subtitleStr, player) : Component.empty();

        Title.Times times = Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L));
        Title finalTitle = Title.title(title, subtitle, times);

        player.showTitle(finalTitle);
    }
}
