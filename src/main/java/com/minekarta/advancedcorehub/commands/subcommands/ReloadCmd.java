package com.minekarta.advancedcorehub.commands.subcommands;

import com.minekarta.advancedcorehub.commands.SubCommand;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCmd extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin's configuration files.";
    }

    @Override
    public String getSyntax() {
        return "/ach reload";
    }

    @Override
    public String getPermission() {
        return Permissions.CMD_RELOAD;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        plugin.reloadPlugin();
        plugin.getLocaleManager().sendMessage(sender, "reload-success");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
