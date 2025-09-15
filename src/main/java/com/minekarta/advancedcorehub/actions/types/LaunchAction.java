package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class LaunchAction implements Action {

    private final AdvancedCoreHub plugin;

    public LaunchAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        List<String> args = (List<String>) data;

        if (args.size() < 3) {
            plugin.getLogger().warning("[LaunchAction] Invalid data. Expected: [LAUNCH:power:powerY]");
            return;
        }

        try {
            double power = Double.parseDouble(args.get(1));
            double powerY = Double.parseDouble(args.get(2));

            Vector direction = player.getLocation().getDirection().multiply(power).setY(powerY);
            player.setVelocity(direction);

        } catch (NumberFormatException e) {
            plugin.getLogger().warning("[LaunchAction] Invalid number format in data: " + args);
        }
    }
}
