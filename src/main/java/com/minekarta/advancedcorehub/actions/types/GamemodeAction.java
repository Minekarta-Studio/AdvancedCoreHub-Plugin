package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

public class GamemodeAction implements Action {

    private final AdvancedCoreHub plugin;

    public GamemodeAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;

        @SuppressWarnings("unchecked")
        List<String> args = (List<String>) data;
        if (args.size() < 2) return;

        String gamemode = args.get(1).trim();
        try {
            GameMode gm = GameMode.valueOf(gamemode.toUpperCase());
            player.setGameMode(gm);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[GamemodeAction] Invalid gamemode: " + gamemode);
        }
    }
}
