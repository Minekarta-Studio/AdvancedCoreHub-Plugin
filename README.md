# AdvancedCoreHub

AdvancedCoreHub is a comprehensive and professional core plugin for PaperMC servers, designed to provide a robust and feature-rich experience for your server's hub. Built for modern Minecraft (1.20+) and running on Java 21, it offers a suite of powerful tools to create a dynamic and engaging hub environment.

This project was developed by the AI assistant, Jules, with a focus on professional development standards, maintainability, and performance.

## Features

-   **Advanced Text Formatting**: Full support for **MiniMessage** (`<gradient>`, `<#HEX>`) and **legacy color codes** (`&c`) in all user-facing text. All text is non-italic by default for a clean, modern look.
-   **Dynamic Item Lores**: Use **PlaceholderAPI** placeholders in item names and lore to create dynamic, player-specific item descriptions.
-   **Advanced Item System**: Create custom items with unique display names, lore, enchantments, and custom model data.
-   **Configurable Interaction Sounds**: Add custom sounds to both GUIs and items. Specify unique open/click sounds for each menu, or add audio feedback to any item interaction.
-   **Custom GUI Icons**: Use custom player heads as icons in your menus via player name (`skull-owner`), Base64 texture (`head-texture`), or the **HeadDatabase** plugin.
-   **Hotbar / Join Items**: Automatically equip players with specific items when they join or enter a hub world.
-   **Item Protection**: Prevent players from dropping or moving specific items in hub worlds.
-   **Per-World Inventories**: Automatically saves and restores player inventories when they move between hub worlds and other worlds, ensuring no items are lost.
-   **Flexible Action System**: Define a series of actions to execute on item use or player join. Includes a wide range of built-in actions like `[MESSAGE]`, `[CONSOLE]`, `[MENU]`, and `[BUNGEE]`.
-   **Advanced Announcements**: A powerful announcement system with per-world messages, multiple display types (`CHAT`, `TITLE`, `ACTION_BAR`, `BOSS_BAR`), and a randomized mode.
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
*A full list of permissions can be found in the `plugin.yml` file.*

## Configuration

The plugin is highly configurable through a set of YAML files located in the `/plugins/AdvancedCoreHub/` directory.

### `config.yml`

This is the main configuration file. It allows you to enable/disable features, set spawn locations, manage hub worlds, configure announcements, and much more. The file is heavily commented to explain each option.

### `items.yml`

This file defines all custom items that can be given to players or used in menus.

**Item Properties:**
- `material`: The item material. Also supports `hdb:<id>` for HeadDatabase, `skull-owner:<name>`, and `head-texture:<base64>`.
- `displayname`: The item's name. Supports MiniMessage, legacy codes, and PlaceholderAPI.
- `lore`: The item's description. Supports MiniMessage, legacy codes, and PlaceholderAPI.
- `custom-model-data`: An integer for custom textures.
- `skull-owner`: (For `PLAYER_HEAD` material) The name of the player whose skin to use.
- `head-texture`: (For `PLAYER_HEAD` material) A Base64 texture string. Takes priority over `skull-owner`.
- `interact-sound`: (Optional) A sound to play when the item is clicked.
- `protected`: If true, players without permission cannot move or drop the item.
- `left-click-actions` / `right-click-actions`: A list of actions to run on click.

**Example:**
```yaml
items:
  player_profile:
    material: PLAYER_HEAD
    # This will show the skin of the player holding the item.
    skull-owner: "%player_name%"
    displayname: "<gradient:#12c2e9:#c471ed>My Profile</gradient>"
    lore:
      - "&7View your stats and settings."
      - ""
      - "<yellow>Player: <white>%player_name%"
      - "<yellow>Rank: <white>%vault_rank%"
    right-click-actions:
      - "[MENU] profile"
    interact-sound:
      enabled: true
      name: "block.note_block.pling"
      volume: 1.0
      pitch: 1.5
```

### `menus/*.yml`

You can create any number of menu files in the `menus/` directory. Each file represents a unique GUI menu.

**Menu Properties:**
- `title`: The title of the menu. Supports MiniMessage and legacy codes.
- `size`: The menu size (must be a multiple of 9).
- `open-sound`: (Optional) A sound to play when the menu is opened. Defaults to a chest opening sound.
- `click-sound`: (Optional) A sound to play when any item is clicked.
- `items`: A section defining the items within the menu.
- `filler-item`: (Optional) An item to fill all empty slots.

**Example:**
```yaml
# menus/main_menu.yml
title: "<gradient:#f64f59:#c471ed>Main Menu</gradient>"
size: 27
open-sound:
  name: "block.chest.open"
  volume: 0.8
  pitch: 1.0
click-sound:
  name: "ui.button.click"
  volume: 1.0
  pitch: 1.0
items:
  server_selector:
    material: COMPASS
    slot: 13
    display-name: "<green>Server Selector"
    lore:
      - "<gray>Click to browse servers!"
    right-click-actions:
      - "[MENU] servers"
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