package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocaleManager {

    private final AdvancedCoreHub plugin;
    private final FileManager fileManager;
    private String defaultLang;
    private boolean papiEnabled = false;

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public LocaleManager(AdvancedCoreHub plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    public void load() {
        this.defaultLang = plugin.getConfig().getString("language", "en");
        this.papiEnabled = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (papiEnabled) {
            plugin.getLogger().info("PlaceholderAPI found, placeholder support enabled.");
        } else {
            plugin.getLogger().info("PlaceholderAPI not found, using basic placeholder replacement.");
        }
    }

    public String get(String key, Player player, Object... placeholders) {
        String lang = (player != null) ? player.getLocale().substring(0, 2) : defaultLang;
        FileConfiguration langFile = fileManager.getConfig("languages/" + lang + ".yml");

        if (langFile == null) {
            langFile = fileManager.getConfig("languages/" + defaultLang + ".yml");
        }

        String message = langFile.getString(key, "Missing translation for key: " + key);

        // Replace custom placeholders
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(placeholders[i]));
        }

        // Apply PAPI placeholders
        if (papiEnabled && player != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        return translateColors(message);
    }

    public void sendMessage(CommandSender sender, String key, Object... placeholders) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        String message = get(key, player, placeholders);

        // Modern component-based sending for players
        if (player != null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(toMiniMessage(message)));
        } else {
            // Legacy for console
            sender.sendMessage(message);
        }
    }

    private String translateColors(String message) {
        if (message == null) return "";
        // Translate & color codes
        message = ChatColor.translateAlternateColorCodes('&', message);

        // Translate hex color codes like &#RRGGBB
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private String toMiniMessage(String legacyText) {
        // A simple converter from legacy with '&' and custom hex to MiniMessage format
        legacyText = legacyText.replace('§', '&');
        Matcher matcher = HEX_PATTERN.matcher(legacyText);
        while(matcher.find()){
            legacyText = legacyText.replace(matcher.group(0), "<#" + matcher.group(1) + ">");
        }
        return legacyText.replaceAll("&([0-9a-fk-or])", "<$1>");
    }

    public Component getComponent(String key, Player player, Object... placeholders) {
        String message = get(key, player, placeholders);
        return MiniMessage.miniMessage().deserialize(toMiniMessage(message));
    }
}
