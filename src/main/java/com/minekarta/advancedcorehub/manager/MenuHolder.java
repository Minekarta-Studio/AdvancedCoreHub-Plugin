package com.minekarta.advancedcorehub.manager;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuHolder implements InventoryHolder {

    private final String menuId;

    public MenuHolder(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuId() {
        return menuId;
    }

    @Override
    public @NotNull Inventory getInventory() {
        // This is not used in our implementation, but the method must be implemented.
        // The inventory is held by the Player, not this holder.
        return null;
    }
}
