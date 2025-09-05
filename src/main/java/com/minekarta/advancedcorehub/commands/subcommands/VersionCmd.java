package com.minekarta.advancedcorehub.commands.subcommands;

import com.minekarta.advancedcorehub.commands.SubCommand;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class VersionCmd extends SubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Shows the current plugin version.";
    }

    @Override
    public String getSyntax() {
        return "/ach version";
    }

    @Override
    public String getPermission() {
        return Permissions.CMD_VERSION;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        plugin.getLocaleManager().sendMessage(sender, "version-info", plugin.getDescription().getVersion());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
