package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.MenuHolder;
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
        if (!(event.getWhoClicked() instanceof Player)) return;

        InventoryHolder holder = event.getInventory().getHolder();

        // Check if the inventory is one of our custom menus
        if (holder instanceof MenuHolder) {
            event.setCancelled(true); // Prevent players from taking items out of the menu

            MenuHolder menuHolder = (MenuHolder) holder;
            String menuId = menuHolder.getMenuId();
            Map<Integer, List<String>> actions = plugin.getMenuManager().getActions(menuId);

            if (actions == null) return;

            List<String> slotActions = actions.get(event.getRawSlot());
            if (slotActions != null && !slotActions.isEmpty()) {
                plugin.getActionManager().executeActions((Player) event.getWhoClicked(), slotActions);
            }
        }
    }
}
