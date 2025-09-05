package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private final AdvancedCoreHub plugin;

    public PlayerJoinListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 1. Handle Join Message (can be customized or disabled)
        String joinMessage = plugin.getLocaleManager().get("join-message", player);
        if (joinMessage != null && !joinMessage.isEmpty()) {
            event.joinMessage(plugin.getLocaleManager().getComponent("join-message", player));
        }

        // 2. Execute actions_on_join from config.yml
        List<String> joinActions = plugin.getConfig().getStringList("actions_on_join");
        if (!joinActions.isEmpty()) {
            plugin.getActionManager().executeActions(player, joinActions);
        }

        // 3. Give join items from items.yml
        ConfigurationSection joinItemsSection = plugin.getFileManager().getConfig("items.yml").getConfigurationSection("join_items");
        if (joinItemsSection != null) {
            for (String key : joinItemsSection.getKeys(false)) {
                String itemName = joinItemsSection.getString(key + ".item_name");
                int amount = joinItemsSection.getInt(key + ".amount", 1);
                int slot = joinItemsSection.getInt(key + ".slot", -1);
                boolean force = joinItemsSection.getBoolean(key + ".force", false);

                if (itemName != null) {
                    // Prevent item duplication if not forced
                    if (!force && player.getInventory().contains(plugin.getItemsManager().getItem(itemName))) {
                        continue;
                    }
                    plugin.getItemsManager().giveItem(player, itemName, amount, slot);
                }
            }
        }

        // 4. Handle Boss Bar on join
        if (plugin.getConfig().getBoolean("bossbar.show_on_join", false)) {
            // Logic to be added when BossBar config is defined
        }
    }
}
