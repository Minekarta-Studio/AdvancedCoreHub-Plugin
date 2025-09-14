package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.actions.types.*;
import org.bukkit.configuration.ConfigurationSection;
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
        loadCustomActions();
    }

    private void loadCustomActions() {
        ConfigurationSection customActionsSection = plugin.getConfig().getConfigurationSection("custom-actions");
        if (customActionsSection == null) return;

        for (String key : customActionsSection.getKeys(false)) {
            List<Map<?, ?>> actions = customActionsSection.getMapList(key + ".actions");
            if (actions.isEmpty()) {
                plugin.getLogger().warning("Custom action '" + key + "' has no actions defined or is in the old format.");
                continue;
            }

            registerAction(key, (player, data) -> {
                executeMapActions(player, actions);
            });
            plugin.getLogger().info("Registered custom action: " + key);
        }
    }

    private void registerDefaultActions() {
        registerAction("PLAYER", new PlayerAction(plugin));
        registerAction("CONSOLE", new ConsoleAction(plugin));
        registerAction("MENU", new MenuAction(plugin));
        registerAction("LINK", new LinkAction(plugin));
        registerAction("TITLE", new TitleAction(plugin));
        registerAction("SOUND", new SoundAction(plugin));
        registerAction("FIREWORK", new FireworkAction(plugin));
        registerAction("BROADCAST", new BroadcastAction(plugin));
        registerAction("ITEM", new ItemAction(plugin));
        registerAction("BUNGEE", new BungeeAction(plugin));
        registerAction("CLOSE", new CloseAction(plugin));
        registerAction("CLEAR", new ClearAction(plugin));
        registerAction("LAUNCH", new LaunchAction(plugin));
        registerAction("SLOT", new SlotAction(plugin));
        registerAction("EFFECT", new EffectAction(plugin));
        registerAction("GAMEMODE", new GamemodeAction(plugin));
        registerAction("MOVEMENT", new MovementAction(plugin));
        registerAction("MESSAGE", new MessageAction(plugin));
    }

    public void registerAction(String identifier, Action action) {
        actionMap.put(identifier.toUpperCase(), action);
    }

    public void executeMapActions(Player player, List<Map<?, ?>> actionMaps) {
        if (actionMaps == null) return;
        for (Map<?, ?> actionMap : actionMaps) {
            executeAction(player, actionMap);
        }
    }

    public void executeAction(Player player, Map<?, ?> actionMap) {
        if (actionMap == null || !actionMap.containsKey("type")) {
            plugin.getLogger().warning("Invalid action map format: 'type' key is missing.");
            return;
        }

        String identifier = actionMap.get("type").toString().toUpperCase();
        Object data = actionMap.get("data");

        Action action = this.actionMap.get(identifier);
        if (action != null) {
            try {
                action.execute(player, data);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing action: " + actionMap, e);
            }
        } else {
            plugin.getLogger().warning("Unknown action identifier: " + identifier);
        }
    }

    public void executeStringActions(Player player, List<String> actionStrings) {
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
