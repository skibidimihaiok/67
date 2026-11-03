package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

/** Crosshair-only mace finisher; never scans for or rotates toward targets. */
public final class AutoMaceModule extends ClientModule {
    private final NumberSetting minFall = add(NumberSetting.of("Minimum fall distance", 1.5, 0.5, 8.0, 0.5));
    private final NumberSetting range = add(NumberSetting.of("Range", 4.0, 2.0, 6.0, 0.5));
    private final ModeSetting stunSlam = add(ModeSetting.of("Stun Slam", 0, "Off", "On"));
    private int wait;

    public AutoMaceModule() {
        super("Auto Mace", "Swaps to a hotbar mace and attacks only the player under your crosshair while falling.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        if (!(client.crosshairPickEntity instanceof Player target) || !target.isAlive()) return;
        if (client.player.distanceTo(target) > range.get()) return;
        if (client.player.onGround() || client.player.fallDistance < minFall.get()) return;
        int maceSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getItem(i).is(Items.MACE)) { maceSlot = i; break; }
        }
        if (maceSlot < 0) return;
        int previous = client.player.getInventory().selected;
        client.player.getInventory().selected = maceSlot;
        if (stunSlam.index() == 1 && target.isBlocking()) {
            int chargeSlot = -1;
            for (int i = 0; i < 9; i++) {
                if (client.player.getInventory().getItem(i).is(Items.WIND_CHARGE)) {
                    chargeSlot = i;
                    break;
                }
            }
            if (chargeSlot >= 0) {
                client.player.getInventory().selected = chargeSlot;
                client.gameMode.useItem(client.player, net.minecraft.world.InteractionHand.MAIN_HAND);
                client.player.getInventory().selected = maceSlot;
            }
        }
        if (client.player.getAttackStrengthScale(0.5F) >= 1.0F) {
            client.gameMode.attack(client.player, target);
            client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            wait = 10;
        }
        client.player.getInventory().selected = previous;
    }

    @Override
    protected void onDisable() { wait = 0; }
}
