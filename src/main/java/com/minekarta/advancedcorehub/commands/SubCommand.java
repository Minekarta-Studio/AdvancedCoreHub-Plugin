package com.minekarta.advancedcorehub.commands;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    protected final AdvancedCoreHub plugin = AdvancedCoreHub.getInstance();

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract String getPermission();
    public abstract void perform(CommandSender sender, String[] args);
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

}
