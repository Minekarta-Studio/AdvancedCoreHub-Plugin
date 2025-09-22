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
        if (!(data instanceof String)) return;
        String menuName = (String) data;
        if (menuName.isEmpty()) return;

        plugin.getMenuManager().openMenu(player, menuName.trim());
    }
}
