package com.leclowndu93150.extrautils2.block.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.concurrent.ThreadLocalRandom;

public final class TransferHelper {

    private TransferHelper() {}

    public static boolean isPipe(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof TransferPipeBlock;
    }

    public static boolean isPipeNotBlocked(Level level, BlockPos pos, Direction entryFace) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TransferPipeBlock)) return false;
        int blocked = state.getValue(TransferPipeBlock.BLOCKED);
        return (blocked & (1 << entryFace.ordinal())) == 0;
    }

    public static boolean isNode(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof TransferNodeBlock;
    }

    public static boolean hasItemInventory(Level level, BlockPos pos, Direction face) {
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, face) != null;
    }

    public static Direction[] getShuffledDirections() {
        Direction[] dirs = Direction.values().clone();
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = dirs.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            Direction tmp = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = tmp;
        }
        return dirs;
    }
}
