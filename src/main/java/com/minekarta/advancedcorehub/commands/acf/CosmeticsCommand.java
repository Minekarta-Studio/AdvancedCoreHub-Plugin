package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.cosmetics.CosmeticsManager;
import com.minekarta.advancedcorehub.cosmetics.ParticleTrail;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import org.bukkit.entity.Player;

@CommandAlias("cosmetics|cosmetic")
public class CosmeticsCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private LocaleManager localeManager;

    @Default
    @Description("Opens the main cosmetics menu.")
    public void onDefault(Player player) {
        plugin.getMenuManager().openMenu(player, "cosmetics");
    }

    @Subcommand("trail")
    @Syntax("<trail_id>")
    @CommandCompletion("@particletrails")
    @Description("Sets your active particle trail.")
    public void onTrail(Player player, String trailId) {
        CosmeticsManager cosmeticsManager = plugin.getCosmeticsManager();
        ParticleTrail trail = cosmeticsManager.getTrail(trailId);

        if (trail == null) {
            localeManager.sendMessage(player, "cosmetic-not-found", trailId);
            return;
        }

        if (!player.hasPermission(trail.permission())) {
            localeManager.sendMessage(player, "no-permission");
            return;
        }

        cosmeticsManager.setActiveTrail(player, trail.particle() == null ? null : trail);
        localeManager.sendMessage(player, "trail-set", trail.name());
    }
}
