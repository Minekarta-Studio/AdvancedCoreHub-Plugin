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
| `/bossbar <create/remove> ...` | Manages global or per-player boss bars. | `advancedcorehub.command.bossbar` |
| `/spawn` | Teleports you to the server spawn. | `advancedcorehub.command.spawn` |
| `/setspawn` | Sets the server spawn location. | `advancedcorehub.command.setspawn` |
| `/lockchat` | Locks or unlocks the global chat. | `advancedcorehub.command.lockchat` |
| `/clearchat` | Clears the global chat. | `advancedcorehub.command.clearchat` |

## Dependencies

### Required
-   None (the plugin can run standalone)

### Optional (Soft Dependencies)
-   **PlaceholderAPI**: For full placeholder support in messages and menus.
-   **HeadDatabase**: For using custom heads in menus.

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