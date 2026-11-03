package com.suschunkfinder;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;

/** Small sparkle particles near the ground around the player. */
public final class WorldSparklesModule extends ClientModule {
    private final NumberSetting amount = add(NumberSetting.of("Sparkles per tick", 1, 1, 4, 1));
    private final NumberSetting radius = add(NumberSetting.of("Radius", 3.0, 1.0, 8.0, 0.5));

    public WorldSparklesModule() {
        super("World Sparkles", "Adds subtle enchantment sparkles around the player.", Category.RENDER);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < amount.getInt(); i++) {
            double x = client.player.getX() + random.nextDouble(-radius.get(), radius.get());
            double y = client.player.getY() + random.nextDouble(0.05, 0.8);
            double z = client.player.getZ() + random.nextDouble(-radius.get(), radius.get());
            client.level.addParticle(ParticleTypes.ENCHANT, x, y, z, 0.0, 0.025, 0.0);
        }
    }
}
