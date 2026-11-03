package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Uses a held mace against the player directly under the crosshair while gliding. */
public final class ElytraMaceTargetModule extends ClientModule {
    private final NumberSetting range = add(NumberSetting.of("Range", 4.0, 1.0, 6.0, 0.5));
    private int wait;

    public ElytraMaceTargetModule() {
        super("Elytra Mace Target", "Attacks only the crosshair player with a held mace while gliding.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (!client.player.isFallFlying()) { wait = 0; return; }
        if (wait > 0) { wait--; return; }
        if (!client.player.getMainHandItem().is(Items.MACE)) return;
        if (!(client.crosshairPickEntity instanceof Player target) || !target.isAlive()
                || client.player.distanceTo(target) > range.get()) return;
        if (client.player.getAttackStrengthScale(0.5F) < 1.0F) return;
        client.gameMode.attack(client.player, target);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        wait = 5;
    }

    @Override
    protected void onDisable() { wait = 0; }
}
