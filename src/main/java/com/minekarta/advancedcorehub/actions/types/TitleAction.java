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

public class TitleAction implements Action {

    private final AdvancedCoreHub plugin;

    public TitleAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String titleStr = "";
        String subtitleStr = "";
        int fadeIn = 10, stay = 70, fadeOut = 20;

        if (data instanceof Map) {
            Map<String, Object> dataMap = (Map<String, Object>) data;
            titleStr = (String) dataMap.get("title");
            subtitleStr = (String) dataMap.get("subtitle");
            fadeIn = Formatter.parseInt(dataMap.getOrDefault("fade-in", fadeIn).toString(), fadeIn);
            stay = Formatter.parseInt(dataMap.getOrDefault("stay", stay).toString(), stay);
            fadeOut = Formatter.parseInt(dataMap.getOrDefault("fade-out", fadeOut).toString(), fadeOut);
        } else if (data instanceof List) {
            List<String> args = (List<String>) data;
            if (args.size() < 2) return; // Needs at least [TITLE, title_text]
            titleStr = args.get(1);
            if (args.size() > 2) subtitleStr = args.get(2);
            if (args.size() > 3) fadeIn = Formatter.parseInt(args.get(3), fadeIn);
            if (args.size() > 4) stay = Formatter.parseInt(args.get(4), stay);
            if (args.size() > 5) fadeOut = Formatter.parseInt(args.get(5), fadeOut);
        } else {
            return;
        }

        if (titleStr == null && subtitleStr == null) {
            plugin.getLogger().warning("[TitleAction] Title and subtitle are both missing.");
            return;
        }

        Component title = titleStr != null ? plugin.getLocaleManager().getComponentFromString(titleStr, player) : Component.empty();
        Component subtitle = subtitleStr != null ? plugin.getLocaleManager().getComponentFromString(subtitleStr, player) : Component.empty();

        Title.Times times = Title.Times.times(Duration.ofMillis(fadeIn * 50), Duration.ofMillis(stay * 50), Duration.ofMillis(fadeOut * 50));
        Title finalTitle = Title.title(title, subtitle, times);

        player.showTitle(finalTitle);
    }
}
