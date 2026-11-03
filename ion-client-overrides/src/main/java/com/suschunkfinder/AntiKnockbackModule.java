package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Scales local player velocity down during the vanilla hurt animation. */
public final class AntiKnockbackModule extends ClientModule {
    private final NumberSetting horizontal = add(NumberSetting.of("Horizontal resistance %", 100, 0, 100, 5));
    private final NumberSetting vertical = add(NumberSetting.of("Vertical resistance %", 100, 0, 100, 5));

    public AntiKnockbackModule() {
        super("Anti Knockback", "Reduces local velocity during the hurt window; no packet spoofing or anti-cheat bypass.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.player.hurtTime <= 0) return;
        Vec3 velocity = client.player.getDeltaMovement();
        double h = horizontal.get() / 100.0;
        double v = vertical.get() / 100.0;
        client.player.setDeltaMovement(velocity.x * h, velocity.y * v, velocity.z * h);
    }
}
