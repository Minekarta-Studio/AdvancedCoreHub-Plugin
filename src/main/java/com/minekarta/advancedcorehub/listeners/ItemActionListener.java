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
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Action action = event.getAction();

        boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        boolean isRightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;

        if (!isLeftClick && !isRightClick) {
            return;
        }

        String actionString = null;
        if (isLeftClick && container.has(PersistentKeys.LEFT_CLICK_ACTIONS_KEY, PersistentDataType.STRING)) {
            actionString = container.get(PersistentKeys.LEFT_CLICK_ACTIONS_KEY, PersistentDataType.STRING);
        } else if (isRightClick) {
            if (container.has(PersistentKeys.RIGHT_CLICK_ACTIONS_KEY, PersistentDataType.STRING)) {
                actionString = container.get(PersistentKeys.RIGHT_CLICK_ACTIONS_KEY, PersistentDataType.STRING);
            } else if (container.has(PersistentKeys.ACTIONS_KEY, PersistentDataType.STRING)) {
                // Backward compatibility
                actionString = container.get(PersistentKeys.ACTIONS_KEY, PersistentDataType.STRING);
            }
        }

        if (actionString != null && !actionString.isEmpty()) {
            event.setCancelled(true); // Prevent default item actions
            List<String> actions = Arrays.asList(actionString.split("\n"));
            plugin.getActionManager().executeActions(player, actions);
        }
    }
}
