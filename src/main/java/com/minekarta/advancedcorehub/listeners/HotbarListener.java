package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.ItemsManager;
import com.minekarta.advancedcorehub.util.CustomItem;
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

import java.util.List;

public class HotbarListener implements Listener {

    private final AdvancedCoreHub plugin;
    private final ItemsManager itemsManager;

    public HotbarListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.itemsManager = plugin.getItemsManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // We only care about right-click actions for now
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        // Check if the item is one of our custom items
        if (pdc.has(PersistentKeys.ITEM_ID, PersistentDataType.STRING)) {
            String itemId = pdc.get(PersistentKeys.ITEM_ID, PersistentDataType.STRING);
            CustomItem customItem = itemsManager.getCustomItem(itemId);

            if (customItem != null) {
                // Prevent default item actions (like eating, placing blocks)
                event.setCancelled(true);

                List<String> actions = customItem.actions();
                if (actions != null && !actions.isEmpty()) {
                    plugin.getActionManager().executeActions(player, actions);
                }
            }
        }
    }
}
