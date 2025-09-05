package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private final AdvancedCoreHub plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public CooldownManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    public void setCooldown(Player player, String key, long seconds) {
        if (seconds <= 0) {
            removeCooldown(player, key);
            return;
        }
        long expiryTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(key, expiryTime);
    }

    public boolean hasCooldown(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null || !playerCooldowns.containsKey(key)) {
            return false;
        }
        return System.currentTimeMillis() < playerCooldowns.get(key);
    }

    public long getRemainingCooldown(Player player, String key) {
        if (!hasCooldown(player, key)) {
            return 0;
        }
        long expiryTime = cooldowns.get(player.getUniqueId()).get(key);
        return TimeUnit.MILLISECONDS.toSeconds(expiryTime - System.currentTimeMillis());
    }

    public void removeCooldown(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns != null) {
            playerCooldowns.remove(key);
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(player.getUniqueId());
            }
        }
    }
}
