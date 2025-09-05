package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class TitleAction implements Action {

    private final AdvancedCoreHub plugin;

    public TitleAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        String[] parts = data.split(";", 5);
        if (parts.length < 2) {
            plugin.getLogger().warning("[TitleAction] Invalid data format. Expected: title;subtitle;fadeIn;stay;fadeOut");
            return;
        }

        String titleStr = plugin.getLocaleManager().getLegacyString(parts[0], player);
        String subtitleStr = plugin.getLocaleManager().getLegacyString(parts[1], player);

        Component title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleStr);
        Component subtitle = LegacyComponentSerializer.legacyAmpersand().deserialize(subtitleStr);

        try {
            long fadeIn = parts.length > 2 ? Long.parseLong(parts[2]) : 10L;
            long stay = parts.length > 3 ? Long.parseLong(parts[3]) : 70L;
            long fadeOut = parts.length > 4 ? Long.parseLong(parts[4]) : 20L;

            Title.Times times = Title.Times.times(Duration.ofMillis(fadeIn * 50), Duration.ofMillis(stay * 50), Duration.ofMillis(fadeOut * 50));
            Title finalTitle = Title.title(title, subtitle, times);

            player.showTitle(finalTitle);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("[TitleAction] Invalid number format for timings in data: " + data);
        }
    }
}
