package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Keeps a selected utility item in the offhand, returning the displaced item to inventory. */
public final class OffhandModule extends ClientModule {
    private final ModeSetting item = add(ModeSetting.of("Offhand item", 0,
            "Totem", "Shield", "Golden Apple", "End Crystal"));
    private final NumberSetting delay = add(NumberSetting.of("Check delay (ticks)", 8, 1, 40, 1));
    private int wait;

    public OffhandModule() {
        super("Offhand", "Moves the selected utility item into your offhand.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        var wanted = switch (item.index()) {
            case 1 -> Items.SHIELD;
            case 2 -> Items.GOLDEN_APPLE;
            case 3 -> Items.END_CRYSTAL;
            default -> Items.TOTEM_OF_UNDYING;
        };
        if (client.player.getOffhandItem().is(wanted)) return;
        int slot = client.player.getInventory().findSlotMatchingItem(new ItemStack(wanted));
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
