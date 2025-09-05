package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        removeBossBar(player); // Remove existing bar before creating a new one

        Component componentTitle = MiniMessage.miniMessage().deserialize(toMiniMessage(title));
        BossBar bossBar = Bukkit.createBossBar(LegacyComponentSerializer.legacySection().serialize(componentTitle), color, style);
        bossBar.addPlayer(player);
        playerBossBars.put(player.getUniqueId(), bossBar);
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
            Component componentTitle = MiniMessage.miniMessage().deserialize(toMiniMessage(newTitle));
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

    private String toMiniMessage(String legacyText) {
        if (legacyText == null) return "";
        return legacyText.replace('§', '&').replaceAll("&([0-9a-fk-or])", "<$1>");
    }
}
