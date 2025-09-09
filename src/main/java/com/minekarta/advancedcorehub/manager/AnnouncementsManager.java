package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AnnouncementsManager {

    private final AdvancedCoreHub plugin;
    private BukkitTask announcementTask;
    private List<Map<?, ?>> announcements;
    private boolean randomized;
    private int interval;
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public AnnouncementsManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void load() {
        cancelTasks();

        ConfigurationSection config = plugin.getConfig().getConfigurationSection("announcements");
        if (config == null || !config.getBoolean("enabled", false)) {
            return;
        }

        this.interval = config.getInt("interval_seconds", 60);
        this.randomized = config.getBoolean("randomized", false);

        // Handle backward compatibility for old string list format
        if (config.isStringList("messages")) {
            this.announcements = config.getStringList("messages").stream()
                    .map(msg -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("message", msg);
                        map.put("type", config.getString("display_mode", "CHAT").toUpperCase());
                        return map;
                    })
                    .collect(Collectors.toList());
            plugin.getLogger().warning("You are using a deprecated format for announcements. Please update to the new object format for full features.");
        } else {
            this.announcements = config.getMapList("messages");
        }

        if (announcements == null || announcements.isEmpty()) {
            return;
        }

        startAnnouncements();
    }

    private void startAnnouncements() {
        announcementTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::sendNextAnnouncement, 20L * 10, 20L * interval);
    }

    private void sendNextAnnouncement() {
        if (announcements.isEmpty() || Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        Map<?, ?> announcementData;
        if (randomized) {
            announcementData = announcements.get(ThreadLocalRandom.current().nextInt(announcements.size()));
        } else {
            announcementData = announcements.get(currentIndex.getAndIncrement());
            if (currentIndex.get() >= announcements.size()) {
                currentIndex.set(0);
            }
        }

        String type = ((String) announcementData.getOrDefault("type", "CHAT")).toUpperCase();
        List<String> worlds = (List<String>) announcementData.get("worlds");
        Collection<? extends Player> recipients = getRecipients(worlds);

        if (recipients.isEmpty()) return;

        switch (type) {
            case "TITLE":
                sendTitleAnnouncement(recipients, announcementData);
                break;
            case "ACTION_BAR":
                sendActionBarAnnouncement(recipients, announcementData);
                break;
            case "BOSS_BAR":
                sendBossBarAnnouncement(recipients, announcementData);
                break;
            case "CHAT":
            default:
                sendChatAnnouncement(recipients, announcementData);
                break;
        }
    }

    private Collection<? extends Player> getRecipients(List<String> worldNames) {
        if (worldNames == null || worldNames.isEmpty()) {
            return Bukkit.getOnlinePlayers();
        }
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> worldNames.contains(p.getWorld().getName()))
                .collect(Collectors.toList());
    }

    private void sendChatAnnouncement(Collection<? extends Player> recipients, Map<?, ?> data) {
        String message = (String) data.get("message");
        if (message == null) return;

        // Chat messages are sent globally to recipients, placeholders that are not player-specific will work
        Component component = plugin.getLocaleManager().getComponentFromString(message, null);
        recipients.forEach(player -> player.sendMessage(component));
    }

    private void sendTitleAnnouncement(Collection<? extends Player> recipients, Map<?, ?> data) {
        String message = (String) data.get("message");
        if (message == null) return;

        String[] parts = message.split(";", 2);
        String titleStr = parts[0];
        String subtitleStr = parts.length > 1 ? parts[1] : "";

        int fadeIn = Formatter.parseInt(data.getOrDefault("fade-in", 10).toString(), 10);
        int stay = Formatter.parseInt(data.getOrDefault("stay", 70).toString(), 70);
        int fadeOut = Formatter.parseInt(data.getOrDefault("fade-out", 20).toString(), 20);

        Title.Times times = Title.Times.times(Duration.ofTicks(fadeIn), Duration.ofTicks(stay), Duration.ofTicks(fadeOut));

        for (Player player : recipients) {
            Component title = plugin.getLocaleManager().getComponentFromString(titleStr, player);
            Component subtitle = plugin.getLocaleManager().getComponentFromString(subtitleStr, player);
            player.showTitle(Title.title(title, subtitle, times));
        }
    }

    private void sendActionBarAnnouncement(Collection<? extends Player> recipients, Map<?, ?> data) {
        String message = (String) data.get("message");
        if (message == null) return;

        for (Player player : recipients) {
            Component component = plugin.getLocaleManager().getComponentFromString(message, player);
            player.sendActionBar(component);
        }
    }

    private void sendBossBarAnnouncement(Collection<? extends Player> recipients, Map<?, ?> data) {
        String message = (String) data.get("message");
        if (message == null) return;

        BarColor color = BarColor.valueOf(((String) data.getOrDefault("color", "YELLOW")).toUpperCase());
        BarStyle style = BarStyle.valueOf(((String) data.getOrDefault("style", "SOLID")).toUpperCase());
        int duration = Formatter.parseInt(data.getOrDefault("duration", 10).toString(), 10);

        for (Player player : recipients) {
            plugin.getBossBarManager().createBossBar(player, message, color, style, duration);
        }
    }

    public void cancelTasks() {
        if (announcementTask != null && !announcementTask.isCancelled()) {
            announcementTask.cancel();
        }
    }
}
