package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

public class FireworkAction implements Action {

    private final AdvancedCoreHub plugin;

    public FireworkAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String fireworkData;
        if (data instanceof List) {
            fireworkData = String.join(":", ((List<String>) data));
        } else if (data instanceof String) {
            fireworkData = (String) data;
        } else {
            plugin.getLogger().warning("[FireworkAction] Invalid data type. Expected a List or a String.");
            return;
        }

        // Expected format: [FIREWORK:type:r:g:b:power:delay]
        String[] parts = fireworkData.split(":");
        if (parts.length < 7) {
            plugin.getLogger().warning("[FireworkAction] Invalid data. Expected: [FIREWORK:type:r:g:b:power:delay], got: " + fireworkData);
            return;
        }

        try {
            FireworkEffect.Type type = FireworkEffect.Type.valueOf(parts[1].toUpperCase());
            int r = Integer.parseInt(parts[2]);
            int g = Integer.parseInt(parts[3]);
            int b = Integer.parseInt(parts[4]);
            int power = Integer.parseInt(parts[5]);
            long delay = Long.parseLong(parts[6]);

            Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.setPower(power);
            fwm.addEffect(FireworkEffect.builder()
                    .with(type)
                    .withColor(Color.fromRGB(r, g, b))
                    .build());

            fw.setFireworkMeta(fwm);

            if (delay > 0) {
                plugin.getServer().getScheduler().runTaskLater(plugin, fw::detonate, delay);
            } else {
                fw.detonate();
            }

        } catch (NumberFormatException e) {
            plugin.getLogger().warning("[FireworkAction] Invalid number format in firework data: " + fireworkData);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[FireworkAction] Invalid firework type: " + parts[1]);
        } catch (Exception e) {
            plugin.getLogger().warning("[FireworkAction] Failed to parse firework data: " + fireworkData + " | Error: " + e.getMessage());
        }
    }
}
