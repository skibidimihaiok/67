package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

/** Uses a healing splash potion from the hotbar when health is low. */
public final class AutoPotionModule extends ClientModule {
    private final NumberSetting health = add(NumberSetting.of("Health threshold", 10.0, 4.0, 20.0, 1.0));
    private final NumberSetting cooldown = add(NumberSetting.of("Cooldown (ticks)", 40, 10, 100, 5));
    private int wait;
    private int restoreSlot = -1;

    public AutoPotionModule() {
        super("Auto Potion", "Uses a hotbar splash potion named Healing or Regeneration when health is low.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (restoreSlot >= 0) {
            client.player.getInventory().setSelectedSlot(restoreSlot);
            restoreSlot = -1;
        }
        if (wait > 0) { wait--; return; }
        if (client.player.getHealth() > health.get()) return;
        for (int i = 0; i < 9; i++) {
            var stack = client.player.getInventory().getItem(i);
            if (!stack.getHoverName().getString().toLowerCase(java.util.Locale.ROOT)
                    .matches(".*(splash potion of healing|splash potion of regeneration|healing splash potion|regeneration splash potion).*")) continue;
            restoreSlot = client.player.getInventory().getSelectedSlot();
            client.player.getInventory().setSelectedSlot(i);
            client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
            wait = cooldown.getInt();
            return;
        }
    }

    @Override
    protected void onDisable() { wait = 0; restoreSlot = -1; }
}
