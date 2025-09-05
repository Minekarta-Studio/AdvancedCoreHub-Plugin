# AdvancedCoreHub

AdvancedCoreHub is a comprehensive core plugin for PaperMC Minecraft servers, designed to manage hub-specific functionalities. It is built for Minecraft 1.20+ and runs on Java 21.

This project was developed by the AI assistant, Jules, with a focus on stability, feature-richness, and modern development practices.

## Features

-   **Custom Items & Actions**: Create items with custom names, lore, and attached actions. Hotbar items given on join are now fully functional.
-   **Flexible Action System**: A powerful, chainable action system (`[PLAYER]`, `[CONSOLE]`, `[MENU]`, `[SOUND]`, `[EFFECT]`, `[LAUNCH]`, etc.) to create complex interactions. The default configuration now includes examples like a launch feather and a speed boost item.
-   **Enhanced Commands**:
    -   `/fly`: Now supports temporary flight (e.g., `/fly 60` for 60 seconds) for yourself or other players.
    -   `/clearchat`: Now includes a bypass permission (`advancedcorehub.clearchat.bypass`).
-   **Movement Items**: Special items like a Teleporting Trident and a Grappling Hook.
-   **Unit Tested**: Core components, like the text formatting system, are validated by a suite of unit tests to ensure stability.
-   **Multi-language Support**: All player-facing messages can be translated.
-   **Per-World Event Cancellation**: Disable events like block breaking and damage in specific worlds.

## Commands

-   `/ach reload`: Reloads the plugin's configuration files.
-   `/ach give <player> <item_id>`: Gives a custom item to a player.
-   `/fly [player] [duration]`: Toggles flight for yourself or another player. If a duration (in seconds) is provided, flight is enabled temporarily.
-   `/clearchat`: Clears the chat for all players who do not have the `advancedcorehub.clearchat.bypass` permission.
-   `/lockchat`: Prevents players without the bypass permission from talking in chat.
-   `/spawn`: Teleports you to the server's spawn.
-   `/setspawn`: Sets the server's spawn to your current location.

## Dependencies

### Required
-   None (the plugin can run standalone)

### Optional (Soft Dependencies)
-   **PlaceholderAPI**: For full placeholder support in messages.
-   **HeadDatabase**: For using custom heads in menus.

## For Developers

### Building from Source

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

### Running Tests

This project includes a suite of unit tests to ensure code quality and stability. To run the tests, execute the following Maven command:

```bash
mvn clean test
```

## Installation

1.  Ensure your server is running Paper or a fork of Paper for Minecraft 1.20 or newer.
2.  Install the soft dependencies (PlaceholderAPI, HeadDatabase) if you wish to use their features.
3.  Copy the compiled `AdvancedCoreHub-x.y.z-SNAPSHOT.jar` into your server's `plugins/` directory.
4.  Start or restart your server. The default configuration files will be generated in the `plugins/AdvancedCoreHub/` directory.
5.  Customize the configuration files (`config.yml`, `items.yml`, etc.) to your liking and use `/ach reload` to apply the changes.