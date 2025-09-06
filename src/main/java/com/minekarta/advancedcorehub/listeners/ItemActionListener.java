package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.PersistentKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class ItemActionListener implements Listener {

    private final AdvancedCoreHub plugin;

    public ItemActionListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // We only care about right-click actions for this listener
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(PersistentKeys.ACTIONS_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true); // Prevent default item actions

            String actionString = container.get(PersistentKeys.ACTIONS_KEY, PersistentDataType.STRING);
            if (actionString != null && !actionString.isEmpty()) {
                List<String> actions = Arrays.asList(actionString.split("\n"));
                plugin.getActionManager().executeActions(player, actions);
            }
        }
    }
}
