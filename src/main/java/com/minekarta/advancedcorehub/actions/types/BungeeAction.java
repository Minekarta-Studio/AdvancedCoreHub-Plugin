package com.minekarta.advancedcorehub.actions.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

public class BungeeAction implements Action {

    private final AdvancedCoreHub plugin;

    public BungeeAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String data) {
        if (data == null || data.isEmpty()) {
            plugin.getLogger().warning("[BungeeAction] Server name cannot be empty.");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(data); // server name

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
