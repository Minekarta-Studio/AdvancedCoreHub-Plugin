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

    public void load() {
        cancelTasks();

        ConfigurationSection config = plugin.getConfig().getConfigurationSection("announcements");
        if (config == null || !config.getBoolean("enabled", false)) {
            return;
        }

        this.interval = config.getInt("interval_seconds", 60);
        this.randomized = config.getBoolean("randomized", false);

        this.announcements = config.getMapList("messages");

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

        Object typeObj = announcementData.get("type");
        String type = (typeObj != null) ? typeObj.toString().toUpperCase() : "CHAT";

        @SuppressWarnings("unchecked")
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

        String prefix = plugin.getLocaleManager().getPrefix();
        String finalMessage = prefix + message;

        // Chat messages are sent globally to recipients, placeholders that are not player-specific will work
        Component component = plugin.getLocaleManager().getComponentFromString(finalMessage, null);
        recipients.forEach(player -> player.sendMessage(component));
    }

    private void sendTitleAnnouncement(Collection<? extends Player> recipients, Map<?, ?> data) {
        String titleStr = (String) data.get("title");
        String subtitleStr = (String) data.get("subtitle");

        if (titleStr == null && subtitleStr == null) {
            plugin.getLogger().warning("Title announcement is missing both title and subtitle.");
            return;
        }

        Object fadeInObj = data.get("fade-in");
        int fadeIn = (fadeInObj != null) ? Formatter.parseInt(fadeInObj.toString(), 10) : 10;

        Object stayObj = data.get("stay");
        int stay = (stayObj != null) ? Formatter.parseInt(stayObj.toString(), 70) : 70;

        Object fadeOutObj = data.get("fade-out");
        int fadeOut = (fadeOutObj != null) ? Formatter.parseInt(fadeOutObj.toString(), 20) : 20;

        Title.Times times = Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L));

        for (Player player : recipients) {
            Component title = titleStr != null ? plugin.getLocaleManager().getComponentFromString(titleStr, player) : Component.empty();
            Component subtitle = subtitleStr != null ? plugin.getLocaleManager().getComponentFromString(subtitleStr, player) : Component.empty();
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

        Object colorObj = data.get("color");
        String colorStr = (colorObj != null) ? colorObj.toString().toUpperCase() : "YELLOW";
        BarColor color = BarColor.valueOf(colorStr);

        Object styleObj = data.get("style");
        String styleStr = (styleObj != null) ? styleObj.toString().toUpperCase() : "SOLID";
        BarStyle style = BarStyle.valueOf(styleStr);

        Object durationObj = data.get("duration");
        int duration = (durationObj != null) ? Formatter.parseInt(durationObj.toString(), 10) : 10;


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
