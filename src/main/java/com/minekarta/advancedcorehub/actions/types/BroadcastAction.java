package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class BroadcastAction implements Action {

    private final AdvancedCoreHub plugin;

    public BroadcastAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) return;

        // The 'data' can be a key from the language file OR a raw message.
        // getComponent handles both cases gracefully (though raw messages are not best practice).
        Component message = plugin.getLocaleManager().getComponent(data, player);
        plugin.getServer().broadcast(message);
    }
}
