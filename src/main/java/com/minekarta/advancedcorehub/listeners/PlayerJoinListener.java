package com.minekarta.advancedcorehub.listeners;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.Location;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private final AdvancedCoreHub plugin;

    public PlayerJoinListener(AdvancedCoreHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 1. Teleport to spawn
        if (plugin.getConfig().getBoolean("spawn-on-join.enabled", true)) {
            ConfigurationSection spawnConfig = plugin.getConfig().getConfigurationSection("spawn");
            if (spawnConfig != null) {
                String worldName = spawnConfig.getString("world");
                World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    double x = spawnConfig.getDouble("x");
                    double y = spawnConfig.getDouble("y");
                    double z = spawnConfig.getDouble("z");
                    float yaw = (float) spawnConfig.getDouble("yaw");
                    float pitch = (float) spawnConfig.getDouble("pitch");
                    player.teleport(new Location(world, x, y, z, yaw, pitch));
                } else {
                    plugin.getLogger().warning("Spawn world '" + worldName + "' not found!");
                }
            }
        }

        // 2. Handle Join Message (can be customized or disabled)
        if (!plugin.getVanishManager().isVanished(player)) {
            event.joinMessage(plugin.getLocaleManager().getComponent("join-message", player));
        } else {
            event.joinMessage(null);
        }

        // Handle vanish visibility for the joining player
        plugin.getVanishManager().handlePlayerJoin(player);

        // 2. Execute actions_on_join from config.yml
        List<java.util.Map<?, ?>> joinActions = plugin.getConfig().getMapList("actions_on_join");
        if (!joinActions.isEmpty()) {
            plugin.getActionManager().executeMapActions(player, joinActions);
        }

        // 3. Handle Hub Inventory and Join Items
        if (plugin.getInventoryManager().isHubWorld(player.getWorld().getName())) {
            // Player is in a hub world, run the full setup
            if (plugin.getInventoryManager().isSaveAndRestoreEnabled()) {
                plugin.getInventoryManager().savePlayerInventory(player);
            }
            plugin.getInventoryManager().setupHubInventory(player); // This also gives items

            // Handle double jump flight
            if (plugin.getConfig().getBoolean("movement_features.double_jump.enabled", true)) {
                if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                    player.setAllowFlight(true);
                }
            }
        } else {
            // Player is not in a hub world, just give them any applicable join items
            // without clearing or saving their inventory.
            plugin.getInventoryManager().giveJoinItems(player);
        }

        // 4. Handle Boss Bar on join
        if (plugin.getConfig().getBoolean("bossbar.show_on_join", false)) {
            ConfigurationSection bossBarConfig = plugin.getConfig().getConfigurationSection("bossbar");
            if (bossBarConfig != null) {
                String title = bossBarConfig.getString("title", "<red>Welcome!</red>");
                int duration = bossBarConfig.getInt("duration", 10);

                BarColor color;
                try {
                    color = BarColor.valueOf(bossBarConfig.getString("color", "WHITE").toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid Boss Bar color in config.yml. Defaulting to WHITE.");
                    color = BarColor.WHITE;
                }

                BarStyle style;
                try {
                    style = BarStyle.valueOf(bossBarConfig.getString("style", "SOLID").toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid Boss Bar style in config.yml. Defaulting to SOLID.");
                    style = BarStyle.SOLID;
                }

                plugin.getBossBarManager().createBossBar(player, title, color, style, duration);
            }
        }

        // 5. Handle Join Firework
        ConfigurationSection fireworkConfig = plugin.getConfig().getConfigurationSection("cosmetics.join_firework");
        if (fireworkConfig != null && fireworkConfig.getBoolean("enabled", true)) {
            if (plugin.getHubWorldManager().isHubWorld(player.getWorld().getName())) {
                spawnFirework(player, fireworkConfig);
            }
        }

        // 6. Create Scoreboard
        plugin.getScoreboardManager().createBoard(player);

        // 7. Check for persistent timed flight
        if (player.getPersistentDataContainer().has(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION, org.bukkit.persistence.PersistentDataType.LONG)) {
            long expirationTime = player.getPersistentDataContainer().get(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION, org.bukkit.persistence.PersistentDataType.LONG);
            long currentTime = System.currentTimeMillis();

            if (currentTime >= expirationTime) {
                // Flight has expired while offline
                player.setAllowFlight(false);
                player.setFlying(false);
                player.getPersistentDataContainer().remove(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION);
            } else {
                // Flight is still active, restart the timer
                long remainingTicks = (expirationTime - currentTime) / 50;
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline() && player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.getPersistentDataContainer().remove(com.minekarta.advancedcorehub.util.PersistentKeys.FLY_EXPIRATION);
                        plugin.getLocaleManager().sendMessage(player, "fly-expired");
                    }
                }, remainingTicks);
            }
        }
    }

    private void spawnFirework(Player player, ConfigurationSection config) {
        Location loc = player.getLocation();
        Firework fw = player.getWorld().spawn(loc, Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();

        try {
            // Set type
            FireworkEffect.Type type = FireworkEffect.Type.valueOf(config.getString("type", "STAR"));

            // Build colors
            FireworkEffect.Builder builder = FireworkEffect.builder().with(type);
            for (String colorStr : config.getStringList("colors")) {
                builder.withColor(parseColor(colorStr));
            }

            // Build fade colors
            for (String colorStr : config.getStringList("fade_colors")) {
                builder.withFade(parseColor(colorStr));
            }

            // Set flicker and trail
            if (config.getBoolean("flicker", false)) {
                builder.withFlicker();
            }
            if (config.getBoolean("trail", false)) {
                builder.withTrail();
            }

            fwm.addEffect(builder.build());
            fwm.setPower(config.getInt("power", 0));
            fw.setFireworkMeta(fwm);

        } catch (Exception e) {
            plugin.getLogger().warning("Could not spawn join firework due to an error in the configuration.");
            e.printStackTrace();
            fw.detonate(); // detonate immediately to remove the entity
        }
    }

    private Color parseColor(String str) {
        String[] rgb = str.split(",");
        int r = Integer.parseInt(rgb[0].trim());
        int g = Integer.parseInt(rgb[1].trim());
        int b = Integer.parseInt(rgb[2].trim());
        return Color.fromRGB(r, g, b);
    }
}
