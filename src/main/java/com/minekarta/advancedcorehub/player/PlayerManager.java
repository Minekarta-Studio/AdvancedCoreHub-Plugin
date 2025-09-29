package com.minekarta.advancedcorehub.player;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.database.IDataManager;
import com.minekarta.advancedcorehub.util.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PlayerManager implements IPlayerManager {

    private final AdvancedCoreHub plugin;
    private final IDataManager dataManager;
    private final Map<UUID, PlayerData> playerDataCache = new ConcurrentHashMap<>();

    public PlayerManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loadPlayerDataAsync(player.getUniqueId(), player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerDataAsync(player.getUniqueId()).thenRun(() -> {
            playerDataCache.remove(player.getUniqueId());
            plugin.getLogger().info("Removed PlayerData for " + player.getName() + " from cache.");
        });
    }

    @Override
    public Optional<PlayerData> getPlayerData(UUID uuid) {
        return Optional.ofNullable(playerDataCache.get(uuid));
    }

    private void loadPlayerDataAsync(UUID uuid, String username) {
        CompletableFuture.runAsync(() -> {
            String sql = "SELECT * FROM " + Constants.PLAYER_DATA_TABLE + " WHERE uuid = ?;";

            try (Connection conn = dataManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();

                PlayerData playerData;
                if (rs.next()) {
                    // Player exists in the database
                    playerData = new PlayerData(uuid, username);
                    playerData.setLastSeen(System.currentTimeMillis());
                    playerData.setFlyEnabled(rs.getInt("fly_enabled") == 1);
                    playerData.setDoubleJumpEnabled(rs.getInt("double_jump_enabled") == 1);
                    plugin.getLogger().info("Loaded data for existing player: " + username);
                } else {
                    // New player, create default data
                    playerData = new PlayerData(uuid, username);
                    plugin.getLogger().info("Created new data profile for player: " + username);
                }
                playerDataCache.put(uuid, playerData);

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load player data for " + username, e);
                // Optionally, kick the player if data is critical
                // plugin.getServer().getScheduler().runTask(plugin, () -> player.kickPlayer("Could not load your data."));
            }
        });
    }

    private CompletableFuture<Void> savePlayerDataAsync(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            Optional<PlayerData> playerDataOpt = getPlayerData(uuid);
            if (!playerDataOpt.isPresent()) {
                plugin.getLogger().warning("Could not save player data for " + uuid + ", not found in cache.");
                return;
            }

            PlayerData data = playerDataOpt.get();
            String sql = "INSERT OR REPLACE INTO " + Constants.PLAYER_DATA_TABLE
                    + " (uuid, username, last_seen, fly_enabled, double_jump_enabled) VALUES (?, ?, ?, ?, ?);";

            try (Connection conn = dataManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, data.getUuid().toString());
                ps.setString(2, data.getUsername());
                ps.setLong(3, System.currentTimeMillis());
                ps.setInt(4, data.isFlyEnabled() ? 1 : 0);
                ps.setInt(5, data.isDoubleJumpEnabled() ? 1 : 0);

                ps.executeUpdate();
                plugin.getLogger().info("Successfully saved data for " + data.getUsername());

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + data.getUsername(), e);
            }
        });
    }
}