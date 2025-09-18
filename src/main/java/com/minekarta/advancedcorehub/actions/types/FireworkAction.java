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
        if (!(data instanceof List)) {
            plugin.getLogger().warning("[FireworkAction] Invalid data type for FireworkAction. Expected a List of strings.");
            return;
        }
        List<String> args = (List<String>) data;

        // Args: [FIREWORK, type, r, g, b, power, delay]
        if (args.size() < 7) {
            plugin.getLogger().warning("[FireworkAction] Invalid data. Expected: [FIREWORK:type:r:g:b:power:delay]");
            return;
        }

        try {
            FireworkEffect.Type type = FireworkEffect.Type.valueOf(args.get(1).toUpperCase());
            int r = Integer.parseInt(args.get(2));
            int g = Integer.parseInt(args.get(3));
            int b = Integer.parseInt(args.get(4));
            int power = Integer.parseInt(args.get(5));
            long delay = Long.parseLong(args.get(6));

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

        } catch (Exception e) {
            plugin.getLogger().warning("[FireworkAction] Failed to parse firework data: " + args + " | Error: " + e.getMessage());
        }
    }
}
