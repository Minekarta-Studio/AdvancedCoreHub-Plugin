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
    public void execute(Player player, Object data) {
        if (!(data instanceof String) || ((String) data).isEmpty()) {
            plugin.getLogger().warning("[BungeeAction] Server name cannot be empty.");
            return;
        }

        String serverName = (String) data;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName); // server name

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
