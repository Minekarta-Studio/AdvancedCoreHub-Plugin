package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig.AnnouncementsConfig;
import com.minekarta.advancedcorehub.config.PluginConfig.AnnouncementConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AnnouncementsManager {

    private final AdvancedCoreHub plugin;
    private final AnnouncementsConfig config;
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
        // Optimization: if no players are online, or no messages are configured, do nothing.
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        if (config.messages.isEmpty() || onlinePlayers.isEmpty()) {
            return;
        }

        // Select the announcement to display
        AnnouncementConfig announcement;
        if (config.randomized) {
            int randomIndex = ThreadLocalRandom.current().nextInt(config.messages.size());
            announcement = config.messages.get(randomIndex);
        } else {
            announcement = config.messages.get(currentIndex);
            currentIndex = (currentIndex + 1) % config.messages.size();
        }

        // Optimization: Iterate over the cached list of players
        List<String> worlds = announcement.worlds;
        for (Player player : onlinePlayers) {
            if (worlds == null || worlds.isEmpty() || worlds.contains(player.getWorld().getName())) {
                executeAnnouncementAction(player, announcement);
            }
        }
    }

    private void executeAnnouncementAction(Player player, AnnouncementConfig announcement) {
        String type = announcement.type.toUpperCase();
        switch (type) {
            case "CHAT":
                if (announcement.message != null && !announcement.message.isEmpty()) {
                    // Use sendMessageNoPrefix for announcements to avoid double prefixes
                    plugin.getLocaleManager().sendMessageNoPrefix(player, announcement.message);
                }
                break;
            case "TITLE":
                plugin.getActionManager().executeAction(player, "[TITLE]" + announcement.title + ";" + announcement.subtitle + ";" + announcement.fadeIn + ";" + announcement.stay + ";" + announcement.fadeOut);
                break;
            case "ACTION_BAR":
                if (announcement.message != null && !announcement.message.isEmpty()) {
                    Component actionBarComponent = plugin.getLocaleManager().getComponent(announcement.message, player);
                    player.sendActionBar(actionBarComponent);
                }
                break;
            case "BOSS_BAR":
                if (announcement.message != null && !announcement.message.isEmpty()) {
                    try {
                        BarColor color = BarColor.valueOf(announcement.bossBarColor.toUpperCase());
                        BarStyle style = BarStyle.valueOf(announcement.bossBarStyle.toUpperCase());
                        plugin.getBossBarManager().createBossBar(player, announcement.message, color, style, announcement.bossBarDuration);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid boss bar color or style in announcements config: '" +
                                announcement.bossBarColor + "' or '" + announcement.bossBarStyle + "'. Defaulting to WHITE/SOLID.");
                        // Fallback to default values
                        plugin.getBossBarManager().createBossBar(player, announcement.message, BarColor.WHITE, BarStyle.SOLID, announcement.bossBarDuration);
                    }
                }
                break;
            default:
                plugin.getLogger().warning("Unknown announcement type in config: '" + announcement.type + "'");
                break;
        }
    }

    public void cancelTasks() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
    }
}