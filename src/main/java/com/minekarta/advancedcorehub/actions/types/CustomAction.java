package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.actions.Action;
import com.minekarta.advancedcorehub.manager.ActionManager;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * An action that executes a series of other actions, acting as a macro.
 * It supports argument substitution for dynamic execution.
 */
public class CustomAction implements Action {

    private final ActionManager actionManager;
    private final List<String> actionStrings;

    public CustomAction(ActionManager actionManager, List<String> actionStrings) {
        this.actionManager = actionManager;
        this.actionStrings = actionStrings;
    }

    @Override
    public void execute(Player player, Object data) {
        String[] args = (data instanceof String) ? ((String) data).split(" ") : new String[0];

        for (String actionString : actionStrings) {
            String processedAction = actionString;
            // Replace %arg1%, %arg2%, etc. with the provided arguments.
            for (int i = 0; i < args.length; i++) {
                processedAction = processedAction.replace("%arg" + (i + 1) + "%", args[i]);
            }
            // Execute the processed action string.
            actionManager.executeAction(player, processedAction);
        }
    }
}