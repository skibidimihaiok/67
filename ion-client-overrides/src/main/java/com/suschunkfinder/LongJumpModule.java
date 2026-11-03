package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Adds a configurable forward boost to a normal jump. */
public final class LongJumpModule extends ClientModule {
    private final NumberSetting vertical = add(NumberSetting.of("Vertical power", 0.55, 0.42, 1.0, 0.05));
    private final NumberSetting horizontal = add(NumberSetting.of("Horizontal boost", 1.25, 1.0, 1.8, 0.05));
    private boolean wasJumpDown;

    public LongJumpModule() {
        super("Long Jump", "Adds configurable distance to a grounded jump.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null) return;
        boolean down = client.options.keyJump.isDown();
        if (down && !wasJumpDown && client.player.onGround()) {
            Vec3 v = client.player.getDeltaMovement();
            client.player.setDeltaMovement(v.x * horizontal.get(), vertical.get(), v.z * horizontal.get());
        }
        wasJumpDown = down;
    }

    @Override
    protected void onDisable() { wasJumpDown = false; }
}
