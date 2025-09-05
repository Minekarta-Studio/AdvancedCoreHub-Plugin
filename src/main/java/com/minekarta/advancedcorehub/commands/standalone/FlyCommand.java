package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlyCommand implements CommandExecutor, TabCompleter {

    private final AdvancedCoreHub plugin;
    private final Map<UUID, BukkitTask> timedFlyTasks = new HashMap<>();
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhd])");

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
            toggleFlight(sender, (Player) sender, -1);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        long duration = -1;

        if (target != null) { // /fly <player> [duration]
            if (!sender.hasPermission(Permissions.CMD_FLY_OTHERS)) {
                plugin.getLocaleManager().sendMessage(sender, "no-permission");
                return true;
            }
            if (args.length > 1) {
                duration = parseDuration(args[1]);
                if (duration < 0) {
                    plugin.getLocaleManager().sendMessage(sender, "invalid-duration-format", args[1]);
                    return true;
                }
            }
        } else { // /fly <duration> or /fly <player_that_is_offline>
            if (!(sender instanceof Player)) {
                plugin.getLocaleManager().sendMessage(sender, "players-only");
                return true;
            }
            duration = parseDuration(args[0]);
            if (duration > 0) {
                target = (Player) sender;
            } else {
                plugin.getLocaleManager().sendMessage(sender, "player-not-found", args[0]);
                return true;
            }
        }

        toggleFlight(sender, target, duration);
        return true;
    }

    public void toggleFlight(CommandSender sender, Player target, long durationTicks) {
        boolean isEnabling = !target.getAllowFlight();

        if (isEnabling) {
            target.setAllowFlight(true);
            target.setFlying(true);

            if (durationTicks > 0) {
                long durationMillis = durationTicks * 50; // Ticks to milliseconds
                long endTime = System.currentTimeMillis() + durationMillis;
                target.getPersistentDataContainer().set(PersistentKeys.FLY_EXPIRATION, PersistentDataType.LONG, endTime);

                plugin.getLocaleManager().sendMessage(target, "fly-toggled-timed-self", formatDuration(durationMillis));
                if(sender != target) plugin.getLocaleManager().sendMessage(sender, "fly-toggled-timed-other", target.getName(), formatDuration(durationMillis));

                // Cancel any existing task before starting a new one
                if (timedFlyTasks.containsKey(target.getUniqueId())) {
                    timedFlyTasks.get(target.getUniqueId()).cancel();
                }

                BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (target.isOnline() && target.getAllowFlight()) {
                        target.setAllowFlight(false);
                        target.setFlying(false);
                        target.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);
                        plugin.getLocaleManager().sendMessage(target, "fly-expired");
                    }
                    timedFlyTasks.remove(target.getUniqueId());
                }, durationTicks);
                timedFlyTasks.put(target.getUniqueId(), task);

            } else { // Permanent flight
                target.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);
                plugin.getLocaleManager().sendMessage(target, "fly-toggled-self", "enabled");
                 if(sender != target) plugin.getLocaleManager().sendMessage(sender, "fly-toggled-other", "enabled", target.getName());
            }
        } else { // Disabling
            target.setAllowFlight(false);
            target.setFlying(false);
            target.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);

            if (timedFlyTasks.containsKey(target.getUniqueId())) {
                timedFlyTasks.get(target.getUniqueId()).cancel();
                timedFlyTasks.remove(target.getUniqueId());
            }

            plugin.getLocaleManager().sendMessage(target, "fly-toggled-self", "disabled");
            if(sender != target) plugin.getLocaleManager().sendMessage(sender, "fly-toggled-other", "disabled", target.getName());
        }
    }

    private long parseDuration(String durationStr) {
        Matcher matcher = DURATION_PATTERN.matcher(durationStr.toLowerCase());
        if (!matcher.matches()) {
            return -1;
        }
        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);
        switch (unit) {
            case "s": return value * 20;
            case "m": return value * 60 * 20;
            case "h": return value * 60 * 60 * 20;
            case "d": return value * 24 * 60 * 60 * 20;
            default: return -1;
        }
    }

    private String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");

        return sb.toString().trim();
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> suggestions = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            suggestions.addAll(Arrays.asList("10s", "5m", "1h"));
            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && Bukkit.getPlayer(args[0]) != null && sender.hasPermission(Permissions.CMD_FLY_OTHERS)) {
             return Arrays.asList("10s", "5m", "1h").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
