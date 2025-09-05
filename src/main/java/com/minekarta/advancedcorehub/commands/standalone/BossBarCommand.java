package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BossBarCommand implements CommandExecutor, TabCompleter {

    private final AdvancedCoreHub plugin;

    public BossBarCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(Permissions.CMD_BOSSBAR)) {
            plugin.getLocaleManager().sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                handleCreate(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            case "set":
                handleSet(sender, args);
                break;
            default:
                sendHelpMessage(sender);
                break;
        }

        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 5) {
            plugin.getLocaleManager().sendMessage(sender, "invalid-usage", "/bossbar create <player|@a> <color> <style> <title...>");
            return;
        }

        String targetSelector = args[1];
        BarColor color;
        BarStyle style;

        try {
            color = BarColor.valueOf(args[2].toUpperCase());
            style = BarStyle.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("No enum constant org.bukkit.boss.BarColor")) {
                plugin.getLocaleManager().sendMessage(sender, "bossbar-invalid-color", args[2]);
            } else {
                plugin.getLocaleManager().sendMessage(sender, "bossbar-invalid-style", args[3]);
            }
            return;
        }

        String title = String.join(" ", Arrays.copyOfRange(args, 4, args.length));

        if (targetSelector.equalsIgnoreCase("@a")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.getBossBarManager().createBossBar(player, title, color, style);
            }
            plugin.getLocaleManager().sendMessage(sender, "bossbar-created", "all players");
        } else {
            Player target = Bukkit.getPlayer(targetSelector);
            if (target == null) {
                plugin.getLocaleManager().sendMessage(sender, "player-not-found", targetSelector);
                return;
            }
            plugin.getBossBarManager().createBossBar(target, title, color, style);
            plugin.getLocaleManager().sendMessage(sender, "bossbar-created", target.getName());
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getLocaleManager().sendMessage(sender, "invalid-usage", "/bossbar remove <player|@a>");
            return;
        }

        String targetSelector = args[1];

        if (targetSelector.equalsIgnoreCase("@a")) {
            // This is a bit tricky as we don't have a list of all players with bars.
            // We'll just iterate all online players.
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.getBossBarManager().removeBossBar(player);
            }
            plugin.getLocaleManager().sendMessage(sender, "bossbar-removed", "all players");
        } else {
            Player target = Bukkit.getPlayer(targetSelector);
            if (target == null) {
                plugin.getLocaleManager().sendMessage(sender, "player-not-found", targetSelector);
                return;
            }
            plugin.getBossBarManager().removeBossBar(target);
            plugin.getLocaleManager().sendMessage(sender, "bossbar-removed", target.getName());
        }
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sendHelpMessage(sender);
            return;
        }

        String targetSelector = args[1];
        String property = args[2].toLowerCase();
        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        // Simplified target processing for now, can be expanded later
        Player target = Bukkit.getPlayer(targetSelector);
        if (target == null) {
            plugin.getLocaleManager().sendMessage(sender, "player-not-found", targetSelector);
            return;
        }

        switch (property) {
            case "title":
                plugin.getBossBarManager().updateTitle(target, value);
                plugin.getLocaleManager().sendMessage(sender, "bossbar-set-success", property, value, target.getName());
                break;
            case "color":
                try {
                    BarColor color = BarColor.valueOf(value.toUpperCase());
                    plugin.getBossBarManager().updateColor(target, color);
                    plugin.getLocaleManager().sendMessage(sender, "bossbar-set-success", property, value, target.getName());
                } catch (IllegalArgumentException e) {
                    plugin.getLocaleManager().sendMessage(sender, "bossbar-invalid-color", value);
                }
                break;
            case "style":
                try {
                    BarStyle style = BarStyle.valueOf(value.toUpperCase());
                    plugin.getBossBarManager().updateStyle(target, style);
                    plugin.getLocaleManager().sendMessage(sender, "bossbar-set-success", property, value, target.getName());
                } catch (IllegalArgumentException e) {
                    plugin.getLocaleManager().sendMessage(sender, "bossbar-invalid-style", value);
                }
                break;
            default:
                plugin.getLocaleManager().sendMessage(sender, "bossbar-invalid-property", property);
        }
    }


    private void sendHelpMessage(CommandSender sender) {
        plugin.getLocaleManager().sendMessage(sender, "bossbar-help-header");
        plugin.getLocaleManager().sendMessage(sender, "bossbar-help-create");
        plugin.getLocaleManager().sendMessage(sender, "bossbar-help-remove");
        plugin.getLocaleManager().sendMessage(sender, "bossbar-help-set");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("create", "remove", "set").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set"))) {
            List<String> suggestions = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove")) {
                suggestions.add("@a");
            }
            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return Arrays.stream(BarColor.values())
                    .map(Enum::name)
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return List.of("title", "color", "style").stream()
                    .filter(s -> s.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            return Arrays.stream(BarStyle.values())
                    .map(Enum::name)
                    .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
            if (args[2].equalsIgnoreCase("color")) {
                return Arrays.stream(BarColor.values())
                        .map(Enum::name)
                        .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args[2].equalsIgnoreCase("style")) {
                return Arrays.stream(BarStyle.values())
                        .map(Enum::name)
                        .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}
