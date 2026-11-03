package com.suschunkfinder;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;

/** Adds extra crit particles when the local player makes a falling attack. */
public final class CriticalParticlesModule extends ClientModule {
    private final NumberSetting amount = add(NumberSetting.of("Amount", 8, 1, 24, 1));

    public CriticalParticlesModule() {
        super("Critical Particles", "Adds a burst of crit particles on falling attacks.", Category.RENDER);
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            Minecraft client = Minecraft.getInstance();
            if (isEnabled() && client.player == player && client.level == world
                    && !player.onGround() && player.fallDistance > 0.0F) {
                for (int i = 0; i < amount.getInt(); i++) {
                    world.addParticle(ParticleTypes.CRIT,
                            entity.getX() + (Math.random() - 0.5) * entity.getBbWidth(),
                            entity.getY() + Math.random() * entity.getBbHeight(),
                            entity.getZ() + (Math.random() - 0.5) * entity.getBbWidth(),
                            (Math.random() - 0.5) * 0.15, Math.random() * 0.12,
                            (Math.random() - 0.5) * 0.15);
                }
            }
            return InteractionResult.PASS;
        });
    }
}
