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
        if (!(data instanceof List)) {
            plugin.getLogger().warning("[EffectAction] Invalid data type for EffectAction. Expected a List of strings.");
            return;
        }

        List<String> args = (List<String>) data;
        // Args from string: [EFFECT, type, duration, strength]
        if (args.size() < 4) {
            plugin.getLogger().warning("[EffectAction] Invalid data. Expected: [EFFECT:type:duration:strength]");
            return;
        }

        try {
            PotionEffectType effectType = PotionEffectType.getByName(args.get(1).toUpperCase());
            if (effectType == null) {
                plugin.getLogger().warning("[EffectAction] Invalid potion effect type: " + args.get(1));
                return;
            }
            int duration = Integer.parseInt(args.get(2)) * 20; // Convert seconds to ticks
            int strength = Integer.parseInt(args.get(3)) - 1; // Convert 1-based strength to 0-based amplifier

            player.addPotionEffect(new PotionEffect(effectType, duration, strength));

        } catch (Exception e) {
            plugin.getLogger().warning("[EffectAction] Failed to parse effect data: " + args + " | Error: " + e.getMessage());
        }
    }
}
