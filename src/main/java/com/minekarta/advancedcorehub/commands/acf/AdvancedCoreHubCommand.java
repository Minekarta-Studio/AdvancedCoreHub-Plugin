package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.ChatManager;
import com.minekarta.advancedcorehub.manager.HubWorldManager;
import com.minekarta.advancedcorehub.manager.ItemsManager;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("advancedcorehub|ach|acore|ahub")
public class AdvancedCoreHubCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private LocaleManager localeManager;

    @Dependency
    private ItemsManager itemsManager;

    @Dependency
    private HubWorldManager hubWorldManager;

    @Dependency
    private ChatManager chatManager;

    @Default
    @HelpCommand
    @Subcommand("help")
    @Description("Shows the plugin's help menu.")
    public void onHelp(CommandSender sender) {
        // This will be handled by ACF's @HelpCommand annotation.
        // We can later customize this if needed by using `manager.generateCommandHelp()`.
    }

    @Subcommand("reload")
    @CommandPermission(Permissions.CMD_RELOAD)
    @Description("Reloads the plugin's configuration files.")
    public void onReload(CommandSender sender) {
        plugin.reloadPlugin();
        localeManager.sendMessage(sender, "reload-success");
    }

    @Subcommand("version")
    @CommandPermission(Permissions.CMD_VERSION)
    @Description("Shows the current plugin version.")
    public void onVersion(CommandSender sender) {
        localeManager.sendMessage(sender, "version-info", plugin.getDescription().getVersion());
    }

    @Subcommand("give")
    @CommandPermission(Permissions.CMD_GIVE)
    @Syntax("<player> <item> [amount] [slot]")
    @CommandCompletion("@players @customitems")
    @Description("Gives a player a custom item.")
    public void onGive(CommandSender sender, Player target, String itemName, @Optional @Default("1") Integer amount, @Optional @Default("-1") Integer slot) {
        itemsManager.giveItem(target, itemName, amount, slot);
        localeManager.sendMessage(sender, "item-given", String.valueOf(amount), itemName, target.getName());
    }

    @Subcommand("listitems")
    @CommandPermission(Permissions.CMD_LISTITEMS)
    @Description("Lists all available custom items.")
    public void onListItems(CommandSender sender) {
        java.util.Set<String> itemNames = itemsManager.getItemKeys();
        if (itemNames.isEmpty()) {
            localeManager.sendMessage(sender, "no-items-found");
            return;
        }

        String itemList = String.join(", ", itemNames);
        localeManager.sendMessage(sender, "item-list", itemList);
    }

    @Subcommand("clearchat|cc")
    @CommandPermission(Permissions.CMD_CLEARCHAT)
    @Description("Clears the server chat for all players.")
    public void onClearChat(CommandSender sender) {
        for (int i = 0; i < 100; i++) {
            plugin.getServer().broadcastMessage(" ");
        }
        Player player = (sender instanceof Player) ? (Player) sender : null;
        plugin.getServer().broadcast(localeManager.getComponent("chat-clear-broadcast", player, sender.getName()));
    }

    @Subcommand("lockchat|lc")
    @CommandPermission(Permissions.CMD_LOCKCHAT)
    @Description("Toggles the chat lock on or off.")
    public void onLockChat(CommandSender sender) {
        chatManager.toggleChatLock();
        boolean isLocked = chatManager.isChatLocked();
        String status = isLocked ? "locked" : "unlocked";

        Player player = (sender instanceof Player) ? (Player) sender : null;
        plugin.getServer().broadcast(localeManager.getComponent("chat-lock-broadcast", player, sender.getName(), status));
    }

    @Subcommand("worlds")
    @CommandPermission(Permissions.CMD_WORLDS)
    @Description("Manages the disabled worlds list.")
    public class WorldsGroup extends BaseCommand {

        @Subcommand("add")
        @Syntax("<world>")
        @CommandCompletion("@worlds")
        public void onAdd(CommandSender sender, String worldName) {
            if (hubWorldManager.addWorld(worldName)) {
                plugin.getConfig().set("hub-worlds", new java.util.ArrayList<>(hubWorldManager.getHubWorlds()));
                plugin.saveConfig();
                localeManager.sendMessage(sender, "world-added", worldName);
            } else {
                localeManager.sendMessage(sender, "world-add-failed", worldName);
            }
        }

        @Subcommand("remove")
        @Syntax("<world>")
        @CommandCompletion("@worlds")
        public void onRemove(CommandSender sender, String worldName) {
            if (hubWorldManager.removeWorld(worldName)) {
                plugin.getConfig().set("hub-worlds", new java.util.ArrayList<>(hubWorldManager.getHubWorlds()));
                plugin.saveConfig();
                localeManager.sendMessage(sender, "world-removed", worldName);
            } else {
                localeManager.sendMessage(sender, "world-remove-failed", worldName);
            }
        }

        @Subcommand("list")
        public void onList(CommandSender sender) {
            hubWorldManager.listWorlds(sender);
        }

        @Subcommand("check")
        @Syntax("<world>")
        @CommandCompletion("@worlds")
        public void onCheck(CommandSender sender, String worldName) {
            boolean isHubWorld = hubWorldManager.isHubWorld(worldName);
            localeManager.sendMessage(sender, "world-check-status", worldName, isHubWorld ? "a hub world" : "not a hub world");
        }
    }
}
