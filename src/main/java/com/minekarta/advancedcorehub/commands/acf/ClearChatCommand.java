package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("clearchat|cc")
@CommandPermission(Permissions.CMD_CLEARCHAT)
public class ClearChatCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private LocaleManager localeManager;

    @Default
    public void onClearChat(CommandSender sender) {
        for (int i = 0; i < 100; i++) {
            plugin.getServer().broadcastMessage(" ");
        }

        Player player = (sender instanceof Player) ? (Player) sender : null;
        plugin.getServer().broadcast(localeManager.getComponent("chat-clear-broadcast", player, sender.getName()));
    }
}
