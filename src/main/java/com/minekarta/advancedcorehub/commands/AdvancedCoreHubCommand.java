package com.minekarta.advancedcorehub.commands;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AdvancedCoreHubCommand implements CommandExecutor, TabCompleter {

    private final AdvancedCoreHub plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public AdvancedCoreHubCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        registerSubCommand(new HelpCmd(this)); // Pass the main command to help cmd
        registerSubCommand(new ReloadCmd());
        registerSubCommand(new VersionCmd());
        registerSubCommand(new GiveCmd());
        registerSubCommand(new WorldsCmd());
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            // Show help or version by default
            subCommands.get("help").perform(sender, args);
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            plugin.getLocaleManager().sendMessage(sender, "unknown-command", args[0]);
            return true;
        }

        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            plugin.getLocaleManager().sendMessage(sender, "no-permission");
            return true;
        }

        String[] subCommandArgs = Arrays.copyOfRange(args, 1, args.length);
        subCommand.perform(sender, subCommandArgs);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .filter(name -> {
                        SubCommand sub = subCommands.get(name);
                        return sub.getPermission() == null || sender.hasPermission(sub.getPermission());
                    })
                    .collect(Collectors.toList());
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                return Collections.emptyList();
            }
            String[] subCommandArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommand.onTabComplete(sender, subCommandArgs);
        }

        return Collections.emptyList();
    }
}
