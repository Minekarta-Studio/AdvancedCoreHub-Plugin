package com.minekarta.advancedcorehub.commands.subcommands;

import com.minekarta.advancedcorehub.commands.SubCommand;
import com.minekarta.advancedcorehub.util.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCmd extends SubCommand {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives a player a custom item.";
    }

    @Override
    public String getSyntax() {
        return "/ach give <player> <item> [amount] [slot]";
    }

    @Override
    public String getPermission() {
        return Permissions.CMD_GIVE;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getLocaleManager().sendMessage(sender, "invalid-usage", getSyntax());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getLocaleManager().sendMessage(sender, "player-not-found", args[0]);
            return;
        }

        String itemName = args[1];
        int amount = 1;
        int slot = -1;

        if (args.length > 2) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.getLocaleManager().sendMessage(sender, "invalid-number", args[2]);
                return;
            }
        }

        if (args.length > 3) {
            try {
                slot = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                plugin.getLocaleManager().sendMessage(sender, "invalid-number", args[3]);
                return;
            }
        }

        plugin.getItemsManager().giveItem(target, itemName, amount, slot);
        plugin.getLocaleManager().sendMessage(sender, "item-given", amount, itemName, target.getName());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            // This is tricky without access to the item map keySet.
            // A proper implementation would get the keys from ItemsManager.
            // For now, we'll leave it empty or provide a static list if possible.
            return new ArrayList<>(); // Or plugin.getItemsManager().getItemKeys();
        }
        return Collections.emptyList();
    }
}
