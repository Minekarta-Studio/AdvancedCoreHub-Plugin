package com.minekarta.advancedcorehub.commands.acf;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.manager.BossBarManager;
import com.minekarta.advancedcorehub.manager.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

@CommandAlias("bossbar|bb")
@CommandPermission("advancedcorehub.command.bossbar")
@Description("Manage server-wide boss bars.")
public class BossBarCommand extends BaseCommand {

    @Dependency
    private AdvancedCoreHub plugin;

    @Dependency
    private BossBarManager bossBarManager;

    @Dependency
    private LocaleManager localeManager;


    @Default
    @HelpCommand
    @Subcommand("help")
    public void onHelp(CommandSender sender) {
        localeManager.sendMessage(sender, "bossbar-help-header");
        localeManager.sendMessage(sender, "bossbar-help-create");
        localeManager.sendMessage(sender, "bossbar-help-remove");
        localeManager.sendMessage(sender, "bossbar-help-set");
    }

    @Subcommand("create")
    @Syntax("<player|@a> <color> <style> <title...>")
    @CommandCompletion("@players|@a @barcolors @barstyles")
    @Description("Create a boss bar for a player or everyone.")
    public void onCreate(CommandSender sender, String targetName, BarColor color, BarStyle style, String title) {
        if (targetName.equalsIgnoreCase("@a")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            for (Player player : onlinePlayers) {
                bossBarManager.createBossBar(player, title, color, style);
            }
            localeManager.sendMessage(sender, "bossbar-created", "all players");
        } else {
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                localeManager.sendMessage(sender, "player-not-found", targetName);
                return;
            }
            bossBarManager.createBossBar(target, title, color, style);
            localeManager.sendMessage(sender, "bossbar-created", target.getName());
        }
    }

    @Subcommand("remove")
    @Syntax("<player|@a>")
    @CommandCompletion("@players|@a")
    @Description("Remove a boss bar from a player or everyone.")
    public void onRemove(CommandSender sender, String targetName) {
        if (targetName.equalsIgnoreCase("@a")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            for (Player player : onlinePlayers) {
                bossBarManager.removeBossBar(player);
            }
            localeManager.sendMessage(sender, "bossbar-removed", "all players");
        } else {
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                localeManager.sendMessage(sender, "player-not-found", targetName);
                return;
            }
            bossBarManager.removeBossBar(target);
            localeManager.sendMessage(sender, "bossbar-removed", target.getName());
        }
    }

    @Subcommand("set")
    @Description("Modify properties of a player's boss bar.")
    public class SetCommands extends BaseCommand {

        @Subcommand("title")
        @Syntax("<player> <title...>")
        @CommandCompletion("@players")
        public void onSetTitle(CommandSender sender, Player target, String title) {
            bossBarManager.updateTitle(target, title);
            localeManager.sendMessage(sender, "bossbar-set-success", "title", title, target.getName());
        }

        @Subcommand("color")
        @Syntax("<player> <color>")
        @CommandCompletion("@players @barcolors")
        public void onSetColor(CommandSender sender, Player target, BarColor color) {
            bossBarManager.updateColor(target, color);
            localeManager.sendMessage(sender, "bossbar-set-success", "color", color.name(), target.getName());
        }

        @Subcommand("style")
        @Syntax("<player> <style>")
        @CommandCompletion("@players @barstyles")
        public void onSetStyle(CommandSender sender, Player target, BarStyle style) {
            bossBarManager.updateStyle(target, style);
            localeManager.sendMessage(sender, "bossbar-set-success", "style", style.name(), target.getName());
        }
    }
}
