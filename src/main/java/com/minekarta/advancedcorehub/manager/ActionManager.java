package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.actions.types.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ActionManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, Action> actionMap = new HashMap<>();

    public ActionManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        // The ActionManager is recreated on reload, ensuring a clean state.
        registerDefaultActions();
        loadCustomActions();
    }

    private void loadCustomActions() {
        var customActionsConfig = plugin.getPluginConfig().getCustomActions();
        if (customActionsConfig == null) return;

        for (Map.Entry<String, ?> entry : customActionsConfig.entrySet()) {
            String key = entry.getKey();
            if (!(entry.getValue() instanceof Map<?, ?> actionData)) continue;

            if (!(actionData.get("actions") instanceof List<?> rawActionStrings)) {
                plugin.getLogger().warning("Custom action '" + key + "' has no 'actions' list or it's empty.");
                continue;
            }

            List<String> actionStrings = rawActionStrings.stream().map(Object::toString).collect(Collectors.toList());
            registerAction(key.toUpperCase(), new CustomAction(this, actionStrings));
        }
        plugin.getLogger().info("Loaded " + customActionsConfig.size() + " custom actions.");
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
        MessageAction messageAction = new MessageAction(plugin);
        registerAction("MESSAGE", messageAction);
        registerAction("LANG", messageAction); // Alias for MESSAGE
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
                plugin.getLogger().log(Level.SEVERE, "Error executing map-based action: " + actionMap, e);
            }
        } else {
            plugin.getLogger().warning("Unknown action identifier: " + identifier);
        }
    }

    public void executeStringActions(Player player, List<String> actionStrings) {
        if (actionStrings == null) return;
        for (String actionString : actionStrings) {
            executeAction(player, actionString);
        }
    }

    public void executeAction(Player player, String actionString) {
        if (actionString == null || actionString.isBlank()) {
            return;
        }

        String trimmedAction = actionString.trim();
        if (!trimmedAction.startsWith("[")) {
            return; // Not a valid action format.
        }

        int closingBracketIndex = trimmedAction.indexOf(']');
        if (closingBracketIndex == -1) {
            plugin.getLogger().warning("Malformed action string (missing ']]'): " + actionString);
            return;
        }

        String identifier = trimmedAction.substring(1, closingBracketIndex).toUpperCase();
        String data = trimmedAction.substring(closingBracketIndex + 1).trim();

        Action action = actionMap.get(identifier);
        if (action != null) {
            try {
                // All actions, including CustomAction, now receive the raw data string.
                // The action itself is responsible for parsing this data.
                action.execute(player, data);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing string-based action: " + actionString, e);
            }
        } else {
            plugin.getLogger().warning("Unknown action identifier: " + identifier);
        }
    }
}