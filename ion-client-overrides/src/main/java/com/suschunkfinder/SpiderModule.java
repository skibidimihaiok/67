package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Adds a gentle upward nudge while pressing into a wall. */
public final class SpiderModule extends ClientModule {
    private final NumberSetting climbSpeed = add(NumberSetting.of("Climb speed", 0.18, 0.05, 0.35, 0.01));

    public SpiderModule() {
        super("Spider", "Lets you climb by moving into a wall.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null || client.player.onGround()) return;
        if (!client.player.horizontalCollision) return;
        Vec3 velocity = client.player.getDeltaMovement();
        client.player.setDeltaMovement(velocity.x, Math.max(velocity.y, climbSpeed.get()), velocity.z);
    }
}
