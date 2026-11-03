package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

/** Leaves a particle trail along the local player's recent path. */
public final class PlayerTrailsModule extends ClientModule {
    private final NumberSetting spacing = add(NumberSetting.of("Trail spacing", 0.18, 0.05, 0.5, 0.01));
    private Vec3 last;

    public PlayerTrailsModule() {
        super("Player Trails", "Leaves a light particle trail as you move.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        Vec3 current = client.player.position().add(0.0, 0.15, 0.0);
        if (last != null && current.distanceToSqr(last) >= spacing.get() * spacing.get()) {
            client.level.addParticle(ParticleTypes.END_ROD, last.x, last.y, last.z, 0.0, 0.01, 0.0);
            last = current;
        } else if (last == null) {
            last = current;
        }
    }

    @Override
    protected void onDisable() { last = null; }
}
