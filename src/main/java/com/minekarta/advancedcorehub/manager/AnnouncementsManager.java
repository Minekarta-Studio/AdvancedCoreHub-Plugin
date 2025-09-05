package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnnouncementsManager {

    private final AdvancedCoreHub plugin;
    private BukkitTask announcementTask;
    private List<String> announcements;
    private int interval;
    private String displayMode;
    private BarColor bossBarColor;
    private BarStyle bossBarStyle;
    private int bossBarDuration;

    public AnnouncementsManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (announcementTask != null) {
            announcementTask.cancel();
        }

        if (!plugin.getConfig().getBoolean("announcements.enabled", false)) {
            return;
        }

        this.announcements = plugin.getConfig().getStringList("announcements.messages");
        this.interval = plugin.getConfig().getInt("announcements.interval_seconds", 60);
        this.displayMode = plugin.getConfig().getString("announcements.display_mode", "CHAT").toUpperCase();

        try {
            this.bossBarColor = BarColor.valueOf(plugin.getConfig().getString("announcements.boss_bar_color", "YELLOW").toUpperCase());
            this.bossBarStyle = BarStyle.valueOf(plugin.getConfig().getString("announcements.boss_bar_style", "SOLID").toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid boss bar color or style in config.yml. Using defaults.");
            this.bossBarColor = BarColor.YELLOW;
            this.bossBarStyle = BarStyle.SOLID;
        }

        this.bossBarDuration = plugin.getConfig().getInt("announcements.boss_bar_duration", 10);


        if (announcements.isEmpty()) {
            return;
        }

        startAnnouncements();
    }

    private void startAnnouncements() {
        AtomicInteger currentIndex = new AtomicInteger(0);
        announcementTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (announcements.isEmpty() || Bukkit.getOnlinePlayers().isEmpty()) {
                return; // Don't announce to an empty server
            }

            String message = announcements.get(currentIndex.getAndIncrement());
            if (currentIndex.get() >= announcements.size()) {
                currentIndex.set(0);
            }

            if ("BOSS_BAR".equals(displayMode)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // We pass the player to format placeholders per-player in the announcement
                    plugin.getBossBarManager().createBossBar(player, message, bossBarColor, bossBarStyle, bossBarDuration);
                }
            } else { // Default to CHAT
                // For chat, we can't format per-player, so we pass null.
                Component componentMessage = plugin.getLocaleManager().getComponentFromString(message, null);
                plugin.getServer().broadcast(componentMessage);
            }

        }, 20L * 10, 20L * interval); // 10 second initial delay
    }

    public void cancelTasks() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
    }
}
