package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

public class ClearAction implements Action {

    private final AdvancedCoreHub plugin;

    public ClearAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        player.getInventory().clear();
    }
}
