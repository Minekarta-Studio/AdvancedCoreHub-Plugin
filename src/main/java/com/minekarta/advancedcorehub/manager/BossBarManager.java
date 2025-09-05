package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {

    private final AdvancedCoreHub plugin;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public BossBarManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void createBossBar(Player player, String title, BarColor color, BarStyle style) {
        createBossBar(player, title, color, style, -1); // -1 for permanent
    }

    public void createBossBar(Player player, String title, BarColor color, BarStyle style, int durationSeconds) {
        removeBossBar(player); // Remove existing bar before creating a new one

        Component componentTitle = Formatter.format(player, title);
        // The modern component methods might not be available in all 1.20.4 paper forks,
        // so we use the legacy serializer for broader compatibility.
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(componentTitle);

        BossBar bossBar = Bukkit.createBossBar(legacyTitle, color, style);
        bossBar.addPlayer(player);
        playerBossBars.put(player.getUniqueId(), bossBar);

        if (durationSeconds > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Only remove it if it's still the same bar we created
                BossBar currentBar = playerBossBars.get(player.getUniqueId());
                if (currentBar == bossBar) {
                    removeBossBar(player);
                }
            }, durationSeconds * 20L);
        }
    }

    public void removeBossBar(Player player) {
        BossBar existingBar = playerBossBars.remove(player.getUniqueId());
        if (existingBar != null) {
            existingBar.removeAll();
        }
    }

    public void updateTitle(Player player, String newTitle) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            Component componentTitle = Formatter.format(player, newTitle);
            bossBar.setTitle(LegacyComponentSerializer.legacySection().serialize(componentTitle));
        }
    }

    public void updateColor(Player player, BarColor color) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.setColor(color);
        }
    }

    public void updateStyle(Player player, BarStyle style) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.setStyle(style);
        }
    }

    public void cleanup() {
        for (BossBar bossBar : playerBossBars.values()) {
            bossBar.removeAll();
        }
        playerBossBars.clear();
    }
}
