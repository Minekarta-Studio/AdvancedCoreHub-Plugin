package com.minekarta.advancedcorehub.actions.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

import java.util.List;

public class BungeeAction implements Action {

    private final AdvancedCoreHub plugin;

    public BungeeAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String serverName = "";
        if (data instanceof List) {
            List<String> args = (List<String>) data;
            if (args.size() > 1) {
                serverName = args.get(1);
            }
        } else if (data instanceof String) {
            serverName = (String) data;
        }

        if (serverName.isEmpty()) {
            plugin.getLogger().warning("[BungeeAction] Server name cannot be empty.");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName); // server name

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
