package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;
import java.util.Map;

public class MenuListener implements Listener {

    private final AdvancedCoreHub plugin;

    public MenuListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        // Check if the inventory is one of ours
        if (holder instanceof MenuHolder) {
            event.setCancelled(true);

            // Verify the click was in the top inventory
            if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            String menuId = ((MenuHolder) holder).getMenuId();

            Map<Integer, List<String>> actions = plugin.getMenuManager().getActionsForMenu(menuId);
            if (actions == null) return;

            List<String> slotActions = actions.get(event.getRawSlot());
            if (slotActions != null && !slotActions.isEmpty()) {
                // Close the inventory before executing actions that open another menu
                if (slotActions.stream().anyMatch(s -> s.toLowerCase().startsWith("[menu]"))) {
                    player.closeInventory();
                }
                plugin.getActionManager().executeActions(player, slotActions);
            }
        }
    }
}
