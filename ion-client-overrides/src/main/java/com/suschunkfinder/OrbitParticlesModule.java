package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

/** Renders a configurable orbit of particles around the local player. */
public final class OrbitParticlesModule extends ClientModule {
    private final NumberSetting count = add(NumberSetting.of("Particle count", 8, 3, 20, 1));
    private final NumberSetting radius = add(NumberSetting.of("Orbit radius", 0.8, 0.3, 1.8, 0.1));
    private final NumberSetting speed = add(NumberSetting.of("Orbit speed", 1.0, 0.2, 3.0, 0.1));

    public OrbitParticlesModule() {
        super("Orbit Particles", "Creates a rotating ring of particles around you.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        double phase = System.currentTimeMillis() * 0.004 * speed.get();
        for (int i = 0; i < count.getInt(); i++) {
            double angle = phase + Math.PI * 2.0 * i / count.getInt();
            double x = client.player.getX() + Math.cos(angle) * radius.get();
            double y = client.player.getY() + 0.8 + Math.sin(angle * 2.0) * 0.25;
            double z = client.player.getZ() + Math.sin(angle) * radius.get();
            client.level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.01, 0.0);
        }
    }
}
