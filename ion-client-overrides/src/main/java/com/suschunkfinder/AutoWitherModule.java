package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Manual action-bind helper that builds a vanilla wither structure from the block under the crosshair. */
public final class AutoWitherModule extends ClientModule {
    private final NumberSetting delay = add(NumberSetting.of("Place delay (ticks)", 2, 1, 10, 1));
    private BlockPos base;
    private int wait;

    public AutoWitherModule() {
        super("Auto Wither", "Action bind builds a vanilla soul-sand T and adds skulls last to summon a wither.", Category.COMBAT);
    }

    @Override
    public void onBindAction() {
        Minecraft client = Minecraft.getInstance();
        if (client.hitResult instanceof BlockHitResult hit) {
            base = hit.getBlockPos().relative(hit.getDirection());
            wait = 0;
        }
    }

    @Override
    public void tick(Minecraft client) {
        if (base == null || client.player == null || client.level == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }

        BlockPos[] soul = {base, base.west(), base.east(), base.above()};
        for (int i = 0; i < soul.length; i++) {
            BlockPos pos = soul[i];
            if (client.level.getBlockState(pos).is(Blocks.SOUL_SAND)) continue;
            if (!client.level.getBlockState(pos).isAir()) { base = null; return; }
            BlockPos support = pos.below();
            if (client.level.getBlockState(support).isAir()) { base = null; return; }
            if (!place(client, Items.SOUL_SAND, support)) return;
            wait = delay.getInt();
            return;
        }

        BlockPos[] skulls = {base.west().above(), base.above(2), base.east().above()};
        for (BlockPos pos : skulls) {
            if (!client.level.getBlockState(pos).isAir()) {
                if (client.level.getBlockState(pos).is(Blocks.WITHER_SKELETON_SKULL)
                        || client.level.getBlockState(pos).is(Blocks.WITHER_SKELETON_WALL_SKULL)) continue;
                base = null;
                return;
            }
            if (!place(client, Items.WITHER_SKELETON_SKULL, pos.below())) return;
            wait = delay.getInt();
            return;
        }
        base = null;
    }

    private boolean place(Minecraft client, net.minecraft.world.item.Item item, BlockPos support) {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getItem(i).is(item)) { slot = i; break; }
        }
        if (slot < 0) { base = null; return false; }
        int previous = client.player.getInventory().getSelectedSlot();
        client.player.getInventory().setSelectedSlot(slot);
        client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(support), Direction.UP, support, false));
        client.player.getInventory().setSelectedSlot(previous);
        return true;
    }

    @Override
    protected void onDisable() { base = null; wait = 0; }
}
