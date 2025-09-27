package com.minekarta.advancedcorehub.config;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnnouncementConfig {

    public final String type;
    public final String message;
    public final List<String> worlds;
    public final String title;
    public final String subtitle;
    public final int fadeIn;
    public final int stay;
    public final int fadeOut;
    public final BarColor bossBarColor;
    public final BarStyle bossBarStyle;
    public final int bossBarDuration;

    @SuppressWarnings("unchecked")
    public AnnouncementConfig(Map<?, ?> map) {
        Object typeObj = map.get("type");
        this.type = (typeObj != null) ? typeObj.toString() : "CHAT";

        Object messageObj = map.get("message");
        this.message = (messageObj != null) ? messageObj.toString() : null;

        Object worldsObj = map.get("worlds");
        if (worldsObj instanceof List) {
            this.worlds = (List<String>) worldsObj;
        } else {
            this.worlds = Collections.emptyList();
        }

        Object titleObj = map.get("title");
        this.title = (titleObj != null) ? titleObj.toString() : null;

        Object subtitleObj = map.get("subtitle");
        this.subtitle = (subtitleObj != null) ? subtitleObj.toString() : null;

        this.fadeIn = getInt(map, "fade-in", 10);
        this.stay = getInt(map, "stay", 70);
        this.fadeOut = getInt(map, "fade-out", 20);
        this.bossBarDuration = getInt(map, "duration", 10);

        this.bossBarColor = getEnumValue(BarColor.class, map, "color", "YELLOW");
        this.bossBarStyle = getEnumValue(BarStyle.class, map, "style", "SOLID");
    }

    private int getInt(Map<?, ?> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    private <T extends Enum<T>> T getEnumValue(Class<T> enumClass, Map<?, ?> map, String key, String defaultValue) {
        Object value = map.get(key);
        String stringValue = (value != null) ? value.toString().toUpperCase() : defaultValue.toUpperCase();
        try {
            return Enum.valueOf(enumClass, stringValue);
        } catch (IllegalArgumentException e) {
            return Enum.valueOf(enumClass, defaultValue.toUpperCase());
        }
    }
}