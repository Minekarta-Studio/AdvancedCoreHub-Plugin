# AdvancedCoreHub

AdvancedCoreHub is a comprehensive and professional core plugin for PaperMC servers, designed to provide a robust and feature-rich experience for your server's hub. Built for modern Minecraft (1.20+) and running on Java 21, it offers a suite of powerful tools to create a dynamic and engaging hub environment.

This project was developed by the AI assistant, Jules, with a focus on professional development standards, maintainability, and performance.

## Features

-   **Full MiniMessage Support**: All user-facing text, from chat messages to item lore, supports the full range of MiniMessage formatting, including gradients, hover/click events, and more. All text is non-italic by default for a clean, modern look.
-   **Advanced Item System**: Create custom items with unique display names, lore, enchantments, custom model data, and separate actions for left and right clicks.
-   **Hotbar / Join Items**: Automatically equip players with specific items when they join or enter a hub world.
-   **Item Protection**: Prevent players from dropping or moving specific items in hub worlds.
-   **Per-World Inventories**: Automatically saves and restores player inventories when they move between hub worlds and other worlds, ensuring no items are lost.
-   **Flexible Action System**: Define a series of actions to execute on item use or player join. Includes a wide range of built-in actions like `[MESSAGE]`, `[CONSOLE]`, `[MENU]`, and `[BUNGEE]`. The `[SOUND]` action is enhanced to support volume and pitch (`[SOUND]NAME;VOL;PITCH`). You can also create your own custom actions in `config.yml`.
-   **Configurable GUI Menus**: Create fully custom GUI menus from YAML files. Items in menus support all the features of the advanced item system (lore, model data, etc.).
-   **Advanced Announcements**: A powerful announcement system with per-world messages, multiple display types (`CHAT`, `TITLE`, `ACTION_BAR`, `BOSS_BAR`), and a randomized mode.
-   **Movement Items**: A unified listener handles all movement items (Trident, Grappling Hook, Custom Elytra, etc.), with configurable cooldowns and settings.
-   **Admin Commands**: A suite of commands to manage the server hub.
-   **Per-World Event Cancellation**: Disable events like block breaking and hunger loss in specified hub worlds.
-   **Multi-language Support**: All messages can be translated.

## Commands

The plugin's commands are organized for ease of use. The main administrative command is `/ach` (aliases: `/advancedcorehub`, `/acore`, `/ahub`).

### Player Commands

| Command | Description |
| --- | --- |
| `/fly [player] [duration]` | Toggles flight. Duration can be specified (e.g., `10s`, `5m`, `1h`). |
| `/spawn` | Teleports you to the server spawn. |

### Admin Commands

| Command | Description |
| --- | --- |
| `/setspawn` | Sets the server spawn location. |
| `/bossbar <...>` | Manages global or per-player boss bars. |
| `/ach <subcommand>` | The root command for all other administrative functions. |

### `/ach` Subcommands

| Subcommand | Description |
| --- | --- |
| `help` | Shows the plugin's help menu. |
| `reload` | Reloads all plugin configuration files. |
| `version` | Shows the current plugin version. |
| `give <player> <item> [amount]` | Gives a player a custom item. |
| `listitems` | Lists all available custom items. |
| `clearchat` | Clears the global chat for all players. |
| `lockchat` | Toggles the chat lock on or off. |
| `worlds <add/remove/list>` | Manages the list of hub worlds. |

## Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `advancedcorehub.command.fly` | Allows a player to use the `/fly` command for themselves. | OP |
| `advancedcorehub.command.fly.others` | Allows a player to use the `/fly` command for other players. | OP |
| `advancedcorehub.command.spawn` | Allows a player to use the `/spawn` command. | Everyone |
| `advancedcorehub.command.setspawn` | Allows a player to use the `/setspawn` command. | OP |
| `advancedcorehub.command.reload` | Allows a player to use the `/ach reload` command. | OP |
| `advancedcorehub.command.give` | Allows a player to use the `/ach give` command. | OP |
| `advancedcorehub.command.listitems` | Allows a player to use the `/ach listitems` command. | OP |
| `advancedcorehub.command.worlds` | Allows a player to manage the hub worlds list. | OP |
| `advancedcorehub.command.clearchat` | Allows a player to use the `/ach clearchat` command. | OP |
| `advancedcorehub.command.lockchat` | Allows a player to use the `/ach lockchat` command. | OP |
| `advancedcorehub.command.bossbar` | Allows a player to use the `/bossbar` command. | OP |
| `advancedcorehub.bypass.cooldown` | Allows a player to bypass all item cooldowns. | OP |
| `advancedcorehub.bypass.chatlock` | Allows a player to chat when the chat is locked. | OP |
| `advancedcorehub.bypass.worldguard` | Allows a player to bypass world event cancellations (e.g., block breaking). | OP |

## Configuration

The plugin is highly configurable through a set of YAML files located in the `/plugins/AdvancedCoreHub/` directory.

### `config.yml`

This is the main configuration file. It allows you to enable/disable features, set spawn locations, manage hub worlds, configure announcements, and much more. The file is heavily commented to explain each option.

### `items.yml`

This file defines all custom items that can be given to players or used in menus.

**Example:**
```yaml
items:
  my_awesome_sword:
    material: DIAMOND_SWORD
    displayname: "<gold>My Awesome Sword"
    lore:
      - "<gray>This sword is awesome."
      - "<gray>Left-click for info, Right-click to attack."
    custom-model-data: 1001
    enchantments:
      - "sharpness:5"
      - "unbreaking:3"
    left-click-actions:
      - "[MESSAGE] This is my awesome sword!"
    right-click-actions:
      - "[SOUND] ENTITY_PLAYER_ATTACK_SWEEP;1.0;1.2"
    protected: true
```

### `menus/*.yml`

You can create any number of menu files in the `menus/` directory. Each file represents a unique GUI menu.

**Example:**
```yaml
# menus/my_menu.yml
title: "<blue>My Custom Menu"
size: 27 # Must be a multiple of 9
items:
  info_item:
    material: BOOK
    slot: 13
    display-name: "<green>Information"
    lore:
      - "<gray>This is an item in my menu."
    actions:
      - "[MESSAGE] You clicked the info item!"
      - "[CLOSE]"
filler-item:
  material: GRAY_STAINED_GLASS_PANE
  display-name: " "
```

## For Developers

### Action System API

You can register your own custom actions with the `ActionManager` to extend the plugin's functionality.

**Example:**
```java
// Get the ActionManager instance
ActionManager am = AdvancedCoreHub.getInstance().getActionManager();

// Register a new action
am.registerAction("MY_ACTION", (player, data) -> {
    player.setFoodLevel(20);
    player.sendMessage("You have been fed!");
});

// You can now use "[MY_ACTION]" in your items.yml or menus!
```

## Building from Source

This project uses Apache Maven for building.

1.  Clone the repository: `git clone <repository_url>`
2.  Navigate to the project directory: `cd AdvancedCoreHub`
3.  Build the project: `mvn clean package`

The compiled JAR will be located in the `target/` directory.

## Installation

1.  Requires Paper (or a fork) for Minecraft 1.20+.
2.  Install PlaceholderAPI (optional, for placeholder support).
3.  Copy the compiled JAR into your server's `plugins/` directory.
4.  Start the server to generate the default configuration files.
5.  Customize the generated files and use `/ach reload` to apply changes.