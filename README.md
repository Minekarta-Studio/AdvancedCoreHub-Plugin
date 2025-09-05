# AdvancedCoreHub

AdvancedCoreHub is a comprehensive core plugin for PaperMC Minecraft servers, designed to manage hub-specific functionalities. It is built for Minecraft 1.20+ and runs on Java 21.

This project was developed by the AI assistant, Jules.

## Features

-   Custom Item System with Actions
-   Movement Items (Teleporting Trident, Grappling Hook, etc.)
-   Flexible Action System (`[PLAYER]`, `[CONSOLE]`, `[MENU]`, etc.)
-   Multi-language Support
-   Per-World Event Cancellation
-   And much more!

## Dependencies

### Required
-   None (the plugin can run standalone)

### Optional (Soft Dependencies)
-   **PlaceholderAPI**: For full placeholder support in messages.
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

3.  The compiled JAR file will be located in the `target/` directory, named `AdvancedCoreHub-x.y.z-SNAPSHOT.jar`.

## Installation

1.  Ensure your server is running Paper or a fork of Paper for Minecraft 1.20 or newer.
2.  Install the soft dependencies (PlaceholderAPI, HeadDatabase) if you wish to use their features.
3.  Copy the compiled `AdvancedCoreHub-x.y.z-SNAPSHOT.jar` into your server's `plugins/` directory.
4.  Start or restart your server. The default configuration files will be generated in the `plugins/AdvancedCoreHub/` directory.
5.  Customize the configuration files (`config.yml`, `items.yml`, etc.) to your liking and use `/ach reload` to apply the changes.