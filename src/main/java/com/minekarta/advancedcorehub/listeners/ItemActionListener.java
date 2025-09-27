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

        // Guard clauses: exit early if item is null, has no meta, or the action is not a click
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        if (!event.getAction().isLeftClick() && !event.getAction().isRightClick()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String actionString = null;

        if (event.getAction().isLeftClick()) {
            if (container.has(PersistentKeys.LEFT_CLICK_ACTIONS_KEY, PersistentDataType.STRING)) {
                actionString = container.get(PersistentKeys.LEFT_CLICK_ACTIONS_KEY, PersistentDataType.STRING);
            }
        } else if (event.getAction().isRightClick()) {
            // Check for the new key first
            if (container.has(PersistentKeys.RIGHT_CLICK_ACTIONS_KEY, PersistentDataType.STRING)) {
                actionString = container.get(PersistentKeys.RIGHT_CLICK_ACTIONS_KEY, PersistentDataType.STRING);
            }
            // Fallback for backward compatibility with the old 'actions' key
            else if (container.has(PersistentKeys.ACTIONS_KEY, PersistentDataType.STRING)) {
                actionString = container.get(PersistentKeys.ACTIONS_KEY, PersistentDataType.STRING);
            }
        }

        if (actionString != null && !actionString.isEmpty()) {
            event.setCancelled(true); // Prevent default item actions (e.g., eating, placing blocks)
            List<String> actions = Arrays.asList(actionString.split("\n"));
            plugin.getActionManager().executeStringActions(player, actions);

            // After executing actions, check for and play an interaction sound.
            if (container.has(PersistentKeys.INTERACT_SOUND_KEY, PersistentDataType.STRING)) {
                String soundData = container.get(PersistentKeys.INTERACT_SOUND_KEY, PersistentDataType.STRING);
                if (soundData != null) {
                    try {
                        String[] parts = soundData.split(";");
                        if (parts.length == 3) {
                            String soundName = parts[0];
                            float volume = Float.parseFloat(parts[1]);
                            float pitch = Float.parseFloat(parts[2]);
                            player.playSound(player.getLocation(), soundName, volume, pitch);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Could not play interact sound for item. Invalid sound data: " + soundData);
                    }
                }
            }
        }
    }
}
