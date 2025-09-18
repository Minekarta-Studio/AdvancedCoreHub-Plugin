package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerVisibilityManager {

    private final AdvancedCoreHub plugin;
    private final Set<UUID> hidingPlayers = new HashSet<>();

    public PlayerVisibilityManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public boolean isHidingPlayers(Player player) {
        return hidingPlayers.contains(player.getUniqueId());
    }

    public void togglePlayerVisibility(Player player) {
        if (isHidingPlayers(player)) {
            showAllPlayers(player);
        } else {
            hideAllPlayers(player);
        }
    }

    public void hideAllPlayers(Player player) {
        hidingPlayers.add(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!player.equals(onlinePlayer)) {
                player.hidePlayer(plugin, onlinePlayer);
            }
        }
        plugin.getLocaleManager().sendMessage(player, "player-hider-hidden");
    }

    public void showAllPlayers(Player player) {
        hidingPlayers.remove(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!player.equals(onlinePlayer)) {
                player.showPlayer(plugin, onlinePlayer);
            }
        }
        plugin.getLocaleManager().sendMessage(player, "player-hider-shown");
    }

    public void handlePlayerJoin(Player joinedPlayer) {
        // Hide the newly joined player from anyone who has hiding enabled
        for (UUID hidingPlayerUUID : hidingPlayers) {
            Player hidingPlayer = Bukkit.getPlayer(hidingPlayerUUID);
            if (hidingPlayer != null) {
                hidingPlayer.hidePlayer(plugin, joinedPlayer);
            }
        }
    }
}
