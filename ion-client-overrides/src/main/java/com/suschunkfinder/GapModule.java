package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

/** Uses a golden apple from the hotbar when health falls below a chosen threshold. */
public final class GapModule extends ClientModule {
    private final NumberSetting health = add(NumberSetting.of("Health threshold", 12.0, 4.0, 20.0, 1.0));
    private final NumberSetting cooldown = add(NumberSetting.of("Use cooldown (ticks)", 20, 5, 80, 1));
    private int wait;
    private int restoreSlot = -1;

    public GapModule() {
        super("Gap", "Uses a hotbar golden apple when your health is low.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (restoreSlot >= 0) {
            client.player.getInventory().selected = restoreSlot;
            restoreSlot = -1;
        }
        if (wait > 0) { wait--; return; }
        if (client.player.getHealth() > health.get()) return;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getItem(i).is(Items.GOLDEN_APPLE)
                    || client.player.getInventory().getItem(i).is(Items.ENCHANTED_GOLDEN_APPLE)) {
                slot = i;
                break;
            }
        }
        if (slot < 0) return;
        restoreSlot = client.player.getInventory().selected;
        client.player.getInventory().selected = slot;
        client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
        wait = cooldown.getInt();
    }

    @Override
    protected void onDisable() {
        wait = 0;
        restoreSlot = -1;
    }
}
