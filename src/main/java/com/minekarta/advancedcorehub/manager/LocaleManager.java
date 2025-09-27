package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.PluginConfig;
import com.minekarta.advancedcorehub.util.Formatter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LocaleManager {

    private final AdvancedCoreHub plugin;
    private final FileManager fileManager;
    private final PluginConfig config;
    private String defaultLang;
    private String prefix;

    public LocaleManager(AdvancedCoreHub plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.config = plugin.getPluginConfig();
    }

    public void load() {
        this.defaultLang = config.getLanguage();
        this.prefix = config.messages.prefix;

        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.getLogger().info("PlaceholderAPI found, placeholder support enabled in Formatter.");
        } else {
            plugin.getLogger().info("PlaceholderAPI not found, Formatter will skip PAPI placeholders.");
        }
    }

    private String getRawString(String key, Player player, Object... placeholders) {
        String lang = (player != null && player.getLocale() != null && player.getLocale().length() >= 2) ? player.getLocale().substring(0, 2) : defaultLang;
        FileConfiguration langFile = fileManager.getConfig("languages/" + lang + ".yml");

        if (langFile == null) {
            langFile = fileManager.getConfig("languages/" + defaultLang + ".yml");
        }

        String message = langFile.getString(key, "<red>Missing translation for key: " + key + "</red>");

        if (message != null) {
            for (int i = 0; i < placeholders.length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(placeholders[i]));
            }
        }

        return message;
    }

    public void sendMessage(CommandSender sender, String key, Object... placeholders) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        String rawMessage = getRawString(key, player, placeholders);

        if (rawMessage == null || rawMessage.isEmpty()) {
            return;
        }

        String finalMessage = prefix + rawMessage;
        Component formattedComponent = Formatter.format(player, finalMessage);
        sender.sendMessage(formattedComponent);
    }

    public void sendMessageNoPrefix(CommandSender sender, String key, Object... placeholders) {
        Player player = (sender instanceof Player) ? (Player) sender : null;
        String rawMessage = getRawString(key, player, placeholders);
        Component formattedComponent = Formatter.format(player, rawMessage);
        sender.sendMessage(formattedComponent);
    }

    public Component getComponent(String key, Player player, Object... placeholders) {
        String rawMessage = getRawString(key, player, placeholders);
        return Formatter.format(player, rawMessage);
    }

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