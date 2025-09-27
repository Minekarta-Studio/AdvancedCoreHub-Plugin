package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.AnnouncementConfig;
import com.minekarta.advancedcorehub.config.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AnnouncementsManager {

    private final AdvancedCoreHub plugin;
    private final PluginConfig.AnnouncementsConfig config;
    private BukkitTask announcementTask;
    private int currentIndex = 0;

    public AnnouncementsManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig().announcements;
    }

    public void load() {
        cancelTasks();

        if (!config.enabled || config.messages.isEmpty()) {
            plugin.getLogger().info("Announcements feature is disabled or no messages are configured.");
            return;
        }

        long intervalTicks = config.intervalSeconds * 20L;
        if (intervalTicks <= 0) {
            plugin.getLogger().warning("Announcement interval must be positive. Disabling announcements.");
            return;
        }

        announcementTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::sendNextAnnouncement, 0, intervalTicks);
        plugin.getLogger().info("Announcements loaded. Interval: " + config.intervalSeconds + " seconds.");
    }

    private void sendNextAnnouncement() {
        if (config.messages.isEmpty()) return;

        AnnouncementConfig announcement;
        if (config.randomized) {
            int randomIndex = ThreadLocalRandom.current().nextInt(config.messages.size());
            announcement = config.messages.get(randomIndex);
        } else {
            announcement = config.messages.get(currentIndex);
            currentIndex = (currentIndex + 1) % config.messages.size();
        }

        List<String> worlds = announcement.worlds;
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (worlds == null || worlds.isEmpty() || worlds.contains(player.getWorld().getName())) {
                executeAnnouncementAction(player, announcement);
            }
        });
    }

    private void executeAnnouncementAction(Player player, AnnouncementConfig announcement) {
        switch (announcement.type.toUpperCase()) {
            case "CHAT":
                if (announcement.message != null) {
                    plugin.getLocaleManager().sendMessage(player, announcement.message);
                }
                break;
            case "TITLE":
                plugin.getActionManager().executeAction(player, "[TITLE]" + announcement.title + ";" + announcement.subtitle + ";" + announcement.fadeIn + ";" + announcement.stay + ";" + announcement.fadeOut);
                break;
            case "ACTION_BAR":
                if (announcement.message != null) {
                    plugin.getActionManager().executeAction(player, "[MESSAGE] " + announcement.message);
                }
                break;
            case "BOSS_BAR":
                if (announcement.message != null) {
                    plugin.getBossBarManager().createBossBar(player, announcement.message, announcement.bossBarColor, announcement.bossBarStyle, announcement.bossBarDuration);
                }
                break;
        }
    }

    public void cancelTasks() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
    }
}