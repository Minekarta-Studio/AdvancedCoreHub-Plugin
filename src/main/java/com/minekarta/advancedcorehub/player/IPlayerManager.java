package com.minekarta.advancedcorehub.player;

import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for the PlayerManager, defining the contract for handling player data.
 */
public interface IPlayerManager extends Listener {

    /**
     * Gets the {@link PlayerData} for a specific player from the cache.
     *
     * @param uuid The UUID of the player.
     * @return An {@link Optional} containing the PlayerData if the player is online
     *         and their data is loaded, otherwise an empty Optional.
     */
    Optional<PlayerData> getPlayerData(UUID uuid);

}