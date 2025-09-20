package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.entity.Player;

@CommandAlias("selector")
@Description("Opens the server selector menu.")
public class ServerSelectorCommand extends BaseCommand {

    private final AdvancedCoreHub plugin;

    public ServerSelectorCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(Player player) {
        plugin.getMenuManager().openMenu(player, "selector");
    }
}
