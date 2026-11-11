package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Applies a bounded horizontal movement multiplier while walking on the ground. */
public final class SpeedModule extends ClientModule {
    private final NumberSetting multiplier = add(NumberSetting.of("Multiplier", 1.15, 1.05, 1.5, 0.05));
    private final NumberSetting maxSpeed = add(NumberSetting.of("Max horizontal speed", 0.28, 0.15, 0.45, 0.01));

    public SpeedModule() {
        super("Speed", "Adds a bounded horizontal movement boost while grounded.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null || !client.player.onGround()) return;
        if (!client.options.keyUp.isDown() && !client.options.keyDown.isDown()
                && !client.options.keyLeft.isDown() && !client.options.keyRight.isDown()) return;
        Vec3 velocity = client.player.getDeltaMovement();
        double current = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (current < 0.001) return;
        double target = Math.min(current * multiplier.get(), maxSpeed.get());
        double scale = target / current;
        client.player.setDeltaMovement(velocity.x * scale, velocity.y, velocity.z * scale);
    }
}
