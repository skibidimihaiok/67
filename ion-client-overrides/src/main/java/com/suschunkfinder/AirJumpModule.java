package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Allows one extra jump while airborne for each time the player leaves the ground. */
public final class AirJumpModule extends ClientModule {
    private final NumberSetting power = add(NumberSetting.of("Jump power", 0.42, 0.2, 0.8, 0.02));
    private boolean wasJumpDown;
    private boolean used;
    private boolean wasGrounded;

    public AirJumpModule() {
        super("Air Jump", "Allows one additional mid-air jump per airtime.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null) return;
        if (client.player.onGround()) {
            used = false;
            wasGrounded = true;
        } else if (wasGrounded) {
            wasGrounded = false;
        }
        boolean down = client.options.keyJump.isDown();
        if (down && !wasJumpDown && !client.player.onGround() && !used && !client.player.isInWater()) {
            Vec3 velocity = client.player.getDeltaMovement();
            client.player.setDeltaMovement(velocity.x, power.get(), velocity.z);
            used = true;
        }
        wasJumpDown = down;
    }

    @Override
    protected void onDisable() {
        wasJumpDown = false;
        used = false;
        wasGrounded = false;
    }
}
