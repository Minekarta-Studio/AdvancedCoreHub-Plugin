package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuAction implements Action {

    private final AdvancedCoreHub plugin;

    public MenuAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (!(data instanceof List)) return;
        List<String> args = (List<String>) data;
        if (args.size() < 2) return;
        plugin.getMenuManager().openMenu(player, args.get(1));
    }
}
