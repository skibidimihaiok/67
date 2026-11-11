package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Emergency totem swap that activates only below a configured health threshold. */
public final class TotemModule extends ClientModule {
    private final NumberSetting health = add(NumberSetting.of("Health threshold", 8.0, 2.0, 20.0, 1.0));
    private final NumberSetting delay = add(NumberSetting.of("Check delay (ticks)", 5, 1, 20, 1));
    private int wait;

    public TotemModule() {
        super("Totem", "Moves a totem to the offhand only when health falls below the configured threshold.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        if (client.player.getHealth() > health.get()
                || client.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;
        int slot = client.player.getInventory().findSlotMatchingItem(new ItemStack(Items.TOTEM_OF_UNDYING));
        if (slot < 0 || slot > 35) return;
        int menuSlot = slot < 9 ? slot + 36 : slot;
        int container = client.player.inventoryMenu.containerId;
        client.gameMode.handleInventoryMouseClick(container, menuSlot, 0, ClickType.PICKUP, client.player);
        client.gameMode.handleInventoryMouseClick(container, 45, 0, ClickType.PICKUP, client.player);
        client.gameMode.handleInventoryMouseClick(container, menuSlot, 0, ClickType.PICKUP, client.player);
        wait = delay.getInt();
    }

    @Override
    protected void onDisable() { wait = 0; }
}
