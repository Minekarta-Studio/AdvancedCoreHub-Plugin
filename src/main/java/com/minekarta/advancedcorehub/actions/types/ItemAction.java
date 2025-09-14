package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

public class ItemAction implements Action {

    private final AdvancedCoreHub plugin;

    public ItemAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || ((String) data).isEmpty()) return;

        String itemData = (String) data;
        // Data: item_name;amount;slot
        String[] parts = itemData.split(";");
        if (parts.length == 0) return;

        String itemName = parts[0];
        int amount = 1;
        int slot = -1; // Default to adding to inventory

        if (parts.length > 1) {
            try {
                amount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {}
        }
        if (parts.length > 2) {
            try {
                slot = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {}
        }

        plugin.getItemsManager().giveItem(player, itemName, amount, slot);
    }
}
