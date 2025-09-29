package com.minekarta.advancedcorehub.database;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Constants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class DataManager implements IDataManager {

    private final AdvancedCoreHub plugin;
    private HikariDataSource dataSource;

    public DataManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initDatabase() {
        // Run database initialization asynchronously to avoid blocking the main thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::setupDataSourceAndTables);
    }

    private void setupDataSourceAndTables() {
        try {
            // Ensure the database file exists
            File dbFile = new File(plugin.getDataFolder(), Constants.DATABASE_FILE_NAME);
            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Could not create database file!", e);
                    return;
                }
            }

            // Configure HikariCP for SQLite
            HikariConfig config = new HikariConfig();
            config.setPoolName("AdvancedCoreHub-Pool");
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            config.setConnectionTestQuery("SELECT 1");
            config.setMaxLifetime(60000); // 60 seconds
            config.setIdleTimeout(45000); // 45 seconds
            config.setMaximumPoolSize(10);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            this.dataSource = new HikariDataSource(config);
            plugin.getLogger().info("HikariCP connection pool initialized for SQLite.");

            // Create player data table if it doesn't exist
            createTables();

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize the database connection pool.", e);
        }
    }

    private void createTables() {
        // SQL statement for creating the player data table
        // This is a simple example; more columns can be added as needed.
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + Constants.PLAYER_DATA_TABLE + " ("
                + "uuid TEXT PRIMARY KEY NOT NULL,"
                + "username TEXT,"
                + "last_seen INTEGER NOT NULL,"
                + "fly_enabled INTEGER DEFAULT 0,"
                + "double_jump_enabled INTEGER DEFAULT 1"
                + ");";

        // Use try-with-resources to ensure the connection and statement are closed
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(createTableSQL)) {
            ps.execute();
            plugin.getLogger().info("Database tables verified/created successfully.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create database tables.", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection is not available.");
        }
        return dataSource.getConnection();
    }

    @Override
    public void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool closed.");
        }
    }
}