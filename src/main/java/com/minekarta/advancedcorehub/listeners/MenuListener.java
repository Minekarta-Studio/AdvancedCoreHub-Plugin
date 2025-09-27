package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.config.MenuItemConfig;
import com.minekarta.advancedcorehub.manager.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class MenuListener implements Listener {

    private final AdvancedCoreHub plugin;

    public MenuListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuHolder)) {
            return;
        }

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String menuId = ((MenuHolder) event.getInventory().getHolder()).getMenuId();
        int clickedSlot = event.getRawSlot();

        MenuItemConfig itemConfig = plugin.getMenuManager().getMenuItem(menuId, clickedSlot);

        if (itemConfig == null || itemConfig.clickActions.isEmpty()) {
            return;
        }

        List<String> actionsToExecute = null;
        if (event.getClick().isLeftClick() && itemConfig.clickActions.containsKey("LEFT")) {
            actionsToExecute = itemConfig.clickActions.get("LEFT");
        } else if (event.getClick().isRightClick() && itemConfig.clickActions.containsKey("RIGHT")) {
            actionsToExecute = itemConfig.clickActions.get("RIGHT");
        }

        if (actionsToExecute != null && !actionsToExecute.isEmpty()) {
            com.minekarta.advancedcorehub.config.MenuConfig menuConfig = plugin.getMenuManager().getMenuConfig(menuId);
            plugin.getMenuManager().playSound(player, "click", menuConfig);
            plugin.getActionManager().executeStringActions(player, actionsToExecute);
        }
    }
}