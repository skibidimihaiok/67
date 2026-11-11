package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

/** Equips better armor from inventory and swaps the old piece back into its original slot. */
public final class AutoArmorModule extends ClientModule {
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 8, 1, 40, 1));
    private int wait;

    public AutoArmorModule() {
        super("Auto Armor", "Equips higher-tier armor from inventory and preserves the displaced armor.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        var inventory = client.player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack candidate = inventory.getItem(i);
            if (candidate.isEmpty()) continue;
            String name = candidate.getHoverName().getString().toLowerCase(java.util.Locale.ROOT);
            int armorSlot = armorMenuSlot(name);
            if (armorSlot < 0) continue;
            ItemStack worn = client.player.inventoryMenu.getSlot(armorSlot).getItem();
            if (tier(name) <= tier(worn.getHoverName().getString().toLowerCase(java.util.Locale.ROOT))) continue;
            int inventorySlot = i < 9 ? i + 36 : i;
            int container = client.player.inventoryMenu.containerId;
            client.gameMode.handleInventoryMouseClick(container, inventorySlot, 0, ClickType.PICKUP, client.player);
            client.gameMode.handleInventoryMouseClick(container, armorSlot, 0, ClickType.PICKUP, client.player);
            client.gameMode.handleInventoryMouseClick(container, inventorySlot, 0, ClickType.PICKUP, client.player);
            wait = delay.getInt();
            return;
        }
    }

    private static int armorMenuSlot(String name) {
        if (name.contains("helmet")) return 5;
        if (name.contains("chestplate")) return 6;
        if (name.contains("leggings")) return 7;
        if (name.contains("boots")) return 8;
        return -1;
    }

    private static int tier(String name) {
        if (name.contains("netherite")) return 6;
        if (name.contains("diamond")) return 5;
        if (name.contains("iron")) return 4;
        if (name.contains("golden")) return 3;
        if (name.contains("chainmail")) return 2;
        if (name.contains("leather")) return 1;
        return 0;
    }

    @Override
    protected void onDisable() { wait = 0; }
}
