package com.minekarta.advancedcorehub.actions.types;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import com.minekarta.advancedcorehub.actions.Action;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MessageAction implements Action {

    private final AdvancedCoreHub plugin;

    public MessageAction(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, Object data) {
        String message;
        if (data instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> args = (List<String>) data;
            if (args.size() < 2) return;
            message = args.subList(1, args.size()).stream().map(String::trim).collect(Collectors.joining(" "));
        } else if (data instanceof String) {
            message = ((String) data).trim();
        } else {
            return;
        }

        if (message.isEmpty()) return;
        plugin.getLocaleManager().sendMessage(player, message);
    }
}
