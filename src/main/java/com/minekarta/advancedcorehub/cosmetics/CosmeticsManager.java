package com.minekarta.advancedcorehub.cosmetics;

import com.minekarta.advancedcorehub.AdvancedCoreHub;
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
        loadTrails();
    }

    public void loadTrails() {
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

                // Handle the 'none' case
                if (id.equalsIgnoreCase("none")) {
                    availableTrails.put(id, new ParticleTrail(id, name, null, permission));
                    continue;
                }

                Particle particle = Particle.valueOf(particleName.toUpperCase());
                ParticleTrail trail = new ParticleTrail(id, name, particle, permission);
                availableTrails.put(id, trail);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load particle trail: " + id, e);
            }
        }
    }

    public ParticleTrail getTrail(String id) {
        return availableTrails.get(id);
    }

    public Map<String, ParticleTrail> getAvailableTrails() {
        return availableTrails;
    }

    public void setActiveTrail(Player player, ParticleTrail trail) {
        if (trail == null) {
            activeTrails.remove(player.getUniqueId());
        } else {
            activeTrails.put(player.getUniqueId(), trail);
        }
    }

    public ParticleTrail getActiveTrail(Player player) {
        return activeTrails.get(player.getUniqueId());
    }
}
