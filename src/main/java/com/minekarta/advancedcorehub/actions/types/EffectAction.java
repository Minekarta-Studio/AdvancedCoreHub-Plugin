package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class EffectAction implements Action {

    private final AdvancedCoreHub plugin;

    public EffectAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String effectData;
        if (data instanceof List) {
            effectData = String.join(":", ((List<String>) data));
        } else if (data instanceof String) {
            effectData = (String) data;
        } else {
            plugin.getLogger().warning("[EffectAction] Invalid data type. Expected a List or a String.");
            return;
        }

        // Expected format: [EFFECT] type;duration;strength, e.g., JUMP;200;4
        // The "EFFECT:" prefix is removed if present, for compatibility.
        if (effectData.toUpperCase().startsWith("EFFECT:")) {
            effectData = effectData.substring(7);
        }

        String[] parts = effectData.split(";");
        if (parts.length < 3) {
            plugin.getLogger().warning("[EffectAction] Invalid data. Expected: type;duration;strength, got: " + effectData);
            return;
        }

        try {
            PotionEffectType effectType = PotionEffectType.getByName(parts[0].toUpperCase());
            if (effectType == null) {
                plugin.getLogger().warning("[EffectAction] Invalid potion effect type: " + parts[0]);
                return;
            }
            int duration = Integer.parseInt(parts[1]); // Duration is in ticks
            int strength = Integer.parseInt(parts[2]) - 1; // Convert 1-based strength to 0-based amplifier

            player.addPotionEffect(new PotionEffect(effectType, duration, strength));

        } catch (NumberFormatException e) {
            plugin.getLogger().warning("[EffectAction] Invalid number format in effect data: " + effectData);
        } catch (Exception e) {
            plugin.getLogger().warning("[EffectAction] Failed to parse effect data: " + effectData + " | Error: " + e.getMessage());
        }
    }
}
