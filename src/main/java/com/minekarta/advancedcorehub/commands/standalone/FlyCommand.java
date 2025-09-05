package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.FlyManager;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FlyCommand implements CommandExecutor, TabCompleter {

    private final AdvancedCoreHub plugin;
    private final FlyManager flyManager;

    public FlyCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.flyManager = plugin.getFlyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player target = null;
        int duration = -1; // -1 indicates permanent flight

        // Determine target and duration from args
        if (args.length == 0) { // /fly
            if (!(sender instanceof Player)) {
                plugin.getLocaleManager().sendMessage(sender, "players-only");
                return true;
            }
            target = (Player) sender;
        } else if (args.length == 1) { // /fly <player> OR /fly <duration>
            target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                // It's /fly <player>
                if (!sender.hasPermission(Permissions.CMD_FLY_OTHERS)) {
                    plugin.getLocaleManager().sendMessage(sender, "no-permission");
                    return true;
                }
            } else {
                // Not a player, try parsing as duration for self
                if (!(sender instanceof Player)) {
                    plugin.getLocaleManager().sendMessage(sender, "players-only");
                    return true;
                }
                try {
                    duration = Integer.parseInt(args[0]);
                    target = (Player) sender;
                } catch (NumberFormatException e) {
                    plugin.getLocaleManager().sendMessage(sender, "player-not-found", args[0]);
                    return true;
                }
            }
        } else if (args.length == 2) { // /fly <player> <duration>
            if (!sender.hasPermission(Permissions.CMD_FLY_OTHERS)) {
                plugin.getLocaleManager().sendMessage(sender, "no-permission");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                plugin.getLocaleManager().sendMessage(sender, "player-not-found", args[0]);
                return true;
            }
            try {
                duration = Integer.parseInt(args[1]);
                if (duration <= 0) {
                     plugin.getLocaleManager().sendMessage(sender, "invalid-number", args[1]);
                     return true;
                }
            } catch (NumberFormatException e) {
                plugin.getLocaleManager().sendMessage(sender, "invalid-number", args[1]);
                return true;
            }
        } else {
            sender.sendMessage("Usage: /fly [player] [duration]");
            return true;
        }

        // Permission check for self-fly
        if (target == sender && !sender.hasPermission(Permissions.CMD_FLY)) {
            plugin.getLocaleManager().sendMessage(sender, "no-permission");
            return true;
        }

        // Execute the toggle
        flyManager.toggleFlight(target, duration);

        // Send confirmation messages
        boolean isNowFlying = target.getAllowFlight();
        String status = isNowFlying ? "enabled" : "disabled";

        if (sender == target) {
            if (duration > 0 && isNowFlying) {
                plugin.getLocaleManager().sendMessage(sender, "fly-toggled-self-temp", status, duration);
            } else {
                plugin.getLocaleManager().sendMessage(sender, "fly-toggled-self", status);
            }
        } else {
            if (duration > 0 && isNowFlying) {
                plugin.getLocaleManager().sendMessage(target, "fly-toggled-by-other-temp", status, sender.getName(), duration);
                plugin.getLocaleManager().sendMessage(sender, "fly-toggled-other-temp", status, target.getName(), duration);
            } else {
                plugin.getLocaleManager().sendMessage(target, "fly-toggled-by-other", status, sender.getName());
                plugin.getLocaleManager().sendMessage(sender, "fly-toggled-other", status, target.getName());
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(Permissions.CMD_FLY_OTHERS)) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
