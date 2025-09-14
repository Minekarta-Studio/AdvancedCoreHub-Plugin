package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

public class MessageAction implements Action {

    private final AdvancedCoreHub plugin;

    public MessageAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        if (data instanceof String) {
            plugin.getLocaleManager().sendMessage(player, (String) data);
        }
    }
}
