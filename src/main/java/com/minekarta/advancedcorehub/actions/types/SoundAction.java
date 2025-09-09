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

        String[] parts = data.split(";");
        String soundName = parts[0];

        float volume = 1.0f;
        if (parts.length > 1) {
            try {
                volume = Float.parseFloat(parts[1]);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("[SoundAction] Invalid volume format: " + parts[1]);
            }
        }

        float pitch = 1.0f;
        if (parts.length > 2) {
            try {
                pitch = Float.parseFloat(parts[2]);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("[SoundAction] Invalid pitch format: " + parts[2]);
            }
        }

        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[SoundAction] Invalid sound name: " + soundName);
        }
    }
}
