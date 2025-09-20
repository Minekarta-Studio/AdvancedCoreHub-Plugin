package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerVisibilityManager {

    private final AdvancedCoreHub plugin;
    private final Set<UUID> vanished = new HashSet<>();
    private final int itemSlot;
    private final boolean defaultState;
    private final boolean enabled;

    public PlayerVisibilityManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("player-visibility");
        this.enabled = config.getBoolean("enabled", true);
        this.defaultState = config.getBoolean("default_state", true);
        this.itemSlot = config.getInt("item_slot", 8);
    }

    public void toggleVisibility(Player player) {
        setVanished(player, !isVanished(player));
    }

    public void setVanished(Player player, boolean isVanished) {
        if (isVanished) {
            vanished.add(player.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.hasPermission("advancedcorehub.bypass.visibility")) {
                    player.hidePlayer(plugin, other);
                }
            }
            plugin.getLocaleManager().sendMessage(player, "player-hider-hidden");
        } else {
            vanished.remove(player.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, other);
            }
            plugin.getLocaleManager().sendMessage(player, "player-hider-shown");
        }
        updateVisibilityItem(player);
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public void handlePlayerJoin(Player player) {
        if (enabled && plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
            giveVisibilityItem(player);
            if (defaultState) {
                setVanished(player, false);
            } else {
                setVanished(player, true);
            }

            for (UUID vanishedUUID : vanished) {
                Player vanishedPlayer = Bukkit.getPlayer(vanishedUUID);
                if (vanishedPlayer != null) {
                    player.hidePlayer(plugin, vanishedPlayer);
                }
            }

            for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                if(isVanished(onlinePlayer)){
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }
        }
    }

    public void handlePlayerQuit(Player player) {
        vanished.remove(player.getUniqueId());
    }

    public void giveVisibilityItem(Player player) {
        if(enabled){
            player.getInventory().setItem(itemSlot, isVanished(player) ? getHiddenItem(player) : getVisibleItem(player));
        }
    }

    public void updateVisibilityItem(Player player) {
        if(enabled){
            player.getInventory().setItem(itemSlot, isVanished(player) ? getHiddenItem(player) : getVisibleItem(player));
        }
    }

    public ItemStack getVisibleItem(Player player) {
        ConfigurationSection visibleSection = plugin.getConfig().getConfigurationSection("player-visibility.item_visible");
        return new ItemBuilder(Material.valueOf(visibleSection.getString("material", "LIME_DYE")))
                .setDisplayName(plugin.getLocaleManager().getComponentFromString(visibleSection.getString("name"), player))
                .setLore(plugin.getLocaleManager().getComponentList(visibleSection.getStringList("lore"), player))
                .build();
    }

    public ItemStack getHiddenItem(Player player) {
        ConfigurationSection hiddenSection = plugin.getConfig().getConfigurationSection("player-visibility.item_hidden");
        return new ItemBuilder(Material.valueOf(hiddenSection.getString("material", "GRAY_DYE")))
                .setDisplayName(plugin.getLocaleManager().getComponentFromString(hiddenSection.getString("name"), player))
                .setLore(plugin.getLocaleManager().getComponentList(hiddenSection.getStringList("lore"), player))
                .build();
    }

    public boolean isEnabled() {
        return enabled;
    }
}
