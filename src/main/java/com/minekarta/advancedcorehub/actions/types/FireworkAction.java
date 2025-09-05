package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkAction implements Action {

    private final AdvancedCoreHub plugin;

    public FireworkAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        // Data: type;r;g;b;power;delay (type = BALL, BURST, etc. color 0-255)
        if (data == null || data.isEmpty()) return;

        String[] parts = data.split(";");
        if (parts.length < 6) {
            plugin.getLogger().warning("[FireworkAction] Invalid data. Expected: type;r;g;b;power;delay");
            return;
        }

        try {
            FireworkEffect.Type type = FireworkEffect.Type.valueOf(parts[0].toUpperCase());
            int r = Integer.parseInt(parts[1]);
            int g = Integer.parseInt(parts[2]);
            int b = Integer.parseInt(parts[3]);
            int power = Integer.parseInt(parts[4]);
            long delay = Long.parseLong(parts[5]);

            Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
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

        } catch (Exception e) {
            plugin.getLogger().warning("[FireworkAction] Failed to parse firework data: " + data + " | Error: " + e.getMessage());
        }
    }
}
