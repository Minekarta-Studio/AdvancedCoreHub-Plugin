package com.minekarta.advancedcorehub.cosmetics;

import org.bukkit.Particle;

public class ParticleTrail {

    private final String id;
    private final String name;
    private final Particle particle;
    private final String permission;

    public ParticleTrail(String id, String name, Particle particle, String permission) {
        this.id = id;
        this.name = name;
        this.particle = particle;
        this.permission = permission;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Particle getParticle() {
        return particle;
    }

    public String getPermission() {
        return permission;
    }
}
