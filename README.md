# AdvancedCoreHub

AdvancedCoreHub is a comprehensive core plugin for PaperMC Minecraft servers, designed to manage hub-specific functionalities. It is built for Minecraft 1.20+ and runs on Java 21.

This project was developed by the AI assistant, Jules.

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

| Command | Description | Permission |
| --- | --- | --- |
| `/ach <subcommand>` | Root command for the plugin. | `advancedcorehub.command.help` |
| `/ach listitems` | Lists all available custom items. | `advancedcorehub.command.listitems` |
| `/fly [player] [duration]` | Toggles flight. Duration can be specified (e.g., `10s`, `5m`, `1h`). | `advancedcorehub.command.fly` |
| `/bossbar <create/remove/set> ...` | Manages global or per-player boss bars. | `advancedcorehub.command.bossbar` |
| `/spawn` | Teleports you to the server spawn. | `advancedcorehub.command.spawn` |
| `/setspawn` | Sets the server spawn location. | `advancedcorehub.command.setspawn` |
| `/lockchat` | Locks or unlocks the global chat. | `advancedcorehub.command.lockchat` |
| `/clearchat` | Clears the global chat. | `advancedcorehub.command.clearchat` |

## Configuration

### `items.yml`

This file defines all custom items.

```yaml
items:
  my_awesome_sword:
    material: DIAMOND_SWORD
    displayname: "<gold>My Awesome Sword"
    lore:
      - "<gray>This sword is awesome."
      - "<gray>Left-click for info, Right-click to attack."
    # (Optional) Add a custom texture via a resource pack
    custom-model-data: 1001
    # (Optional) Add enchantments
    enchantments:
      - "sharpness:5"
      - "unbreaking:3"
    # (Optional) Define actions for left and right clicks
    left-click-actions:
      - "[MESSAGE] This is my awesome sword!"
    right-click-actions:
      - "[SOUND] ENTITY_PLAYER_ATTACK_SWEEP;1.0;1.2" # Format: [SOUND]SOUND_NAME;VOLUME;PITCH
    # (Optional) Protect the item from being moved or dropped
    protected: true
```

### `menus/*.yml`

You can create any number of menu files in the `menus/` directory.

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
    # You can also use custom-model-data and enchantments here
    custom-model-data: 101
    enchantments: ["unbreaking:1"]
    # Actions are defined directly on the item
    actions:
      - "[MESSAGE] You clicked the info item!"
      - "[CLOSE]" # Closes the menu
```

### `config.yml` - Custom Actions

You can define your own reusable actions in `config.yml`.

```yaml
custom-actions:
  # This action can be triggered with "[welcome_pack]"
  welcome_pack:
    actions:
      - "[MESSAGE] <green>Here is your welcome pack!"
      - "[CONSOLE] give %player_name% golden_apple 1"
      - "[SOUND] ENTITY_PLAYER_LEVELUP;1.0;1.0"

  # This action can be triggered with "[website_link]"
  website_link:
    actions:
      - "[MESSAGE] <yellow>Our website is www.example.com"
```

### `config.yml` - Announcements

The announcements system has been completely overhauled.

```yaml
announcements:
  enabled: true
  interval_seconds: 90
  # If true, announcements will be chosen randomly. If false, they will appear in order.
  randomized: false
  # A list of announcements. Each is an object with its own settings.
  messages:
    # Example 1: A simple chat message for all players
    - type: CHAT
      message: "<yellow>Don't forget to visit our website!"

    # Example 2: A title message for players in specific worlds
    - type: TITLE
      message: "<green>TIP<gray>;<white>Vote for us with /vote!" # Title and subtitle separated by ;
      worlds:
        - "world"
        - "world_nether"

    # Example 3: An action bar message
    - type: ACTION_BAR
      message: "<aqua>Having fun? Let us know!"

    # Example 4: A boss bar message
    - type: BOSS_BAR
      message: "<gradient:blue:purple>We have a Discord server!</gradient>"
      color: PURPLE # Default: YELLOW
      style: SEGMENTED_10 # Default: SOLID
      duration: 10 # Default: 10 seconds
```

## Dependencies

### Required
-   None (the plugin can run standalone)

### Optional (Soft Dependencies)
-   **PlaceholderAPI**: For full placeholder support in messages and menus.
-   **HeadDatabase**: For using custom heads. To use a head, set the `material` to `hdb:<head_id>`.

## For Developers

### Action System API

You can register your own custom actions with the `ActionManager`.

**Example:**
```java
ActionManager am = AdvancedCoreHub.getInstance().getActionManager();
am.registerAction("MY_ACTION", (player, data) -> {
    player.setFoodLevel(20);
});
// Usage in YAML: "[MY_ACTION]"
```

## Building from Source

This project uses Apache Maven.
```bash
git clone <repository_url>
cd AdvancedCoreHub
mvn clean package
```

## Installation

1.  Requires Paper (or a fork) for Minecraft 1.20+.
2.  Install PlaceholderAPI and HeadDatabase (optional).
3.  Copy the JAR into your `plugins/` directory.
4.  Start the server. Customize the generated files and use `/ach reload`.