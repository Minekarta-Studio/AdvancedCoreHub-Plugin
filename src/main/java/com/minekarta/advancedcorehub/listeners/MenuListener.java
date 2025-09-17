package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.cosmetics.Gadget;
import com.minekarta.advancedcorehub.manager.MenuHolder;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
        if (!(holder instanceof MenuHolder)) {
            return;
        }

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir() || !clickedItem.hasItemMeta()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = clickedItem.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String menuId = ((MenuHolder) holder).getMenuId();

        // --- Gadget Handling ---
        if (pdc.has(PersistentKeys.GADGET_ID, PersistentDataType.STRING)) {
            String gadgetId = pdc.get(PersistentKeys.GADGET_ID, PersistentDataType.STRING);
            handleGadgetClick(player, gadgetId);
            return;
        }

        // --- Static Menu Item Handling ---
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }

        int clickedSlot = event.getRawSlot();
        Map<Integer, Map<String, List<String>>> menuActions = plugin.getMenuManager().getActionsForMenu(menuId);

        if (menuActions == null || !menuActions.containsKey(clickedSlot)) {
            return;
        }

        Map<String, List<String>> slotActions = menuActions.get(clickedSlot);
        List<String> actionsToExecute = null;

        if (event.getClick().isLeftClick()) {
            actionsToExecute = slotActions.get("LEFT");
        } else if (event.getClick().isRightClick()) {
            actionsToExecute = slotActions.get("RIGHT");
        }

        if (actionsToExecute != null && !actionsToExecute.isEmpty()) {
            plugin.getActionManager().executeStringActions(player, actionsToExecute);
        }
    }

    private void handleGadgetClick(Player player, String gadgetId) {
        Gadget gadget = plugin.getGadgetManager().getGadget(gadgetId);
        if (gadget == null) {
            plugin.getLogger().warning("Player " + player.getName() + " clicked on an unknown gadget: " + gadgetId);
            return;
        }

        // Check for cooldown
        if (plugin.getCooldownManager().hasCooldown(player, "gadget_" + gadgetId)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "gadget_" + gadgetId);
            plugin.getLocaleManager().sendMessage(player, "gadget-cooldown", String.valueOf(remaining));
            return;
        }

        // Set cooldown if applicable
        if (gadget.cooldown() > 0) {
            plugin.getCooldownManager().setCooldown(player, "gadget_" + gadgetId, gadget.cooldown());
        }

        // Execute actions
        if (gadget.actions() != null && !gadget.actions().isEmpty()) {
            plugin.getActionManager().executeStringActions(player, gadget.actions());
        }
    }
}
