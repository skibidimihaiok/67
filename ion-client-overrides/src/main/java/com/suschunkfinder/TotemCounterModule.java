package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Displays the number of totems carried by the player. */
public final class TotemCounterModule extends HudModule {
    public TotemCounterModule() {
        super("Totem Counter", "Shows how many totems are in your inventory and offhand.", Category.RENDER, 5, 270);
    }

    @Override
    protected void draw(GuiGraphics graphics, int x, int y) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = client.player.getInventory().getItem(i);
            if (stack.is(Items.TOTEM_OF_UNDYING)) count += stack.getCount();
        }
        ItemStack offhand = client.player.getOffhandItem();
        if (offhand.is(Items.TOTEM_OF_UNDYING)) count += offhand.getCount();
        graphics.fill(x - 3, y - 3, x + 48, y + 19, Hud.PANEL);
        graphics.renderItem(new ItemStack(Items.TOTEM_OF_UNDYING), x, y - 1);
        Hud.text(graphics, "x" + count, x + 20, y + 4, Hud.WHITE);
    }
}
