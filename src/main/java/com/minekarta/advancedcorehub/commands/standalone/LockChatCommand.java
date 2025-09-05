package com.minekarta.advancedcorehub.commands.standalone;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LockChatCommand implements CommandExecutor {

    private final AdvancedCoreHub plugin;

    public LockChatCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(Permissions.CMD_LOCKCHAT)) {
            plugin.getLocaleManager().sendMessage(sender, "no-permission");
            return true;
        }

        plugin.getChatManager().toggleChatLock();
        boolean isLocked = plugin.getChatManager().isChatLocked();
        String status = isLocked ? "locked" : "unlocked";

        plugin.getServer().broadcast(plugin.getLocaleManager().getComponent("chat-lock-broadcast", sender.getName(), status));

        return true;
    }
}
