package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LaunchAction implements Action {

    private final AdvancedCoreHub plugin;

    public LaunchAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        String[] parts = data.split(";");
        if (parts.length < 2) {
            plugin.getLogger().warning("[LaunchAction] Invalid data. Expected: power;powerY");
            return;
        }

        try {
            double power = Double.parseDouble(parts[0]);
            double powerY = Double.parseDouble(parts[1]);

            Vector direction = player.getLocation().getDirection().multiply(power).setY(powerY);
            player.setVelocity(direction);

        } catch (NumberFormatException e) {
            plugin.getLogger().warning("[LaunchAction] Invalid number format in data: " + data);
        }
    }
}
