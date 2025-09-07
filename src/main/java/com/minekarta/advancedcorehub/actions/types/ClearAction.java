package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ClearAction implements Action {

    private final AdvancedCoreHub plugin;

    public ClearAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        player.getInventory().clear();
        player.getPersistentDataContainer().set(PersistentKeys.INVENTORY_CLEARED, PersistentDataType.BYTE, (byte) 1);
    }
}
