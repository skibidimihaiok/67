package com.suschunkfinder;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

/** Decorative particles around the local player. */
public final class AmbientParticlesModule extends ClientModule {
    private final NumberSetting amount = add(NumberSetting.of("Particles per tick", 1, 1, 5, 1));
    private final NumberSetting radius = add(NumberSetting.of("Radius", 1.5, 0.5, 4.0, 0.5));
    private final ModeSetting style = add(ModeSetting.of("Style", 0, "Enchant", "End Rod", "Witch", "Happy Villager"));

    public AmbientParticlesModule() {
        super("Ambient Particles", "Spawns a light decorative particle effect around you.", Category.RENDER);
    }

    private ParticleOptions particle() {
        return switch (style.index()) {
            case 1 -> ParticleTypes.END_ROD;
            case 2 -> ParticleTypes.WITCH;
            case 3 -> ParticleTypes.HAPPY_VILLAGER;
            default -> ParticleTypes.ENCHANT;
        };
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.options.hideGui) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < amount.getInt(); i++) {
            double x = client.player.getX() + random.nextDouble(-radius.get(), radius.get());
            double y = client.player.getY() + random.nextDouble(0.15, 1.8);
            double z = client.player.getZ() + random.nextDouble(-radius.get(), radius.get());
            client.level.addParticle(particle(), x, y, z, 0.0, 0.015, 0.0);
        }
    }
}
