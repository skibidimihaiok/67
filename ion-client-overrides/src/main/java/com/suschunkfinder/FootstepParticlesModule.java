package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

/** Emits small particles at the player's feet while walking. */
public final class FootstepParticlesModule extends ClientModule {
    private final NumberSetting interval = add(NumberSetting.of("Step interval", 5, 2, 12, 1));
    private int ticks;
    private Vec3 lastPosition;

    public FootstepParticlesModule() {
        super("Footstep Particles", "Emits a small particle puff as you walk.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null) return;
        Vec3 position = client.player.position();
        if (!client.player.onGround() || (lastPosition != null && position.distanceToSqr(lastPosition) < 0.0004)) {
            lastPosition = position;
            return;
        }
        lastPosition = position;
        if (++ticks < interval.getInt()) return;
        ticks = 0;
        client.level.addParticle(ParticleTypes.CLOUD, position.x, position.y + 0.05, position.z, 0.0, 0.01, 0.0);
    }

    @Override
    protected void onDisable() {
        ticks = 0;
        lastPosition = null;
    }
}
