package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocaleManager {

    private final AdvancedCoreHub plugin;
    private final FileManager fileManager;
    private String defaultLang;
    private boolean papiEnabled = false;

    // Pattern to convert legacy hex codes &#RRGGBB to MiniMessage format <#RRGGBB>
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public LocaleManager(AdvancedCoreHub plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    public void load() {
        this.defaultLang = plugin.getConfig().getString("language", "en");
        this.papiEnabled = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (papiEnabled) {
            plugin.getLogger().info("PlaceholderAPI found, MiniMessage placeholder support enabled.");
        } else {
            plugin.getLogger().info("PlaceholderAPI not found, using basic placeholder replacement.");
        }
    }

    /**
     * Gets the raw, untranslated message string from the appropriate language file.
     *
     * @param key The key of the message.
     * @param player The player, used to determine the locale.
     * @return The raw message string.
     */
    public String getRaw(String key, Player player) {
        String lang = (player != null) ? player.getLocale().substring(0, 2) : defaultLang;
        FileConfiguration langFile = fileManager.getConfig("languages/" + lang + ".yml");

        if (langFile == null) {
            langFile = fileManager.getConfig("languages/" + defaultLang + ".yml");
        }

        return langFile.getString(key, "<red>Missing translation for key: " + key + "</red>");
    }

    /**
     * Formats a raw message string with placeholders and color codes into a Component.
     *
     * @param message The raw message string.
     * @param player The player to apply PlaceholderAPI placeholders for.
     * @param placeholders Custom placeholders to be replaced.
     * @return A formatted Component.
     */
    public Component format(String message, Player player, Object... placeholders) {
        // First, apply our custom indexed placeholders like {0}, {1}, etc.
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(placeholders[i]));
        }

        // Second, apply PlaceholderAPI placeholders if available
        if (papiEnabled && player != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        // For now, we will only support legacy ampersand codes, as the rest of the plugin uses them.
        // This provides stability. A future refactor could move the entire plugin to MiniMessage.
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    /**
     * Sends a formatted message to a CommandSender.
     *
     * @param sender The recipient of the message.
     * @param key The key of the message in the language files.
     * @param placeholders The placeholders to be inserted into the message.
     */
    public void sendMessage(CommandSender sender, String key, Object... placeholders) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        String rawMessage = getRaw(key, player);
        Component formattedComponent = format(rawMessage, player, placeholders);

        if (sender instanceof Player) {
            sender.sendMessage(formattedComponent);
        } else {
            // Send to console without MiniMessage formatting, but with legacy colors
            sender.sendMessage(LegacyComponentSerializer.builder().hexColors().build().serialize(formattedComponent));
        }
    }

    /**
     * Gets a formatted message as a Component.
     *
     * @param key The key of the message in the language files.
     * @param player The player for whom placeholders should be parsed.
     * @param placeholders The placeholders to be inserted into the message.
     * @return The formatted Component.
     */
    public Component getComponent(String key, Player player, Object... placeholders) {
        String rawMessage = getRaw(key, player);
        return format(rawMessage, player, placeholders);
    }

    /**
     * Gets a formatted message as a legacy string with '§' color codes.
     *
     * @param key The key of the message in the language files.
     * @param player The player for whom placeholders should be parsed.
     * @param placeholders The placeholders to be inserted into the message.
     * @return The formatted legacy string.
     */
    public String getLegacyString(String key, Player player, Object... placeholders) {
        Component component = getComponent(key, player, placeholders);
        return LegacyComponentSerializer.builder().hexColors().build().serialize(component);
    }
}
