package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("vanish|v")
public class VanishCommand extends BaseCommand {

    private final AdvancedCoreHub plugin;
    private final VanishManager vanishManager;

    public VanishCommand(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.vanishManager = plugin.getVanishManager();
    }

    @Default
    @CommandPermission("advancedcorehub.command.vanish")
    @Description("Toggle your vanish state.")
    public void onVanish(Player player) {
        vanishManager.toggleVanish(player);
    }

    @Subcommand("toggle")
    @CommandPermission("advancedcorehub.command.vanish.others")
    @CommandCompletion("@players")
    @Syntax("<player>")
    @Description("Toggle another player's vanish state.")
    public void onVanishOther(Player sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.getLocaleManager().sendMessage(sender, "player-not-found", "{player}", targetName);
            return;
        }

        vanishManager.toggleVanish(target);
        boolean isVanished = vanishManager.isVanished(target);

        plugin.getLocaleManager().sendMessage(sender, "prefix",
                isVanished ? "<gray>You have vanished " + target.getName() + "."
                           : "<gray>You have unvanished " + target.getName() + ".");
    }
}
