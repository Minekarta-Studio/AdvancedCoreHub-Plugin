package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
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
        @SuppressWarnings("unchecked")
        List<String> args = (List<String>) data;
        if (args.size() < 2) return;

        String soundName = args.get(1).trim();

        float volume = 1.0f;
        if (args.size() > 2) {
            try {
                volume = Float.parseFloat(args.get(2).trim());
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("[SoundAction] Invalid volume format: " + args.get(2));
            }
        }

        float pitch = 1.0f;
        if (args.size() > 3) {
            try {
                pitch = Float.parseFloat(args.get(3).trim());
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("[SoundAction] Invalid pitch format: " + args.get(3));
            }
        }

        // The modern Paper API allows playing sounds by their string key directly.
        // This is safer than Sound.valueOf() as it won't throw an error for invalid sounds,
        // it will just fail silently. We can add a warning if needed, but for now, this is fine.
        player.playSound(player.getLocation(), soundName.toUpperCase(), volume, pitch);
    }
}
