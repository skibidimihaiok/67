package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Leaves a short particle trail behind the living entity under the crosshair. */
public final class TargetTrailsModule extends ClientModule {
    private final NumberSetting points = add(NumberSetting.of("Trail points", 3, 1, 8, 1));
    private Vec3 previous;

    public TargetTrailsModule() {
        super("Target Trails", "Leaves a subtle trail behind your current crosshair target.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null
                || !(client.crosshairPickEntity instanceof LivingEntity target) || !target.isAlive()) {
            previous = null;
            return;
        }
        Vec3 current = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ());
        if (previous != null) {
            for (int i = 1; i <= points.getInt(); i++) {
                double t = (double) i / (points.getInt() + 1);
                client.level.addParticle(ParticleTypes.END_ROD,
                        previous.x + (current.x - previous.x) * t,
                        previous.y + (current.y - previous.y) * t,
                        previous.z + (current.z - previous.z) * t,
                        0.0, 0.005, 0.0);
            }
        }
        previous = current;
    }

    @Override
    protected void onDisable() { previous = null; }
}
