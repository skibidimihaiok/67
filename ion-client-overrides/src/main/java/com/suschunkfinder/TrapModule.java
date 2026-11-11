package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Places obsidian beside the player under the crosshair where blocks can be supported. */
public final class TrapModule extends ClientModule {
    private final NumberSetting range = add(NumberSetting.of("Range", 4.0, 1.0, 6.0, 0.5));
    private final NumberSetting delay = add(NumberSetting.of("Place delay (ticks)", 3, 1, 20, 1));
    private int wait;
    private int restoreSlot = -1;

    public TrapModule() {
        super("Trap", "Places obsidian around the feet of the player under your crosshair.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.gameMode == null || client.screen != null) return;
        if (restoreSlot >= 0) {
            client.player.getInventory().setSelectedSlot(restoreSlot);
            restoreSlot = -1;
        }
        if (wait > 0) { wait--; return; }
        if (!(client.crosshairPickEntity instanceof Player target) || target == client.player
                || !target.isAlive() || client.player.distanceTo(target) > range.get()) return;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getItem(i).is(Items.OBSIDIAN)) { slot = i; break; }
        }
        if (slot < 0) return;
        BlockPos base = target.blockPosition();
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (Direction direction : directions) {
            BlockPos place = base.relative(direction);
            BlockPos support = place.below();
            if (!client.level.getBlockState(place).isAir() || client.level.getBlockState(support).isAir()) continue;
            restoreSlot = client.player.getInventory().getSelectedSlot();
            client.player.getInventory().setSelectedSlot(slot);
            client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(support), Direction.UP, support, false));
            wait = delay.getInt();
            return;
        }
    }

    @Override
    protected void onDisable() { wait = 0; restoreSlot = -1; }
}
