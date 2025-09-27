package com.minekarta.advancedcorehub.cosmetics;

import org.bukkit.Particle;

/**
 * Represents a cosmetic particle trail configuration.
 * Using a record simplifies this immutable data carrier class.
 *
 * @param id         The unique identifier for the trail (e.g., "flame").
 * @param name       The display name for the trail, supporting color codes.
 * @param particle   The Bukkit Particle type to be spawned. Can be null for "none".
 * @param permission The permission required to use this trail.
 */
public record ParticleTrail(String id, String name, Particle particle, String permission) {
}