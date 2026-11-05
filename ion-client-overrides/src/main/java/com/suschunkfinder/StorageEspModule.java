package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;

/** Storage boxes and optional category-specific tracer rays. */
public final class StorageEspModule extends ClientModule implements EspSource {
    private final NumberSetting range = add(NumberSetting.of("Range (chunks)", 8, 1, 32, 1));
    private final BoolSetting tracers = add(BoolSetting.of("Tracers", false));
    private final BoolSetting chests = add(BoolSetting.of("Chests", true));
    private final ColorSetting chestColor = add(new ColorSetting("Chest Color", -6530304));
    private final BoolSetting chestTracers = add(BoolSetting.of("Chest Tracers", false));
    private final ColorSetting chestTracerColor = add(new ColorSetting("Chest Tracer Color", -6530304));
    private final BoolSetting enderChests = add(BoolSetting.of("Ender Chests", true));
    private final ColorSetting enderColor = add(new ColorSetting("Ender Chest Color", -9109249));
    private final BoolSetting enderTracers = add(BoolSetting.of("Ender Chest Tracers", false));
    private final ColorSetting enderTracerColor = add(new ColorSetting("Ender Chest Tracer Color", -9109249));
    private final BoolSetting shulkers = add(BoolSetting.of("Shulker Boxes", true));
    private final ColorSetting shulkerColor = add(new ColorSetting("Shulker Color", -7995234));
    private final BoolSetting shulkerTracers = add(BoolSetting.of("Shulker Tracers", false));
    private final ColorSetting shulkerTracerColor = add(new ColorSetting("Shulker Tracer Color", -7995234));
    private final BoolSetting furnaces = add(BoolSetting.of("Furnaces", false));
    private final ColorSetting furnaceColor = add(new ColorSetting("Furnace Color", -10197916));
    private final BoolSetting furnaceTracers = add(BoolSetting.of("Furnace Tracers", false));
    private final ColorSetting furnaceTracerColor = add(new ColorSetting("Furnace Tracer Color", -10197916));
    private final BoolSetting barrels = add(BoolSetting.of("Barrels", true));
    private final ColorSetting barrelColor = add(new ColorSetting("Barrel Color", -10204366));
    private final BoolSetting barrelTracers = add(BoolSetting.of("Barrel Tracers", false));
    private final ColorSetting barrelTracerColor = add(new ColorSetting("Barrel Tracer Color", -10204366));
    private final BoolSetting hoppers = add(BoolSetting.of("Hoppers", false));
    private final ColorSetting hopperColor = add(new ColorSetting("Hopper Color", -11513776));
    private final BoolSetting hopperTracers = add(BoolSetting.of("Hopper Tracers", false));
    private final ColorSetting hopperTracerColor = add(new ColorSetting("Hopper Tracer Color", -11513776));
    private final BoolSetting fill = add(BoolSetting.of("Filled", true));

    public StorageEspModule() {
        super("Storage ESP", "Highlights storage containers with independent colors and tracer controls.", Category.RENDER);
    }

    @Override public void collect(EspBatch batch, float pt) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        Vec3 eye = client.player.getEyePosition(pt);
        EspUtil.forEachBlockEntity(range.getInt(), be -> {
            ColorSetting color = null, tracerColor = null;
            boolean enabled = false, tracerEnabled = false;
            if (be instanceof TrappedChestBlockEntity || be instanceof ChestBlockEntity) {
                enabled = chests.get(); color = chestColor; tracerEnabled = chestTracers.get(); tracerColor = chestTracerColor;
            } else if (be instanceof EnderChestBlockEntity) {
                enabled = enderChests.get(); color = enderColor; tracerEnabled = enderTracers.get(); tracerColor = enderTracerColor;
            } else if (be instanceof ShulkerBoxBlockEntity) {
                enabled = shulkers.get(); color = shulkerColor; tracerEnabled = shulkerTracers.get(); tracerColor = shulkerTracerColor;
            } else if (be instanceof BarrelBlockEntity) {
                enabled = barrels.get(); color = barrelColor; tracerEnabled = barrelTracers.get(); tracerColor = barrelTracerColor;
            } else if (be instanceof AbstractFurnaceBlockEntity) {
                enabled = furnaces.get(); color = furnaceColor; tracerEnabled = furnaceTracers.get(); tracerColor = furnaceTracerColor;
            } else if (be instanceof HopperBlockEntity || be instanceof DispenserBlockEntity) {
                enabled = hoppers.get(); color = hopperColor; tracerEnabled = hopperTracers.get(); tracerColor = hopperTracerColor;
            }
            if (!enabled || color == null) return;
            BlockEntity blockEntity = be;
            EspUtil.block(batch, blockEntity.getBlockPos(), new float[]{color.red(),color.green(),color.blue()}, fill.get());
            if (tracers.get() && tracerEnabled && tracerColor != null) {
                Vec3 end = Vec3.atCenterOf(blockEntity.getBlockPos());
                batch.line(eye,end,tracerColor.red(),tracerColor.green(),tracerColor.blue(),0.95f,0.04);
            }
        });
    }
}
