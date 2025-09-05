package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
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

    public FlyCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getLocaleManager().sendMessage(sender, "players-only");
                return true;
            }
            Player player = (Player) sender;
            toggleFlight(player, player);
            return true;
        }

        if (!sender.hasPermission(Permissions.CMD_FLY_OTHERS)) {
            plugin.getLocaleManager().sendMessage(sender, "no-permission");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getLocaleManager().sendMessage(sender, "player-not-found", args[0]);
            return true;
        }

        toggleFlight(sender, target);
        return true;
    }

    private void toggleFlight(CommandSender sender, Player target) {
        boolean isFlying = !target.getAllowFlight();
        target.setAllowFlight(isFlying);
        target.setFlying(isFlying);

        String status = isFlying ? "enabled" : "disabled";

        if (sender == target) {
            plugin.getLocaleManager().sendMessage(target, "fly-toggled-self", status);
        } else {
            plugin.getLocaleManager().sendMessage(target, "fly-toggled-by-other", status, sender.getName());
            plugin.getLocaleManager().sendMessage(sender, "fly-toggled-other", status, target.getName());
        }
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
