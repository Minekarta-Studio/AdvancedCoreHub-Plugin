package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.InventoryManager;
import com.minekarta.advancedcorehub.manager.ItemsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class ItemProtectionListener implements Listener {

    private final ItemsManager itemsManager;
    private final InventoryManager inventoryManager;

    public ItemProtectionListener(AdvancedCoreHub plugin) {
        this.itemsManager = plugin.getItemsManager();
        this.inventoryManager = plugin.getInventoryManager();
    }

    private boolean isProtectedInHub(Player player, ItemStack item) {
        if (item == null) return false;
        return inventoryManager.isHubWorld(player.getWorld().getName()) && itemsManager.isProtected(item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (isProtectedInHub(player, event.getCurrentItem()) || isProtectedInHub(player, event.getCursor())) {
            // Allow players with a bypass permission to manage their inventory
            if (player.hasPermission("advancedcorehub.bypass.protection")) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (isProtectedInHub(player, item)) {
            if (player.hasPermission("advancedcorehub.bypass.protection")) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (isProtectedInHub(player, event.getMainHandItem()) || isProtectedInHub(player, event.getOffHandItem())) {
            if (player.hasPermission("advancedcorehub.bypass.protection")) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (isProtectedInHub(player, item)) {
            if (player.hasPermission("advancedcorehub.bypass.protection")) return;
            event.setCancelled(true);
        }
    }
}
