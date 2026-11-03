package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;

/** Highlights only the living entity currently under the crosshair. */
public final class TargetHighlightModule extends ClientModule {
    private final NumberSetting particles = add(NumberSetting.of("Particles", 2, 1, 6, 1));
    private final NumberSetting height = add(NumberSetting.of("Height offset", 0.5, 0.1, 1.5, 0.1));

    public TargetHighlightModule() {
        super("Target Highlight", "Adds a subtle particle marker to the crosshair target.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null) return;
        if (!(client.crosshairPickEntity instanceof LivingEntity target) || !target.isAlive()) return;
        double x = target.getX();
        double y = target.getY() + height.get();
        double z = target.getZ();
        for (int i = 0; i < particles.getInt(); i++) {
            double angle = (System.currentTimeMillis() / 180.0) + (Math.PI * 2.0 * i / particles.getInt());
            client.level.addParticle(ParticleTypes.END_ROD,
                    x + Math.cos(angle) * target.getBbWidth() * 0.65, y,
                    z + Math.sin(angle) * target.getBbWidth() * 0.65, 0.0, 0.01, 0.0);
        }
    }
}
