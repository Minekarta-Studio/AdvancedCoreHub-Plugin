package com.minekarta.advancedcorehub.manager;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.actions.types.*;
import com.minekarta.advancedcorehub.config.PluginConfig;
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
    private final PluginConfig config;
    private final Map<String, Action> actionMap = new HashMap<>();
    private final java.util.Set<String> customActionNames = new java.util.HashSet<>();
    // Pattern to match actions like [ACTION] data or [ACTION:data]
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\[([A-Z_]+)(?::\\s*(.*?))?\\](.*)");

    public ActionManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
        registerDefaultActions();
        loadCustomActions();
    }

    private void loadCustomActions() {
        if (config.getCustomActions() == null) return;

        for (Map.Entry<String, ?> entry : config.getCustomActions().entrySet()) {
            String key = entry.getKey();
            if (!(entry.getValue() instanceof Map)) continue;

            @SuppressWarnings("unchecked")
            Map<String, Object> actionData = (Map<String, Object>) entry.getValue();

            final List<String> actionStrings = (List<String>) actionData.get("actions");

            if (actionStrings == null || actionStrings.isEmpty()) {
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
        MessageAction messageAction = new MessageAction(plugin);
        registerAction("MESSAGE", messageAction);
        registerAction("LANG", messageAction);
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
                // Custom actions are designed to take a list of arguments for templating,
                // where the first argument is the action name itself.
                // Default actions, however, expect to receive the raw data string as a single argument.
                // This logic correctly dispatches the data in the expected format for each type.
                if (customActionNames.contains(identifier)) {
                    action.execute(player, args);
                } else {
                    action.execute(player, combinedData != null ? combinedData.trim() : "");
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing action: " + actionString, e);
            }
        } else {
            plugin.getLogger().warning("Unknown action identifier: " + identifier);
        }
    }
}