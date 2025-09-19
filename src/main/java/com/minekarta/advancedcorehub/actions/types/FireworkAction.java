package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FireworkAction implements Action {

    private final AdvancedCoreHub plugin;

    public FireworkAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String fireworkData;
        if (data instanceof String) {
            fireworkData = (String) data;
        } else {
            plugin.getLogger().warning("[FireworkAction] Invalid data type. Expected a String.");
            return;
        }

        try {
            Map<String, String> properties = new HashMap<>();
            for (String part : fireworkData.trim().split(" ")) {
                String[] pair = part.split(":", 2);
                if (pair.length == 2) {
                    properties.put(pair[0].toLowerCase(), pair[1]);
                }
            }

            FireworkEffect.Builder builder = FireworkEffect.builder();

            // Type
            FireworkEffect.Type type = FireworkEffect.Type.valueOf(properties.getOrDefault("type", "BALL").toUpperCase());
            builder.with(type);

            // Colors
            if (properties.containsKey("color")) {
                List<Color> colors = parseColors(properties.get("color"));
                if (!colors.isEmpty()) {
                    builder.withColor(colors);
                }
            }

            // Fade Colors
            if (properties.containsKey("fade")) {
                List<Color> fadeColors = parseColors(properties.get("fade"));
                if (!fadeColors.isEmpty()) {
                    builder.withFade(fadeColors);
                }
            }

            // Power and Delay
            int power = Integer.parseInt(properties.getOrDefault("power", "1"));
            long delay = Long.parseLong(properties.getOrDefault("delay", "0"));

            Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(power);
            fwm.addEffect(builder.build());
            fw.setFireworkMeta(fwm);

            if (delay > 0) {
                plugin.getServer().getScheduler().runTaskLater(plugin, fw::detonate, delay);
            } else {
                fw.detonate();
            }

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[FireworkAction] Invalid argument in firework data: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().warning("[FireworkAction] Failed to parse firework data: " + fireworkData + " | Error: " + e.getMessage());
        }
    }

    private List<Color> parseColors(String colorString) {
        List<Color> colors = new ArrayList<>();
        for (String colorName : colorString.split(",")) {
            try {
                java.lang.reflect.Field field = Color.class.getField(colorName.trim().toUpperCase());
                colors.add((Color) field.get(null));
            } catch (Exception e) {
                plugin.getLogger().warning("[FireworkAction] Invalid color name: " + colorName);
            }
        }
        return colors;
    }
}
