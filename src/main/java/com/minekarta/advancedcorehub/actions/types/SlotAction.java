package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

public class SlotAction implements Action {

    private final AdvancedCoreHub plugin;

    public SlotAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || ((String) data).isEmpty()) return;

        String slotData = (String) data;
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
