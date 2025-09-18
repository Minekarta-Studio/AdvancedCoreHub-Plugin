package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import fr.mrmicky.fastboard.FastBoard;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScoreboardManager {

    private final AdvancedCoreHub plugin;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private BukkitRunnable updateTask;
    private boolean placeholderApiHooked = false;

    public ScoreboardManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderApiHooked = true;
            plugin.getLogger().info("Successfully hooked into PlaceholderAPI for scoreboards.");
        }
        load();
    }

    public void load() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        FileConfiguration config = plugin.getFileManager().getConfig("scoreboard.yml");
        if (config == null || (!config.getBoolean("scoreboard.enabled", true) && !config.getBoolean("tablist.enabled", true))) {
            return;
        }

        long updateInterval = config.getLong("scoreboard.update_interval_ticks", 20L);

        this.updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (FastBoard board : boards.values()) {
                    updateBoard(board);
                }
                updateAllTabLists();
            }
        };
        updateTask.runTaskTimerAsynchronously(plugin, 0, updateInterval);
    }

    public void createBoard(Player player) {
        if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) return;

        FastBoard board = new FastBoard(player);
        boards.put(player.getUniqueId(), board);
        updateBoard(board);
    }

    public void removeBoard(Player player) {
        FastBoard board = boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    private String setPlaceholders(Player player, String text) {
        return placeholderApiHooked ? PlaceholderAPI.setPlaceholders(player, text) : text;
    }

    private void updateBoard(FastBoard board) {
        Player player = board.getPlayer();
        if (player == null || !player.isOnline()) {
            removeBoard(player);
            return;
        }

        if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
            board.updateLines(); // clear lines
            return;
        }

        FileConfiguration config = plugin.getFileManager().getConfig("scoreboard.yml");
        if (config == null || !config.getBoolean("scoreboard.enabled", true)) {
            board.delete();
            boards.remove(player.getUniqueId());
            return;
        }

        String title = config.getString("scoreboard.title", "");
        List<String> lines = config.getStringList("scoreboard.lines");

        board.updateTitle(setPlaceholders(player, title));

        List<String> processedLines = lines.stream()
                .map(line -> setPlaceholders(player, line))
                .collect(Collectors.toList());

        board.updateLines(processedLines);
    }

    private void updateAllTabLists() {
        FileConfiguration config = plugin.getFileManager().getConfig("scoreboard.yml");
        if (config == null || !config.getBoolean("tablist.enabled", true)) {
            return;
        }

        String headerStr = String.join("\n", config.getStringList("tablist.header"));
        String footerStr = String.join("\n", config.getStringList("tablist.footer"));

        for(Player player : Bukkit.getOnlinePlayers()){
            if (!plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) continue;

            String playerHeader = setPlaceholders(player, headerStr);
            String playerFooter = setPlaceholders(player, footerStr);

            Component header = plugin.getLocaleManager().getComponentFromString(playerHeader, player);
            Component footer = plugin.getLocaleManager().getComponentFromString(playerFooter, player);

            player.sendPlayerListHeaderAndFooter(header, footer);
        }
    }

    public void cleanup() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        for (FastBoard board : boards.values()) {
            board.delete();
        }
        boards.clear();
    }
}
