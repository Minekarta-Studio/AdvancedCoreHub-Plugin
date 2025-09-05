package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnnouncementsManager {

    private final AdvancedCoreHub plugin;
    private BukkitTask announcementTask;
    private List<String> announcements;
    private int interval;
    private String prefix; // Legacy formatted string

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
        this.prefix = plugin.getLocaleManager().getLegacyString("announcement-prefix", null);

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

            String fullRawMessage = this.prefix + " " + message;

            // Broadcast to each player individually to handle PAPI placeholders correctly
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                Component formattedMessage = plugin.getLocaleManager().format(fullRawMessage, player);
                player.sendMessage(formattedMessage);
            }

        }, 20L * 10, 20L * interval); // 10 second initial delay
    }

    public void cancelTasks() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
    }
}
