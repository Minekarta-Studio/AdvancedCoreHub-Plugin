package com.minekarta.advancedcorehub.commands.subcommands;

import com.minekarta.advancedcorehub.commands.SubCommand;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorldsCmd extends SubCommand {
    @Override
    public String getName() {
        return "worlds";
    }

    @Override
    public String getDescription() {
        return "Manages the disabled worlds list.";
    }

    @Override
    public String getSyntax() {
        return "/ach worlds <add|remove|list|check> [world]";
    }

    @Override
    public String getPermission() {
        return Permissions.CMD_WORLDS;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            plugin.getLocaleManager().sendMessage(sender, "invalid-usage", getSyntax());
            return;
        }

        String subAction = args[0].toLowerCase();

        switch (subAction) {
            case "add":
                if (args.length < 2) {
                    plugin.getLocaleManager().sendMessage(sender, "invalid-usage", "/ach worlds add <world>");
                    return;
                }
                plugin.getDisabledWorlds().addWorld(args[1]);
                plugin.getLocaleManager().sendMessage(sender, "world-added", args[1]);
                break;
            case "remove":
                if (args.length < 2) {
                    plugin.getLocaleManager().sendMessage(sender, "invalid-usage", "/ach worlds remove <world>");
                    return;
                }
                plugin.getDisabledWorlds().removeWorld(args[1]);
                plugin.getLocaleManager().sendMessage(sender, "world-removed", args[1]);
                break;
            case "list":
                plugin.getDisabledWorlds().listWorlds(sender);
                break;
            case "check":
                if (args.length < 2) {
                    plugin.getLocaleManager().sendMessage(sender, "invalid-usage", "/ach worlds check <world>");
                    return;
                }
                boolean isDisabled = plugin.getDisabledWorlds().isDisabled(args[1]);
                plugin.getLocaleManager().sendMessage(sender, "world-check-status", args[1], isDisabled ? "disabled" : "enabled");
                break;
            default:
                plugin.getLocaleManager().sendMessage(sender, "invalid-usage", getSyntax());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "check").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("check"))) {
            // Tab complete world names
            return null; // Let bukkit handle it
        }
        return Collections.emptyList();
    }
}
