# AdvancedCoreHub

AdvancedCoreHub is a comprehensive core plugin for PaperMC Minecraft servers, designed to manage hub-specific functionalities. It is built for Minecraft 1.20+ and runs on Java 21.

This project was developed by the AI assistant, Jules.

## Features

-   **Full MiniMessage Support**: All user-facing text, from chat messages to item lore, supports the full range of MiniMessage formatting, including gradients, hover/click events, and more.
-   **Dynamic GUI Menus**: All menus (e.g., `/server selector`) are created dynamically for each player, allowing for full use of PlaceholderAPI placeholders (like `%player_name%`) in titles and item text.
-   **Custom Item System**: Create custom items with unique display names, lore, and attached actions.
-   **Flexible Action System**: Define a series of actions to execute on item use or player join, including `[MESSAGE]`, `[CONSOLE]`, `[MENU]`, `[SOUND]`, and `[BUNGEE]`.
-   **Refactored Movement Items**: A unified listener handles all movement items (Trident, Grappling Hook, etc.), reducing code duplication and making it easy to add more.
-   **Admin Commands**: A suite of commands to manage the server hub.
    -   `/fly` with temporary flight support (e.g., `/fly Jules 10m`).
    -   `/bossbar` to create and remove global or per-player boss bars.
    -   `/lockchat`, `/clearchat`, `/setspawn`, and more.
-   **Configurable Announcements**: Broadcast automated messages via chat or a global Boss Bar.
-   **Per-World Event Cancellation**: Disable events like block breaking and hunger loss in specified hub worlds.
-   **Multi-language Support**: All messages can be translated (default languages: English and Portuguese).

## Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/ach <subcommand>` | Root command for the plugin. | `advancedcorehub.command.help` |
| `/fly [player] [duration]` | Toggles flight. Duration can be specified (e.g., `10s`, `5m`, `1h`). | `advancedcorehub.command.fly` |
| `/bossbar <create/remove/set> ...` | Manages global or per-player boss bars. | `advancedcorehub.command.bossbar` |
| `/spawn` | Teleports you to the server spawn. | `advancedcorehub.command.spawn` |
| `/setspawn` | Sets the server spawn location. | `advancedcorehub.command.setspawn` |
| `/lockchat` | Locks or unlocks the global chat. | `advancedcorehub.command.lockchat` |
| `/clearchat` | Clears the global chat. | `advancedcorehub.command.clearchat` |

## Dependencies

### Required
-   None (the plugin can run standalone)

### Optional (Soft Dependencies)
-   **PlaceholderAPI**: For full placeholder support in messages and menus.
-   **HeadDatabase**: For using custom heads in menus. To use a head from HeadDatabase, set the `material` of an item in your menu configuration to `hdb:<head_id>` or `headdatabase:<head_id>`.

## For Developers

### Action System API

AdvancedCoreHub allows other plugins to register their own custom actions. To do this, you can access the `ActionManager` and register your own `Action` implementation.

**Example:**
```java
// In your plugin's onEnable, after AdvancedCoreHub has loaded
if (getServer().getPluginManager().isPluginEnabled("AdvancedCoreHub")) {
    ActionManager actionManager = AdvancedCoreHub.getInstance().getActionManager();

    // Create your custom action
    Action myCustomAction = (player, data) -> {
        player.sendMessage("Executing my custom action with data: " + data);
    };

    // Register it
    actionManager.registerAction("MY_CUSTOM_ACTION", myCustomAction);
}
```

You can then use `[MY_CUSTOM_ACTION] some data` in your item actions, just like any other action.

## Future Improvements

-   **Command Framework**: The current command system is functional, but could be migrated to a more robust framework like ACF (Advanced Command Framework) to provide features like improved tab-completion and validation.
-   **Safe Teleportation**: The teleportation for movement items has been improved, but could be enhanced further with more sophisticated checks for things like server lag or other plugins interfering.

## Building from Source

This project uses Apache Maven for building.

1.  **Clone the repository:**
    ```bash
    git clone <repository_url>
    cd AdvancedCoreHub
    ```

2.  **Build the project:**
    ```bash
    mvn clean package
    ```

3.  The compiled JAR file will be located in the `target/` directory.

## Installation

1.  Ensure your server is running Paper or a fork of Paper for Minecraft 1.20 or newer.
2.  Install the soft dependencies (PlaceholderAPI, HeadDatabase) if you wish to use their features.
3.  Copy the compiled JAR into your server's `plugins/` directory.
4.  Start or restart your server. The default configuration files will be generated.
5.  Customize the configuration files (`config.yml`, `items.yml`, etc.) to your liking and use `/ach reload` to apply the changes.