package com.minekarta.advancedcorehub.cosmetics;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CosmeticsManager {

    private final AdvancedCoreHub plugin;
    private final Map<UUID, ParticleTrail> activeTrails = new ConcurrentHashMap<>();
    private final Map<String, ParticleTrail> availableTrails = new ConcurrentHashMap<>();

    public CosmeticsManager(AdvancedCoreHub plugin) {
        this.plugin = plugin;
        loadCosmetics();
    }

    public void loadCosmetics() {
        availableTrails.clear();
        ConfigurationSection trailsSection = plugin.getFileManager().getConfig("cosmetics.yml").getConfigurationSection("particle-trails");
        if (trailsSection == null) {
            plugin.getLogger().warning("No 'particle-trails' section found in cosmetics.yml.");
            return;
        }

        for (String id : trailsSection.getKeys(false)) {
            try {
                String name = trailsSection.getString(id + ".name");
                String particleName = trailsSection.getString(id + ".particle");
                String permission = trailsSection.getString(id + ".permission");

                if (id.equalsIgnoreCase("none")) {
                    availableTrails.put(id, new ParticleTrail(id, name, null, permission));
                    continue;
                }

                Particle particle = particleName != null && !particleName.isEmpty() ? Particle.valueOf(particleName.toUpperCase()) : null;
                availableTrails.put(id, new ParticleTrail(id, name, particle, permission));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().severe("Invalid particle name in cosmetics.yml for trail '" + id + "': " + trailsSection.getString(id + ".particle"));
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load particle trail: " + id, e);
            }
        }
        plugin.getLogger().info("Loaded " + availableTrails.size() + " particle trails.");

        // After reloading, remove any active trails that no longer exist.
        activeTrails.entrySet().removeIf(entry -> !availableTrails.containsKey(entry.getValue().id()));
    }

    public ParticleTrail getTrail(String id) {
        return availableTrails.get(id);
    }

    public Map<String, ParticleTrail> getAvailableTrails() {
        return Map.copyOf(availableTrails);
    }

    public void setActiveTrail(Player player, ParticleTrail trail) {
        // The "none" trail is represented by a trail object with a null particle.
        if (trail == null || trail.particle() == null) {
            activeTrails.remove(player.getUniqueId());
        } else {
            activeTrails.put(player.getUniqueId(), trail);
        }
    }

    public ParticleTrail getActiveTrail(Player player) {
        return activeTrails.get(player.getUniqueId());
    }
}
