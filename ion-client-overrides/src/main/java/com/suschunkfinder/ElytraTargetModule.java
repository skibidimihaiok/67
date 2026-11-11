package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

/** Attacks only the crosshair target while the local player is gliding. */
public final class ElytraTargetModule extends ClientModule {
    private final NumberSetting range = add(NumberSetting.of("Range", 4.0, 1.0, 6.0, 0.5));
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 4, 0, 20, 1));
    private int wait;

    public ElytraTargetModule() {
        super("Elytra Target", "Attacks a living crosshair target only while you are elytra-gliding.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (!client.player.isFallFlying()) { wait = 0; return; }
        if (wait > 0) { wait--; return; }
        if (!(client.crosshairPickEntity instanceof LivingEntity target) || !target.isAlive()
                || client.player.distanceTo(target) > range.get()) return;
        if (client.player.getAttackStrengthScale(0.5F) < 1.0F) return;
        client.gameMode.attack(client.player, target);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        wait = delay.getInt();
    }

    @Override
    protected void onDisable() { wait = 0; }
}
