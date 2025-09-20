package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Formatter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LocaleManager {

    private final AdvancedCoreHub plugin;
    private final FileManager fileManager;
    private String defaultLang;
    private String prefix;

    public LocaleManager(AdvancedCoreHub plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    public void load() {
        this.defaultLang = plugin.getConfig().getString("language", "en");
        this.prefix = plugin.getConfig().getString("messages.prefix", "<dark_aqua>[AdvancedCoreHub] </dark_aqua>");

        // PAPI check is now handled by the Formatter class, this is just for logging
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.getLogger().info("PlaceholderAPI found, placeholder support enabled in Formatter.");
        } else {
            plugin.getLogger().info("PlaceholderAPI not found, Formatter will skip PAPI placeholders.");
        }
    }

    private String getRawString(String key, Player player, Object... placeholders) {
        // Determine language from player or default
        String lang = (player != null && player.getLocale() != null && player.getLocale().length() >= 2) ? player.getLocale().substring(0, 2) : defaultLang;
        FileConfiguration langFile = fileManager.getConfig("languages/" + lang + ".yml");

        // Fallback to default language if player's language file doesn't exist
        if (langFile == null) {
            langFile = fileManager.getConfig("languages/" + defaultLang + ".yml");
        }

        String message = langFile.getString(key, "<red>Missing translation for key: " + key + "</red>");

        // Replace custom placeholders like {0}, {1}, etc.
        if (message != null) {
            for (int i = 0; i < placeholders.length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(placeholders[i]));
            }
        }

        return message;
    }

    /**
     * Sends a formatted message to a CommandSender.
     *
     * @param sender       The sender to receive the message.
     * @param key          The translation key from the language file.
     * @param placeholders The placeholders to insert into the message.
     */
    public void sendMessage(CommandSender sender, String key, Object... placeholders) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        String rawMessage = getRawString(key, player, placeholders);

        // Don't send empty messages
        if (rawMessage == null || rawMessage.isEmpty()) {
            return;
        }

        String finalMessage = prefix + rawMessage;
        Component formattedComponent = Formatter.format(player, finalMessage);
        sender.sendMessage(formattedComponent);
    }

    /**
     * Sends a formatted message without the global prefix.
     *
     * @param sender       The sender to receive the message.
     * @param key          The translation key from the language file.
     * @param placeholders The placeholders to insert into the message.
     */
    public void sendMessageNoPrefix(CommandSender sender, String key, Object... placeholders) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        String rawMessage = getRawString(key, player, placeholders);
        Component formattedComponent = Formatter.format(player, rawMessage);
        sender.sendMessage(formattedComponent);
    }

    /**
     * Gets a formatted Component for a given translation key.
     *
     * @param key          The translation key from the language file.
     * @param player       The player for whom to parse placeholders (can be null).
     * @param placeholders The placeholders to insert into the message.
     * @return The formatted Component.
     */
    public Component getComponent(String key, Player player, Object... placeholders) {
        String rawMessage = getRawString(key, player, placeholders);
        return Formatter.format(player, rawMessage);
    }

    /**
     * Gets a formatted Component from a direct string, not a language key.
     *
     * @param text         The string to format.
     * @param player       The player for whom to parse placeholders (can be null).
     * @return The formatted Component.
     */
    public Component getComponentFromString(String text, Player player) {
        return Formatter.format(player, text);
    }

    public java.util.List<Component> getComponentList(java.util.List<String> lines, Player player) {
        java.util.List<Component> componentList = new java.util.ArrayList<>();
        for (String line : lines) {
            componentList.add(getComponentFromString(line, player));
        }
        return componentList;
    }

    public String getPrefix() {
        return prefix;
    }
}
