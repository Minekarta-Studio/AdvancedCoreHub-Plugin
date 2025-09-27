package com.minekarta.advancedcorehub.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;

public class PluginConfig {

    // Nested configuration sections
    public final SpawnConfig spawn;
    public final MessagesConfig messages;
    public final WorldSettingsConfig worldSettings;
    public final ChatProtectionConfig chatProtection;
    public final AntiWorldDownloaderConfig antiWorldDownloader;
    public final InventoryManagementConfig inventoryManagement;
    public final ServerSelectorConfig serverSelector;
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
        // Load top-level settings
        this.language = source.getString("language", "en");
        this.hubWorlds = source.getStringList("hub-worlds");
        this.actionsOnJoin = source.getMapList("actions_on_join");
        this.customActions = source.getConfigurationSection("custom-actions").getValues(false);

        // Load nested sections
        this.spawn = new SpawnConfig(source.getConfigurationSection("spawn"));
        this.messages = new MessagesConfig(source.getConfigurationSection("messages"));
        this.worldSettings = new WorldSettingsConfig(source.getConfigurationSection("world_settings"));
        this.chatProtection = new ChatProtectionConfig(source.getConfigurationSection("chat_protection"));
        this.antiWorldDownloader = new AntiWorldDownloaderConfig(source.getConfigurationSection("anti_world_downloader"));
        this.inventoryManagement = new InventoryManagementConfig(source.getConfigurationSection("inventory_management"));
        this.serverSelector = new ServerSelectorConfig(source.getConfigurationSection("server-selector"));
        this.movementItems = new MovementItemsConfig(source.getConfigurationSection("movement_items"));
        this.doubleJump = new DoubleJumpConfig(source.getConfigurationSection("double_jump"));
        this.menuSounds = new MenuSoundsConfig(source.getConfigurationSection("menu_sounds"));
        this.announcements = new AnnouncementsConfig(source.getConfigurationSection("announcements"));
        this.bossBar = new BossBarConfig(source.getConfigurationSection("bossbar"));
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
            this.antiSwear = new AntiSwearConfig(section.getConfigurationSection("anti_swear"));
            this.commandBlocker = new CommandBlockerConfig(section.getConfigurationSection("command_blocker"));
        }

        public static class AntiSwearConfig {
            public final boolean enabled;
            public final List<String> blockedWords;

            public AntiSwearConfig(ConfigurationSection section) {
                this.enabled = section.getBoolean("enabled", false);
                this.blockedWords = section.getStringList("blocked_words");
            }
        }

        public static class CommandBlockerConfig {
            public final boolean enabled;
            public final List<String> blockedCommands;

            public CommandBlockerConfig(ConfigurationSection section) {
                this.enabled = section.getBoolean("enabled", false);
                this.blockedCommands = section.getStringList("blocked_commands");
            }
        }
    }

    public static class AntiWorldDownloaderConfig {
        public final boolean enabled;

        public AntiWorldDownloaderConfig(ConfigurationSection section) {
            this.enabled = section.getBoolean("enabled", true);
        }
    }

    public static class InventoryManagementConfig {
        public final boolean enable;
        public final boolean saveAndRestore;
        public final boolean clearOnEnter;

        public InventoryManagementConfig(ConfigurationSection section) {
            this.enable = section.getBoolean("enable", true);
            this.saveAndRestore = section.getBoolean("save-and-restore", true);
            this.clearOnEnter = section.getBoolean("clear-on-enter", true);
        }
    }

    public static class ServerSelectorConfig {
        public final List<String> servers;

        public ServerSelectorConfig(ConfigurationSection section) {
            this.servers = section.getStringList("servers");
        }
    }

    public static class MovementItemsConfig {
        public final TridentConfig trident;
        public final GrapplingHookConfig grapplingHook;
        public final AoteConfig aote;
        public final EnderbowConfig enderbow;
        public final CustomElytraConfig customElytra;

        public MovementItemsConfig(ConfigurationSection section) {
            this.trident = new TridentConfig(section.getConfigurationSection("trident"));
            this.grapplingHook = new GrapplingHookConfig(section.getConfigurationSection("grappling_hook"));
            this.aote = new AoteConfig(section.getConfigurationSection("aote"));
            this.enderbow = new EnderbowConfig(section.getConfigurationSection("enderbow"));
            this.customElytra = new CustomElytraConfig(section.getConfigurationSection("custom_elytra"));
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
            this.enabled = section.getBoolean("enabled", true);
            this.intervalSeconds = section.getInt("interval_seconds", 90);
            this.randomized = section.getBoolean("randomized", false);
            this.messages = section.getMapList("messages").stream()
                                  .map(AnnouncementConfig::new)
                                  .collect(java.util.stream.Collectors.toList());
        }
    }

    public static class BossBarConfig {
        public final boolean showOnJoin;
        public final String title;
        public final String color;
        public final String style;
        public final int duration;

        public BossBarConfig(ConfigurationSection section) {
            this.showOnJoin = section.getBoolean("show_on_join", true);
            this.title = section.getString("title", "<gradient:#5e4fa2:#f79459>Welcome, %player_name%!</gradient>");
            this.color = section.getString("color", "WHITE");
            this.style = section.getString("style", "SOLID");
            this.duration = section.getInt("duration", 10);
        }
    }
}