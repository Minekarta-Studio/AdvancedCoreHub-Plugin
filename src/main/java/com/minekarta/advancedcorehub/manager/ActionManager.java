package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.actions.types.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, Action> actionMap = new HashMap<>();
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\[([A-Z_]+)\\]\\s*(.*)");

    public ActionManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        registerDefaultActions();
    }

    private void registerDefaultActions() {
        registerAction("PLAYER", new PlayerAction(plugin));
        registerAction("CONSOLE", new ConsoleAction(plugin));
        registerAction("MENU", new MenuAction(plugin)); // Will be implemented fully later
        registerAction("LINK", new LinkAction(plugin));
        registerAction("TITLE", new TitleAction(plugin));
        registerAction("SOUND", new SoundAction(plugin));
        registerAction("FIREWORK", new FireworkAction(plugin));
        registerAction("BROADCAST", new BroadcastAction(plugin));
        registerAction("ITEM", new ItemAction(plugin)); // Will depend on ItemsManager
        registerAction("BUNGEE", new BungeeAction(plugin));
        registerAction("CLOSE", new CloseAction(plugin));
        registerAction("CLEAR", new ClearAction(plugin));
        registerAction("LAUNCH", new LaunchAction(plugin));
        registerAction("SLOT", new SlotAction(plugin));
        registerAction("EFFECT", new EffectAction(plugin));
        registerAction("GAMEMODE", new GamemodeAction(plugin));
    }

    public void registerAction(String identifier, Action action) {
        actionMap.put(identifier.toUpperCase(), action);
    }

    public void executeActions(Player player, List<String> actionStrings) {
        for (String actionString : actionStrings) {
            executeAction(player, actionString);
        }
    }

    public void executeAction(Player player, String actionString) {
        if (actionString == null || actionString.isEmpty()) {
            return;
        }

        Matcher matcher = ACTION_PATTERN.matcher(actionString);
        if (!matcher.matches()) {
            plugin.getLogger().warning("Invalid action format: " + actionString);
            return;
        }

        String identifier = matcher.group(1).toUpperCase();
        String data = matcher.group(2);

        Action action = actionMap.get(identifier);
        if (action != null) {
            try {
                action.execute(player, data);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing action: " + actionString, e);
            }
        } else {
            plugin.getLogger().warning("Unknown action identifier: " + identifier);
        }
    }
}
