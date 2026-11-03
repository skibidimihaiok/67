package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

/** Automatically fills empty armor slots from the player's inventory. */
public final class AutoArmorModule extends ClientModule {
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 8, 1, 40, 1));
    private int wait;

    public AutoArmorModule() {
        super("Auto Armor", "Equips armor from your inventory when the matching armor slot is empty.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        var inventory = client.player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!(stack.getItem() instanceof ArmorItem armor)) continue;
            if (!client.player.getItemBySlot(armor.getEquipmentSlot()).isEmpty()) continue;
            int menuSlot = i < 9 ? i + 36 : i;
            client.gameMode.handleInventoryMouseClick(
                    client.player.inventoryMenu.containerId, menuSlot, 0,
                    ClickType.QUICK_MOVE, client.player);
            wait = delay.getInt();
            return;
        }
    }

    @Override
    protected void onDisable() { wait = 0; }
}
