package com.minekarta.advancedcorehub.util;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * A record to hold a custom item's data, including the ItemStack and its associated actions.
 *
 * @param itemStack The configured ItemStack.
 * @param actions   The list of action strings to be executed on interaction.
 */
public record CustomItem(ItemStack itemStack, List<String> actions) {
}
