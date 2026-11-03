package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

/** Emits a small cloud when the player lands on the ground. */
public final class LandingEffectsModule extends ClientModule {
    private final NumberSetting amount = add(NumberSetting.of("Particle count", 8, 1, 20, 1));
    private boolean wasGrounded;

    public LandingEffectsModule() {
        super("Landing Effects", "Emits a configurable puff when you land.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        boolean grounded = client.player.onGround();
        if (!wasGrounded && grounded && client.screen == null) {
            for (int i = 0; i < amount.getInt(); i++) {
                double angle = Math.PI * 2.0 * i / amount.getInt();
                client.level.addParticle(ParticleTypes.CLOUD,
                        client.player.getX() + Math.cos(angle) * 0.35,
                        client.player.getY() + 0.04,
                        client.player.getZ() + Math.sin(angle) * 0.35,
                        Math.cos(angle) * 0.025, 0.01, Math.sin(angle) * 0.025);
            }
        }
        wasGrounded = grounded;
    }

    @Override
    protected void onDisable() { wasGrounded = false; }
}
