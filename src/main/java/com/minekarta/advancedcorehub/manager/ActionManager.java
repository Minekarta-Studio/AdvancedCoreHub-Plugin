package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.actions.types.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ActionManager {

    private final AdvancedCoreHub plugin;
    private final Map<String, Action> actionMap = new HashMap<>();
    // Pattern to match actions like [ACTION] data or [ACTION:data]
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\[([A-Z_]+)(?::\\s*(.*?))?\\](.*)");

    public ActionManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        registerDefaultActions();
        loadCustomActions();
    }

    private void loadCustomActions() {
        ConfigurationSection customActionsSection = plugin.getConfig().getConfigurationSection("custom-actions");
        if (customActionsSection == null) return;

        for (String key : customActionsSection.getKeys(false)) {
            final List<String> actionStrings = customActionsSection.getStringList(key + ".actions");
            if (actionStrings.isEmpty()) {
                plugin.getLogger().warning("Custom action '" + key + "' has no actions defined.");
                continue;
            }

            registerAction(key.toUpperCase(), (player, data) -> {
                if (!(data instanceof List)) return;
                List<String> args = (List<String>) data;
                List<String> processedActionStrings = new ArrayList<>();

                for (String actionString : actionStrings) {
                    String processedAction = actionString;
                    // args[0] is the action name, so args start from index 1.
                    for (int i = 1; i < args.size(); i++) {
                        processedAction = processedAction.replace("%arg" + (i) + "%", args.get(i));
                    }
                    processedActionStrings.add(processedAction);
                }
                executeStringActions(player, processedActionStrings);
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

    public void executeMapActions(Player player, List<Map<?, ?>> actionMaps, String... args) {
        if (actionMaps == null) return;
        for (Map<?, ?> actionMap : actionMaps) {
            // Deep copy the map to avoid modifying the original config
            Map<String, Object> newActionMap = new HashMap<>();
            for (Map.Entry<?, ?> entry : actionMap.entrySet()) {
                newActionMap.put(entry.getKey().toString(), entry.getValue());
            }

            // Process placeholders in the data
            Object data = newActionMap.get("data");
            if (data instanceof String) {
                String processedData = (String) data;
                for (int i = 0; i < args.length; i++) {
                    processedData = processedData.replace("%arg" + (i + 1) + "%", args[i]);
                }
                newActionMap.put("data", processedData);
            } else if (data instanceof Map) {
                // Recursively process placeholders in nested maps (e.g., for TITLE action)
                Map<String, Object> newNestedMap = processNestedMap((Map<?, ?>) data, args);
                newActionMap.put("data", newNestedMap);
            }

            executeAction(player, newActionMap);
        }
    }

    private Map<String, Object> processNestedMap(Map<?, ?> nestedMap, String... args) {
        Map<String, Object> newNestedMap = new HashMap<>();
        for (Map.Entry<?, ?> entry : nestedMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String processedValue = (String) value;
                for (int i = 0; i < args.length; i++) {
                    processedValue = processedValue.replace("%arg" + (i + 1) + "%", args[i]);
                }
                newNestedMap.put(entry.getKey().toString(), processedValue);
            } else {
                newNestedMap.put(entry.getKey().toString(), value);
            }
        }
        return newNestedMap;
    }

    public void executeMapActions(Player player, List<Map<?, ?>> actionMaps) {
        executeMapActions(player, actionMaps, new String[0]);
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
        if (actionStrings == null) return;
        for (String actionString : actionStrings) {
            executeAction(player, actionString);
        }
    }

    public void executeAction(Player player, String actionString) {
        if (actionString == null || actionString.isEmpty()) {
            return;
        }

        Matcher matcher = ACTION_PATTERN.matcher(actionString.trim());
        if (!matcher.matches()) {
            // This string doesn't match our action format, so we ignore it.
            // This can be useful for comments or other text in action lists.
            return;
        }

        String identifier = matcher.group(1).toUpperCase();
        String dataInBrackets = matcher.group(2);
        String dataOutsideBrackets = matcher.group(3);

        List<String> args = new ArrayList<>();
        args.add(identifier);

        String combinedData;
        if (dataInBrackets != null && !dataInBrackets.isEmpty()) {
            combinedData = dataInBrackets;
        } else {
            combinedData = dataOutsideBrackets;
        }

        if (combinedData != null && !combinedData.trim().isEmpty()) {
            // Split the data part by spaces to handle multiple arguments
            args.addAll(Arrays.stream(combinedData.trim().split("\\s+"))
                    .collect(Collectors.toList()));
        }

        Action action = actionMap.get(identifier);
        if (action != null) {
            try {
                action.execute(player, args);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing action: " + actionString, e);
            }
        } else {
            plugin.getLogger().warning("Unknown action identifier: " + identifier);
        }
    }
}
