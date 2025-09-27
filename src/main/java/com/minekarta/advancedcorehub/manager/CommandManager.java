package com.minekarta.advancedcorehub.manager;

import co.aikar.commands.PaperCommandManager;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import co.aikar.commands.PaperCommandManager;
import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.commands.acf.AdvancedCoreHubCommand;
import com.minekarta.advancedcorehub.commands.acf.BossBarCommand;
import com.minekarta.advancedcorehub.commands.acf.CosmeticsCommand;
import com.minekarta.advancedcorehub.commands.acf.FlyCommand;
import com.minekarta.advancedcorehub.commands.acf.ServerSelectorCommand;
import com.minekarta.advancedcorehub.commands.acf.SetSpawnCommand;
import com.minekarta.advancedcorehub.commands.acf.SpawnCommand;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandManager {

    private final PaperCommandManager manager;
    private final AdvancedCoreHub plugin;

    public CommandManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.manager = new PaperCommandManager(plugin);

        registerDependencies();
        registerCommandCompletions();
        registerCommands();
    }

    public PaperCommandManager getManager() {
        return manager;
    }

    public void registerCommands() {
        manager.registerCommand(new BossBarCommand());
        manager.registerCommand(new FlyCommand());
        manager.registerCommand(new SpawnCommand());
        manager.registerCommand(new SetSpawnCommand());
        manager.registerCommand(new AdvancedCoreHubCommand());
        manager.registerCommand(new CosmeticsCommand());
        manager.registerCommand(new ServerSelectorCommand(plugin));
    }

    private void registerDependencies() {
        manager.registerDependency(BossBarManager.class, plugin.getBossBarManager());
        manager.registerDependency(LocaleManager.class, plugin.getLocaleManager());
        manager.registerDependency(ChatManager.class, plugin.getChatManager());
        manager.registerDependency(ItemsManager.class, plugin.getItemsManager());
        manager.registerDependency(HubWorldManager.class, plugin.getHubWorldManager());
        manager.registerDependency(com.minekarta.advancedcorehub.cosmetics.CosmeticsManager.class, plugin.getCosmeticsManager());
    }

    private void registerCommandCompletions() {
        manager.getCommandCompletions().registerCompletion("barcolors", c ->
                Arrays.stream(BarColor.values()).map(e -> e.name().toLowerCase()).collect(Collectors.toList()));
        manager.getCommandCompletions().registerCompletion("barstyles", c ->
                Arrays.stream(BarStyle.values()).map(e -> e.name().toLowerCase()).collect(Collectors.toList()));
        manager.getCommandCompletions().registerCompletion("durations", c ->
                Arrays.asList("10s", "30s", "1m", "5m", "10m", "30m", "1h"));
        manager.getCommandCompletions().registerCompletion("customitems", c ->
                plugin.getItemsManager().getItemKeys());
        manager.getCommandCompletions().registerCompletion("worlds", c ->
                plugin.getServer().getWorlds().stream().map(org.bukkit.World::getName).collect(Collectors.toList()));
        manager.getCommandCompletions().registerCompletion("particletrails", c ->
                plugin.getCosmeticsManager().getAvailableTrails().keySet());
    }
}
