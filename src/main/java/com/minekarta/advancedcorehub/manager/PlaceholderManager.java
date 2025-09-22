package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends PlaceholderExpansion {

    private final AdvancedCoreHub plugin;

    public PlaceholderManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "Minekarta";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "advancedcorehub";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] parts = params.split("_");
        if (parts.length < 2) {
            return null;
        }

        String type = parts[0];
        String serverName = parts[1];

        int playerCount = plugin.getServerInfoManager().getPlayerCount(serverName);

        if (type.equalsIgnoreCase("players")) {
            return playerCount >= 0 ? String.valueOf(playerCount) : "Offline";
        }

        if (type.equalsIgnoreCase("status")) {
            return playerCount >= 0 ? "<green>Online</green>" : "<red>Offline</red>";
        }

        return null;
    }
}
