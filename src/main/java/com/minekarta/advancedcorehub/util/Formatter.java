package com.minekarta.advancedcorehub.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Formatter {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Formats a string with MiniMessage and PlaceholderAPI placeholders.
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

        // Deserialize the text and then remove the italic decoration.
        return MINI_MESSAGE.deserialize(text).decoration(TextDecoration.ITALIC, false);
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
