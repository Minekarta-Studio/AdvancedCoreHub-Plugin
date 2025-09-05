package com.minekarta.advancedcorehub.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuHolder implements InventoryHolder {

    private final String menuId;
    private Inventory inventory;

    public MenuHolder(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuId() {
        return menuId;
    }

    @Override
    public @NotNull Inventory getInventory() {
        // This is managed by Bukkit, but we can hold a reference if needed.
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
