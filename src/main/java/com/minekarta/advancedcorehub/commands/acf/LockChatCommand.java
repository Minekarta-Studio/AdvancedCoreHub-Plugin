package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.ChatManager;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("lockchat")
@CommandPermission(Permissions.CMD_LOCKCHAT)
public class LockChatCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private ChatManager chatManager;

    @Dependency
    private LocaleManager localeManager;

    @Default
    public void onLockChat(CommandSender sender) {
        chatManager.toggleChatLock();
        boolean isLocked = chatManager.isChatLocked();
        String status = isLocked ? "locked" : "unlocked";

        Player player = (sender instanceof Player) ? (Player) sender : null;
        plugin.getServer().broadcast(localeManager.getComponent("chat-lock-broadcast", player, sender.getName(), status));
    }
}
