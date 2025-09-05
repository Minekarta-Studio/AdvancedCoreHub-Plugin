package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeAction implements Action {

    private final AdvancedCoreHub plugin;

    public GamemodeAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        try {
            GameMode gm = GameMode.valueOf(data.toUpperCase());
            player.setGameMode(gm);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[GamemodeAction] Invalid gamemode: " + data);
        }
    }
}
