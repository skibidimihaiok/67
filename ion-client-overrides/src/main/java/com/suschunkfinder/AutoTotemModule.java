package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ClickType;

/** Moves a carried totem into the offhand when the offhand is empty. */
public final class AutoTotemModule extends ClientModule {
    private final NumberSetting delay = add(NumberSetting.of("Check delay (ticks)", 5, 1, 20, 1));
    private int wait;

    public AutoTotemModule() {
        super("Auto Totem", "Keeps a totem in your offhand, swapping the previous item back into inventory.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        ItemStack offhand = client.player.getOffhandItem();
        if (offhand.is(Items.TOTEM_OF_UNDYING)) return;
        Inventory inventory = client.player.getInventory();
        int inventorySlot = inventory.findSlotMatchingItem(new ItemStack(Items.TOTEM_OF_UNDYING));
        if (inventorySlot < 0 || inventorySlot > 35) return;
        int menuSlot = inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
        int container = client.player.inventoryMenu.containerId;
        client.gameMode.handleInventoryMouseClick(container, menuSlot, 0, ClickType.PICKUP, client.player);
        client.gameMode.handleInventoryMouseClick(container, 45, 0, ClickType.PICKUP, client.player);
        client.gameMode.handleInventoryMouseClick(container, menuSlot, 0, ClickType.PICKUP, client.player);
        wait = delay.getInt();
    }

    @Override
    protected void onDisable() { wait = 0; }
}
