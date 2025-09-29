package com.minekarta.advancedcorehub.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PluginConfig {

    // Nested configuration sections
    public final SpawnConfig spawn;
    public final MessagesConfig messages;
    public final WorldSettingsConfig worldSettings;
    public final ChatProtectionConfig chatProtection;
    public final AntiWorldDownloaderConfig antiWorldDownloader;
    public final InventoryManagementConfig inventoryManagement;
    public final MovementItemsConfig movementItems;
    public final DoubleJumpConfig doubleJump;
    public final MenuSoundsConfig menuSounds;
    public final AnnouncementsConfig announcements;
    public final BossBarConfig bossBar;
    // Top-level settings
    private final String language;
    private final List<String> hubWorlds;
    private final List<Map<?, ?>> actionsOnJoin;
    private final Map<String, ?> customActions;

    public PluginConfig(FileConfiguration source) {
        // A null source can happen if the config file fails to load.
        // Create an empty one to prevent NullPointerExceptions.
        if (source == null) {
            source = new YamlConfiguration();
        }

        Logger logger = Logger.getLogger("AdvancedCoreHub"); // Or get from plugin instance if available

        // Load top-level settings
        this.language = source.getString("language", "en");
        this.hubWorlds = source.getStringList("hub-worlds");
        this.actionsOnJoin = source.getMapList("actions_on_join");

        ConfigurationSection customActionsSection = getSection(source, "custom-actions", logger);
        this.customActions = customActionsSection.getValues(false);

        // Load nested sections safely
        this.spawn = new SpawnConfig(getSection(source, "spawn", logger));
        this.messages = new MessagesConfig(getSection(source, "messages", logger));
        this.worldSettings = new WorldSettingsConfig(getSection(source, "world_settings", logger));
        this.chatProtection = new ChatProtectionConfig(getSection(source, "chat_protection", logger));
        this.antiWorldDownloader = new AntiWorldDownloaderConfig(getSection(source, "anti_world_downloader", logger));
        this.inventoryManagement = new InventoryManagementConfig(getSection(source, "inventory_management", logger));
        this.movementItems = new MovementItemsConfig(getSection(source, "movement_items", logger));
        this.doubleJump = new DoubleJumpConfig(getSection(source, "double_jump", logger));
        this.menuSounds = new MenuSoundsConfig(getSection(source, "menu_sounds", logger));
        this.announcements = new AnnouncementsConfig(getSection(source, "announcements", logger));
        this.bossBar = new BossBarConfig(getSection(source, "bossbar", logger));
    }

    /**
     * Safely retrieves a ConfigurationSection, logging a warning if it's missing.
     * @return A valid (potentially empty) ConfigurationSection, never null.
     */
    private ConfigurationSection getSection(ConfigurationSection parent, String key, Logger logger) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) {
            logger.warning("Configuration section '" + key + "' is missing from the config. Using default values.");
            // Create an empty section in memory to avoid NullPointerExceptions
            return parent.createSection(key);
        }
        return section;
    }


    // --- Getters for top-level settings ---
    public String getLanguage() { return language; }
    public List<String> getHubWorlds() { return hubWorlds; }
    public List<Map<?, ?>> getActionsOnJoin() { return actionsOnJoin; }
    public Map<String, ?> getCustomActions() { return customActions; }

    // --- Nested Classes for each configuration section ---

    public static class SpawnConfig {
        public final String world;
        public final double x, y, z;
        public final float yaw, pitch;
        public final boolean spawnOnJoinEnabled;

        public SpawnConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "SpawnConfig section cannot be null.");
            this.world = section.getString("world", "world");
            this.x = section.getDouble("x", 0.0);
            this.y = section.getDouble("y", 100.0);
            this.z = section.getDouble("z", 0.0);
            this.yaw = (float) section.getDouble("yaw", 0.0);
            this.pitch = (float) section.getDouble("pitch", 0.0);
            this.spawnOnJoinEnabled = section.getBoolean("spawn-on-join.enabled", true);
        }
    }

    public static class MessagesConfig {
        public final String prefix;

        public MessagesConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "MessagesConfig section cannot be null.");
            this.prefix = section.getString("prefix", "");
        }
    }

    public static class WorldSettingsConfig {
        public final boolean cancelBlockBreak;
        public final boolean cancelBlockPlace;
        public final boolean cancelPlayerDamage;
        public final boolean cancelHungerLoss;
        public final boolean cancelWeatherChange;

        public WorldSettingsConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "WorldSettingsConfig section cannot be null.");
            this.cancelBlockBreak = section.getBoolean("cancel_block_break", true);
            this.cancelBlockPlace = section.getBoolean("cancel_block_place", true);
            this.cancelPlayerDamage = section.getBoolean("cancel_player_damage", true);
            this.cancelHungerLoss = section.getBoolean("cancel_hunger_loss", true);
            this.cancelWeatherChange = section.getBoolean("cancel_weather_change", true);
        }
    }

    public static class ChatProtectionConfig {
        public final AntiSwearConfig antiSwear;
        public final CommandBlockerConfig commandBlocker;

        public ChatProtectionConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "ChatProtectionConfig section cannot be null.");
            Logger logger = Logger.getLogger("AdvancedCoreHub");
            this.antiSwear = new AntiSwearConfig(getSection(section, "anti_swear", logger));
            this.commandBlocker = new CommandBlockerConfig(getSection(section, "command_blocker", logger));
        }

        private ConfigurationSection getSection(ConfigurationSection parent, String key, Logger logger) {
            ConfigurationSection section = parent.getConfigurationSection(key);
            if (section == null) {
                logger.warning("Configuration subsection '" + parent.getCurrentPath() + "." + key + "' is missing. Using default values.");
                return parent.createSection(key);
            }
            return section;
        }

        public static class AntiSwearConfig {
            public final boolean enabled;
            public final List<String> blockedWords;

            public AntiSwearConfig(ConfigurationSection section) {
                Objects.requireNonNull(section, "AntiSwearConfig section cannot be null.");
                this.enabled = section.getBoolean("enabled", false);
                this.blockedWords = section.getStringList("blocked_words");
            }
        }

        public static class CommandBlockerConfig {
            public final boolean enabled;
            public final List<String> blockedCommands;

            public CommandBlockerConfig(ConfigurationSection section) {
                Objects.requireNonNull(section, "CommandBlockerConfig section cannot be null.");
                this.enabled = section.getBoolean("enabled", false);
                this.blockedCommands = section.getStringList("blocked_commands");
            }
        }
    }

    public static class AntiWorldDownloaderConfig {
        public final boolean enabled;

        public AntiWorldDownloaderConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "AntiWorldDownloaderConfig section cannot be null.");
            this.enabled = section.getBoolean("enabled", true);
        }
    }

    public static class InventoryManagementConfig {
        public final boolean enable;
        public final boolean saveAndRestore;
        public final boolean clearOnEnter;

        public InventoryManagementConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "InventoryManagementConfig section cannot be null.");
            this.enable = section.getBoolean("enable", true);
            this.saveAndRestore = section.getBoolean("save-and-restore", true);
            this.clearOnEnter = section.getBoolean("clear-on-enter", true);
        }
    }

    public static class MovementItemsConfig {
        public final TridentConfig trident;
        public final GrapplingHookConfig grapplingHook;
        public final AoteConfig aote;
        public final EnderbowConfig enderbow;
        public final CustomElytraConfig customElytra;

        public MovementItemsConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "MovementItemsConfig section cannot be null.");
            Logger logger = Logger.getLogger("AdvancedCoreHub");
            this.trident = new TridentConfig(getSection(section, "trident", logger));
            this.grapplingHook = new GrapplingHookConfig(getSection(section, "grappling_hook", logger));
            this.aote = new AoteConfig(getSection(section, "aote", logger));
            this.enderbow = new EnderbowConfig(getSection(section, "enderbow", logger));
            this.customElytra = new CustomElytraConfig(getSection(section, "custom_elytra", logger));
        }

        private ConfigurationSection getSection(ConfigurationSection parent, String key, Logger logger) {
            ConfigurationSection section = parent.getConfigurationSection(key);
            if (section == null) {
                logger.warning("Configuration subsection '" + parent.getCurrentPath() + "." + key + "' is missing. Using default values.");
                return parent.createSection(key);
            }
            return section;
        }

        public static class TridentConfig {
            public final boolean returnTrident;
            public final int cooldown;

            public TridentConfig(ConfigurationSection section) {
                this.returnTrident = section.getBoolean("return_trident", true);
                this.cooldown = section.getInt("cooldown", 5);
            }
        }

        public static class GrapplingHookConfig {
            public final double power;
            public final int cooldown;

            public GrapplingHookConfig(ConfigurationSection section) {
                this.power = section.getDouble("power", 1.8);
                this.cooldown = section.getInt("cooldown", 3);
            }
        }

        public static class AoteConfig {
            public final int distance;
            public final int cooldown;

            public AoteConfig(ConfigurationSection section) {
                this.distance = section.getInt("distance", 8);
                this.cooldown = section.getInt("cooldown", 2);
            }
        }

        public static class EnderbowConfig {
            public final int cooldown;

            public EnderbowConfig(ConfigurationSection section) {
                this.cooldown = section.getInt("cooldown", 3);
            }
        }

        public static class CustomElytraConfig {
            public final int cooldown;
            public final double speedBoost;

            public CustomElytraConfig(ConfigurationSection section) {
                this.cooldown = section.getInt("cooldown", 10);
                this.speedBoost = section.getDouble("speed_boost", 1.8);
            }
        }
    }

    public static class DoubleJumpConfig {
        public final boolean enabled;
        public final int cooldown;
        public final double power;
        public final SoundConfig sound;

        public DoubleJumpConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "DoubleJumpConfig section cannot be null.");
            this.enabled = section.getBoolean("enabled", true);
            this.cooldown = section.getInt("cooldown", 2);
            this.power = section.getDouble("power", 1.2);
            this.sound = new SoundConfig(section.getConfigurationSection("sound"));
        }
    }

    public static class MenuSoundsConfig {
        public final SoundConfig open;
        public final SoundConfig click;

        public MenuSoundsConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "MenuSoundsConfig section cannot be null.");
            this.open = new SoundConfig(section.getConfigurationSection("open"));
            this.click = new SoundConfig(section.getConfigurationSection("click"));
        }
    }

    public static class SoundConfig {
        public final boolean enabled;
        public final String name;
        public final float volume;
        public final float pitch;

        public SoundConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "SoundConfig section cannot be null.");
            this.enabled = section.getBoolean("enabled", true);
            this.name = section.getString("name", "ui.button.click");
            this.volume = (float) section.getDouble("volume", 1.0);
            this.pitch = (float) section.getDouble("pitch", 1.0);
        }
    }

    public static class AnnouncementsConfig {
        public final boolean enabled;
        public final int intervalSeconds;
        public final boolean randomized;
        public final List<AnnouncementConfig> messages;

        public AnnouncementsConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "AnnouncementsConfig section cannot be null.");
            this.enabled = section.getBoolean("enabled", true);
            this.intervalSeconds = section.getInt("interval_seconds", 90);
            this.randomized = section.getBoolean("randomized", false);
            this.messages = section.getMapList("messages").stream()
                                  .map(AnnouncementConfig::new)
                                  .collect(Collectors.toList());
        }
    }

    public static class AnnouncementConfig {
        public final String message;
        public final String type;
        public final List<String> worlds;
        // For TITLE type
        public final String title;
        public final String subtitle;
        public final int fadeIn;
        public final int stay;
        public final int fadeOut;
        // For BOSS_BAR type
        public final String bossBarColor;
        public final String bossBarStyle;
        public final int bossBarDuration;


        public AnnouncementConfig(Map<?, ?> map) {
            this.message = get(map, "message", "");
            this.type = get(map, "type", "CHAT");
            this.worlds = getList(map, "worlds");

            // Title properties
            this.title = get(map, "title", "");
            this.subtitle = get(map, "subtitle", "");
            this.fadeIn = get(map, "fadeIn", 10);
            this.stay = get(map, "stay", 70);
            this.fadeOut = get(map, "fadeOut", 20);

            // BossBar properties
            this.bossBarColor = get(map, "bossBarColor", "WHITE");
            this.bossBarStyle = get(map, "bossBarStyle", "SOLID");
            this.bossBarDuration = get(map, "bossBarDuration", 10);
        }

        // --- Type-safe helper methods for parsing the map ---

        private <T> T get(Map<?, ?> map, String key, T def) {
            Object val = map.get(key);
            if (val == null) return def;
            try {
                // This is an unchecked cast, but we trust the config structure.
                // The try-catch will handle cases where the type is wrong.
                return (T) def.getClass().cast(val);
            } catch (ClassCastException e) {
                return def;
            }
        }

        private List<String> getList(Map<?, ?> map, String key) {
            Object val = map.get(key);
            if (val instanceof List) {
                return ((List<?>) val).stream()
                                      .map(String::valueOf)
                                      .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }


    public static class BossBarConfig {
        public final boolean showOnJoin;
        public final String title;
        public final String color;
        public final String style;
        public final int duration;

        public BossBarConfig(ConfigurationSection section) {
            Objects.requireNonNull(section, "BossBarConfig section cannot be null.");
            this.showOnJoin = section.getBoolean("show_on_join", true);
            this.title = section.getString("title", "<gradient:#5e4fa2:#f79459>Welcome, %player_name%!</gradient>");
            this.color = section.getString("color", "WHITE");
            this.style = section.getString("style", "SOLID");
            this.duration = section.getInt("duration", 10);
        }
    }
}