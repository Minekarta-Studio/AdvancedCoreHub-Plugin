package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

public class CloseAction implements Action {

    private final AdvancedCoreHub plugin;

    public CloseAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        player.closeInventory();
    }
}
