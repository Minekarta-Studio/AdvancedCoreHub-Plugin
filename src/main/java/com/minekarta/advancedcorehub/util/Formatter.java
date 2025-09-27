package com.minekarta.advancedcorehub.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("&([0-9a-fk-orA-FK-OR])");

    /**
     * Formats a string with MiniMessage and PlaceholderAPI placeholders.
     * It also translates legacy color codes (&c) to MiniMessage format.
     *
     * @param player The player to parse placeholders for, can be null.
     * @param text The text to format.
     * @return The formatted component.
     */
    public static Component format(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        // Translate legacy codes after placeholders are parsed.
        text = translateLegacyCodes(text);

        // Deserialize the text and then remove the italic decoration.
        return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Translates legacy color codes (e.g., &c) into MiniMessage tags (e.g., <red>).
     *
     * @param text The string to translate.
     * @return The translated string.
     */
    private static String translateLegacyCodes(String text) {
        if (text == null) {
            return "";
        }
        Matcher matcher = LEGACY_COLOR_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, getMiniMessageTag(matcher.group(1).toLowerCase()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Maps a legacy color code character to its MiniMessage tag equivalent.
     *
     * @param code The legacy code character (e.g., "c", "l").
     * @return The corresponding MiniMessage tag (e.g., "<red>", "<bold>").
     */
    private static String getMiniMessageTag(String code) {
        switch (code) {
            case "0": return "<black>";
            case "1": return "<dark_blue>";
            case "2": return "<dark_green>";
            case "3": return "<dark_aqua>";
            case "4": return "<dark_red>";
            case "5": return "<dark_purple>";
            case "6": return "<gold>";
            case "7": return "<gray>";
            case "8": return "<dark_gray>";
            case "9": return "<blue>";
            case "a": return "<green>";
            case "b": return "<aqua>";
            case "c": return "<red>";
            case "d": return "<light_purple>";
            case "e": return "<yellow>";
            case "f": return "<white>";
            case "k": return "<obfuscated>";
            case "l": return "<bold>";
            case "m": return "<strikethrough>";
            case "n": return "<underline>";
            case "o": return "<italic>";
            case "r": return "<reset>";
            default: return "&" + code; // Should not be reached due to regex
        }
    }


    /**
     * Formats a string with MiniMessage, without player-specific placeholders.
     *
     * @param text The text to format.
     * @return The formatted component.
     */
    public static Component format(String text) {
        return format(null, text);
    }

    /**
     * Safely parses an integer from a string.
     *
     * @param input The string to parse.
     * @param defaultValue The value to return if parsing fails.
     * @return The parsed integer or the default value.
     */
    public static int parseInt(String input, int defaultValue) {
        if (input == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
