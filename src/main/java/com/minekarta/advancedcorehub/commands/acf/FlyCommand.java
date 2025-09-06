package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import com.minekarta.advancedcorehub.util.Permissions;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandAlias("fly")
@CommandPermission(Permissions.CMD_FLY)
@Description("Toggle your flight mode, optionally for a duration.")
public class FlyCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private LocaleManager localeManager;

    private final Map<UUID, BukkitTask> timedFlyTasks = new HashMap<>();
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhd])");

    @Default
    @CommandCompletion("@players|@durations @durations")
    @Syntax("[target] [duration]")
    public void onFly(CommandSender sender, @Optional String targetOrDuration, @Optional String durationStr) {
        Player target = null;
        long durationTicks = -1;
        String durationToParse = null;

        if (sender instanceof Player && targetOrDuration == null) {
            // /fly
            target = (Player) sender;
        } else if (targetOrDuration != null) {
            Player potentialTarget = plugin.getServer().getPlayer(targetOrDuration);
            if (potentialTarget != null) {
                // /fly <player> [duration]
                target = potentialTarget;
                durationToParse = durationStr;
            } else if (sender instanceof Player) {
                // /fly <duration>
                target = (Player) sender;
                durationToParse = targetOrDuration;
            }
        }

        if (target == null) {
            localeManager.sendMessage(sender, "player-not-found", targetOrDuration);
            return;
        }

        if (sender != target && !sender.hasPermission(Permissions.CMD_FLY_OTHERS)) {
            localeManager.sendMessage(sender, "no-permission");
            return;
        }

        if (durationToParse != null) {
            durationTicks = parseDuration(durationToParse);
            if (durationTicks < 0) {
                localeManager.sendMessage(sender, "invalid-duration-format", durationToParse);
                return;
            }
        }

        toggleFlight(sender, target, durationTicks);
    }

    private void toggleFlight(CommandSender sender, Player target, long durationTicks) {
        boolean isEnabling = !target.getAllowFlight();

        if (isEnabling) {
            target.setAllowFlight(true);
            target.setFlying(true);

            if (durationTicks > 0) {
                long durationMillis = durationTicks * 50; // Ticks to milliseconds
                long endTime = System.currentTimeMillis() + durationMillis;
                target.getPersistentDataContainer().set(PersistentKeys.FLY_EXPIRATION, PersistentDataType.LONG, endTime);

                localeManager.sendMessage(target, "fly-toggled-timed-self", formatDuration(durationMillis));
                if (sender != target) localeManager.sendMessage(sender, "fly-toggled-timed-other", target.getName(), formatDuration(durationMillis));

                if (timedFlyTasks.containsKey(target.getUniqueId())) {
                    timedFlyTasks.get(target.getUniqueId()).cancel();
                }

                BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (target.isOnline() && target.getAllowFlight()) {
                        target.setAllowFlight(false);
                        target.setFlying(false);
                        target.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);
                        localeManager.sendMessage(target, "fly-expired");
                    }
                    timedFlyTasks.remove(target.getUniqueId());
                }, durationTicks);
                timedFlyTasks.put(target.getUniqueId(), task);

            } else { // Permanent flight
                target.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);
                localeManager.sendMessage(target, "fly-toggled-self", "enabled");
                if (sender != target) localeManager.sendMessage(sender, "fly-toggled-other", "enabled", target.getName());
            }
        } else { // Disabling
            target.setAllowFlight(false);
            target.setFlying(false);
            target.getPersistentDataContainer().remove(PersistentKeys.FLY_EXPIRATION);

            if (timedFlyTasks.containsKey(target.getUniqueId())) {
                timedFlyTasks.get(target.getUniqueId()).cancel();
                timedFlyTasks.remove(target.getUniqueId());
            }

            localeManager.sendMessage(target, "fly-toggled-self", "disabled");
            if (sender != target) localeManager.sendMessage(sender, "fly-toggled-other", "disabled", target.getName());
        }
    }

    private long parseDuration(String durationStr) {
        if (durationStr == null) return -1;
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
}
