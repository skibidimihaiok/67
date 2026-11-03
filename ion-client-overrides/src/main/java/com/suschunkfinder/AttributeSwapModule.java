package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Items;

/** Action-bind swap between a chestplate and elytra using the vanilla inventory menu. */
public final class AttributeSwapModule extends ClientModule {
    private boolean pending;

    public AttributeSwapModule() {
        super("Attribute Swap", "Action bind swaps the chest slot between an elytra and a chestplate.", Category.COMBAT);
    }

    @Override
    public void onBindAction() { pending = true; }

    @Override
    public void tick(Minecraft client) {
        if (!pending || client.player == null || client.gameMode == null || client.screen != null) return;
        pending = false;
        var player = client.player;
        var inventory = player.getInventory();
        int container = player.inventoryMenu.containerId;
        var chest = player.getItemBySlot(EquipmentSlot.CHEST);
        String wanted = chest.is(Items.ELYTRA) ? "chestplate" : "elytra";
        if (!chest.isEmpty()) {
            client.gameMode.handleInventoryMouseClick(container, 6, 0, ClickType.QUICK_MOVE, player);
        }
        for (int i = 0; i < 36; i++) {
            var stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            String name = stack.getHoverName().getString().toLowerCase(java.util.Locale.ROOT);
            if ((wanted.equals("elytra") && stack.is(Items.ELYTRA))
                    || (wanted.equals("chestplate") && name.contains("chestplate"))) {
                int menuSlot = i < 9 ? i + 36 : i;
                client.gameMode.handleInventoryMouseClick(container, menuSlot, 0, ClickType.QUICK_MOVE, player);
                return;
            }
        }
    }

    @Override
    protected void onDisable() { pending = false; }
}
