package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private final AdvancedCoreHub plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void setVanished(Player player, boolean vanish) {
        if (vanish) {
            vanishedPlayers.add(player.getUniqueId());
            hidePlayer(player);
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            showPlayer(player);
        }
    }

    public void toggleVanish(Player player) {
        setVanished(player, !isVanished(player));
    }

    private void hidePlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("advancedcorehub.bypass.vanish")) {
                onlinePlayer.hidePlayer(plugin, player);
            }
        }
        plugin.getLocaleManager().sendMessage(player, "prefix", "<gray>You are now vanished.</gray>");
    }

    private void showPlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(plugin, player);
        }
        plugin.getLocaleManager().sendMessage(player, "prefix", "<gray>You are no longer vanished.</gray>");
    }

    public void handlePlayerJoin(Player player) {
        for (UUID vanishedUUID : vanishedPlayers) {
            Player vanishedPlayer = Bukkit.getPlayer(vanishedUUID);
            if (vanishedPlayer != null && vanishedPlayer.isOnline()) {
                if (!player.hasPermission("advancedcorehub.bypass.vanish")) {
                    player.hidePlayer(plugin, vanishedPlayer);
                }
            }
        }
    }

    public Set<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }
}
