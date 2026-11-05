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
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;

/** Storage boxes and optional category-specific tracer rays. */
public final class StorageEspModule extends ClientModule implements EspSource {
    private final ModeSetting shape = add(ModeSetting.of("Shape", 0, "Outline", "Boxes"));
    private final NumberSetting boxSize = add(NumberSetting.of("Box Size", 1.0, 0.1, 2.0, 0.1));
    private final NumberSetting fillAlpha = add(NumberSetting.of("Fill Alpha", 0.25, 0.0, 1.0, 0.05));
    private final NumberSetting lineWidth = add(NumberSetting.of("Line Width", 1.5, 0.5, 5.0, 0.5));
    private final BoolSetting glow = add(BoolSetting.of("Glow", true));
    private final BoolSetting distanceFade = add(BoolSetting.of("Distance Fade", true));
    private final BoolSetting reveal = add(BoolSetting.of("Reveal Animation", true));
    private final NumberSetting maxDistance = add(NumberSetting.of("Max Distance", 256, 16, 2048, 16));
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
    private final BoolSetting spawners = add(BoolSetting.of("Spawners", true));
    private final ColorSetting spawnerColor = add(new ColorSetting("Spawner Color", -7700826));
    private final BoolSetting spawnerTracers = add(BoolSetting.of("Spawner Tracers", false));
    private final ColorSetting spawnerTracerColor = add(new ColorSetting("Spawner Tracer Color", -7700826));
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

    private long enabledAt;
    @Override protected void onEnable() { enabledAt = System.currentTimeMillis(); }

    @Override public void collect(EspBatch batch, float pt) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        // Anchor tracer rays to the active render camera so their origin is the screen crosshair, including third-person/freecam views.
        Vec3 eye = client.gameRenderer.getMainCamera().position();
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
            } else if (be instanceof SpawnerBlockEntity) {
                enabled = spawners.get(); color = spawnerColor; tracerEnabled = spawnerTracers.get(); tracerColor = spawnerTracerColor;
            } else if (be instanceof AbstractFurnaceBlockEntity) {
                enabled = furnaces.get(); color = furnaceColor; tracerEnabled = furnaceTracers.get(); tracerColor = furnaceTracerColor;
            } else if (be instanceof HopperBlockEntity || be instanceof DispenserBlockEntity) {
                enabled = hoppers.get(); color = hopperColor; tracerEnabled = hopperTracers.get(); tracerColor = hopperTracerColor;
            }
            if (!enabled || color == null) return;
            BlockEntity blockEntity = be;
            Vec3 center = Vec3.atCenterOf(blockEntity.getBlockPos());
            double distance = Math.sqrt(eye.distanceToSqr(center));
            if (distance > maxDistance.get()) return;
            double fade = distanceFade.get() ? Math.max(0.15, 1.0 - distance / Math.max(1.0, maxDistance.get())) : 1.0;
            float alpha = (float)(fade * (reveal.get() ? Math.min(1.0, (System.currentTimeMillis() - enabledAt) / 350.0) : 1.0));
            double half = boxSize.get() * 0.5;
            double x = center.x, y = center.y, z = center.z;
            double x0=x-half, x1=x+half, y0=y-half, y1=y+half, z0=z-half, z1=z+half;
            float cr=color.red(), cg=color.green(), cb=color.blue();
            if (glow.get()) batch.outline(x0-0.025,y0-0.025,z0-0.025,x1+0.025,y1+0.025,z1+0.025,cr,cg,cb,0.22f*alpha,lineWidth.get()*0.025);
            if (shape.index() == 0) {
                batch.outline(x0,y0,z0,x1,y1,z1,cr,cg,cb,alpha,lineWidth.get()*0.025);
                if (fill.get() && fillAlpha.get() > 0) batch.box(x0,y0,z0,x1,y1,z1,cr,cg,cb,(float)fillAlpha.get()*alpha);
            } else {
                batch.box(x0,y0,z0,x1,y1,z1,cr,cg,cb,Math.max((float)fillAlpha.get(),0.15f)*alpha);
            }
            if (tracers.get() && tracerEnabled && tracerColor != null) {
                batch.line(eye,center,tracerColor.red(),tracerColor.green(),tracerColor.blue(),alpha,0.04);
            }
        });
    }
}
