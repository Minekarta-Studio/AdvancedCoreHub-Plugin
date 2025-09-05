package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnnouncementsManager {

    private final AdvancedCoreHub plugin;
    private BukkitTask announcementTask;
    private List<String> announcements;
    private int interval;
    private String prefix;

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
        this.prefix = plugin.getLocaleManager().get("announcement-prefix", null);

        if (announcements.isEmpty()) {
            return;
        }

        startAnnouncements();
    }

    private void startAnnouncements() {
        AtomicInteger currentIndex = new AtomicInteger(0);
        announcementTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (announcements.isEmpty()) {
                cancelTasks();
                return;
            }
            String message = announcements.get(currentIndex.getAndIncrement());
            if (currentIndex.get() >= announcements.size()) {
                currentIndex.set(0);
            }

            String fullMessage = prefix + message;
            Component componentMessage = MiniMessage.miniMessage().deserialize(toMiniMessage(fullMessage));

            plugin.getServer().broadcast(componentMessage);

        }, 20L * 10, 20L * interval); // 10 second initial delay
    }

    public void cancelTasks() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
    }

    private String toMiniMessage(String legacyText) {
        return legacyText.replace('§', '&').replaceAll("&([0-9a-fk-or])", "<$1>");
    }
}
