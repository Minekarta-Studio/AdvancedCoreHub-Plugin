package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;

public class MenuListener implements Listener {

    private final AdvancedCoreHub plugin;

    public MenuListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String inventoryTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        // A better way would be to check if event.getInventory().getHolder() is a custom holder.
        // But for this project, we check the title.
        Map<Integer, List<String>> actions = plugin.getMenuManager().getActionsForMenu(inventoryTitle);

        if (actions != null) {
            event.setCancelled(true);
            List<String> slotActions = actions.get(event.getRawSlot());
            if (slotActions != null && !slotActions.isEmpty()) {
                plugin.getActionManager().executeActions(player, slotActions);
            }
        }
    }
}
