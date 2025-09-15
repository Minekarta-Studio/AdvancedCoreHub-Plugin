package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class SoundAction implements Action {

    private final AdvancedCoreHub plugin;

    public SoundAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        List<String> args = (List<String>) data;
        if (args.size() < 2) return;

        String soundName = args.get(1);

        float volume = 1.0f;
        if (args.size() > 2) {
            try {
                volume = Float.parseFloat(args.get(2));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("[SoundAction] Invalid volume format: " + args.get(2));
            }
        }

        float pitch = 1.0f;
        if (args.size() > 3) {
            try {
                pitch = Float.parseFloat(args.get(3));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("[SoundAction] Invalid pitch format: " + args.get(3));
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
