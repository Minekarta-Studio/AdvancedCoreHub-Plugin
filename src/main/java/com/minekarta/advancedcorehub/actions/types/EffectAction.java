package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectAction implements Action {

    private final AdvancedCoreHub plugin;

    public EffectAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || ((String) data).isEmpty()) return;

        String effectData = (String) data;
        // Data: effect_type;duration_seconds;strength
        String[] parts = effectData.split(";");
        if (parts.length < 3) {
            plugin.getLogger().warning("[EffectAction] Invalid data. Expected: effect_type;duration;strength");
            return;
        }

        try {
            PotionEffectType effectType = PotionEffectType.getByName(parts[0].toUpperCase());
            if (effectType == null) {
                plugin.getLogger().warning("[EffectAction] Invalid potion effect type: " + parts[0]);
                return;
            }
            int duration = Integer.parseInt(parts[1]) * 20; // Convert seconds to ticks
            int strength = Integer.parseInt(parts[2]) - 1; // Convert 1-based strength to 0-based amplifier

            player.addPotionEffect(new PotionEffect(effectType, duration, strength));

        } catch (Exception e) {
            plugin.getLogger().warning("[EffectAction] Failed to parse effect data: " + effectData + " | Error: " + e.getMessage());
        }
    }
}
