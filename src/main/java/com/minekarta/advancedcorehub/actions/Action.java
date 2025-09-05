package com.minekarta.advancedcorehub.actions;

import org.bukkit.entity.Player;

/**
 * Represents a generic action that can be executed.
 */
@FunctionalInterface
public interface Action {

    /**
     * Executes the action for a specific player.
     *
     * @param player The player context for the action.
     * @param data   The data or arguments for the action, following the identifier.
     */
    void execute(Player player, String data);

}
