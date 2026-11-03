package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

/** Emits a small burst when the player starts a jump. */
public final class JumpEffectsModule extends ClientModule {
    private final NumberSetting amount = add(NumberSetting.of("Particle count", 6, 1, 16, 1));
    private boolean wasGrounded;

    public JumpEffectsModule() {
        super("Jump Effects", "Emits a configurable particle burst when you jump.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        boolean grounded = client.player.onGround();
        if (wasGrounded && !grounded && client.screen == null) {
            for (int i = 0; i < amount.getInt(); i++) {
                double angle = Math.PI * 2.0 * i / amount.getInt();
                client.level.addParticle(ParticleTypes.CRIT,
                        client.player.getX() + Math.cos(angle) * 0.25,
                        client.player.getY() + 0.08,
                        client.player.getZ() + Math.sin(angle) * 0.25,
                        Math.cos(angle) * 0.03, 0.02, Math.sin(angle) * 0.03);
            }
        }
        wasGrounded = grounded;
    }

    @Override
    protected void onDisable() { wasGrounded = false; }
}
