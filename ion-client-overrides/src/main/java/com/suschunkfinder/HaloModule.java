package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

/** Draws a floating particle halo over the local player's head. */
public final class HaloModule extends ClientModule {
    private final NumberSetting radius = add(NumberSetting.of("Radius", 0.45, 0.2, 1.0, 0.05));
    private final NumberSetting height = add(NumberSetting.of("Height", 2.0, 1.5, 3.0, 0.1));
    private final NumberSetting count = add(NumberSetting.of("Particles", 10, 4, 20, 1));

    public HaloModule() {
        super("Halo", "Creates a floating ring of particles above your head.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        double phase = System.currentTimeMillis() * 0.003;
        for (int i = 0; i < count.getInt(); i++) {
            double angle = phase + Math.PI * 2.0 * i / count.getInt();
            client.level.addParticle(ParticleTypes.END_ROD,
                    client.player.getX() + Math.cos(angle) * radius.get(),
                    client.player.getY() + height.get(),
                    client.player.getZ() + Math.sin(angle) * radius.get(),
                    0.0, 0.0, 0.0);
        }
    }
}
