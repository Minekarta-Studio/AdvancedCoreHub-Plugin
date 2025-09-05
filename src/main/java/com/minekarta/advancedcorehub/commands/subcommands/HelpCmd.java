package com.minekarta.advancedcorehub.commands.subcommands;

import com.minekarta.advancedcorehub.commands.AdvancedCoreHubCommand;
import com.minekarta.advancedcorehub.commands.SubCommand;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCmd extends SubCommand {

    private final AdvancedCoreHubCommand mainCommand;

    public HelpCmd(AdvancedCoreHubCommand mainCommand) {
        this.mainCommand = mainCommand;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows the plugin's help menu.";
    }

    @Override
    public String getSyntax() {
        return "/ach help";
    }

    @Override
    public String getPermission() {
        return Permissions.CMD_HELP;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "--- AdvancedCoreHub Help ---");
        mainCommand.getSubCommands().values().stream()
                .filter(sub -> sub.getPermission() == null || sender.hasPermission(sub.getPermission()))
                .forEach(sub -> {
                    sender.sendMessage(ChatColor.GOLD + sub.getSyntax() + ChatColor.WHITE + " - " + ChatColor.GRAY + sub.getDescription());
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
