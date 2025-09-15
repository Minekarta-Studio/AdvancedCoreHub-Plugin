package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

import java.util.List;

public class SlotAction implements Action {

    private final AdvancedCoreHub plugin;

    public SlotAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        List<String> args = (List<String>) data;
        if (args.size() < 2) return;

        String slotData = args.get(1);
        try {
            int slot = Integer.parseInt(slotData);
            if (slot < 0 || slot > 8) {
                plugin.getLogger().warning("[SlotAction] Invalid slot number: " + slot + ". Must be between 0 and 8.");
                return;
            }
            player.getInventory().setHeldItemSlot(slot);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("[SlotAction] Invalid number format for slot: " + slotData);
        }
    }
}
