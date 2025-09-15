package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

import java.util.List;

public class ItemAction implements Action {

    private final AdvancedCoreHub plugin;

    public ItemAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        List<String> args = (List<String>) data;

        // Args: [ITEM, item_name, amount, slot]
        if (args.size() < 2) return;

        String itemName = args.get(1);
        int amount = 1;
        int slot = -1; // Default to adding to inventory

        if (args.size() > 2) {
            try {
                amount = Integer.parseInt(args.get(2));
            } catch (NumberFormatException ignored) {}
        }
        if (args.size() > 3) {
            try {
                slot = Integer.parseInt(args.get(3));
            } catch (NumberFormatException ignored) {}
        }

        plugin.getItemsManager().giveItem(player, itemName, amount, slot);
    }
}
