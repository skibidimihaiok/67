package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

/** Manual pearl-clutch action: briefly aims upward, throws a hotbar pearl, then restores view. */
public final class AutoPearlCatchModule extends ClientModule {
    private final NumberSetting pitch = add(NumberSetting.of("Throw pitch", 85, 60, 90, 1));
    private final NumberSetting minimumFall = add(NumberSetting.of("Minimum fall distance", 2.0, 0.0, 20.0, 0.5));
    private boolean pending;
    private boolean restore;
    private float oldPitch;
    private int oldSlot;

    public AutoPearlCatchModule() {
        super("Auto Pearl Catch", "Manual action-bind pearl clutch: aims upward, throws a pearl, and restores your view.", Category.COMBAT);
    }

    @Override
    public void onBindAction() {
        pending = true;
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) {
            pending = false;
            restore = false;
            return;
        }
        if (restore) {
            client.player.setXRot(oldPitch);
            client.player.getInventory().selected = oldSlot;
            restore = false;
        }
        if (!pending) return;
        pending = false;
        if (client.player.fallDistance < minimumFall.get()) return;
        int pearlSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getItem(i).is(Items.ENDER_PEARL)) {
                pearlSlot = i;
                break;
            }
        }
        if (pearlSlot < 0) return;
        oldPitch = client.player.getXRot();
        oldSlot = client.player.getInventory().selected;
        client.player.getInventory().selected = pearlSlot;
        client.player.setXRot(-(float) pitch.get());
        client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
        restore = true;
    }

    @Override
    protected void onDisable() {
        pending = false;
        restore = false;
    }
}
