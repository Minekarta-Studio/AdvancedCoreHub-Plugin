package com.minekarta.advancedcorehub.player;

import java.util.UUID;

/**
 * A data object representing a player's persistent data.
 */
public class PlayerData {

    private final UUID uuid;
    private String username;
    private long lastSeen;

    // Feature-specific settings
    private boolean flyEnabled;
    private boolean doubleJumpEnabled;

    public PlayerData(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.lastSeen = System.currentTimeMillis();
        // Default values for a new player
        this.flyEnabled = false;
        this.doubleJumpEnabled = true;
    }

    // --- Getters ---

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public boolean isFlyEnabled() {
        return flyEnabled;
    }

    public boolean isDoubleJumpEnabled() {
        return doubleJumpEnabled;
    }

    // --- Setters ---

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setFlyEnabled(boolean flyEnabled) {
        this.flyEnabled = flyEnabled;
    }

    public void setDoubleJumpEnabled(boolean doubleJumpEnabled) {
        this.doubleJumpEnabled = doubleJumpEnabled;
    }
}