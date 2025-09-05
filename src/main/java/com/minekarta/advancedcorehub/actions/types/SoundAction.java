package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundAction implements Action {

    private final AdvancedCoreHub plugin;

    public SoundAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        try {
            Sound sound = Sound.valueOf(data.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[SoundAction] Invalid sound name: " + data);
        }
    }
}
