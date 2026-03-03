package com.leclowndu93150.extrautils2.blockentity.transfer;

import com.leclowndu93150.extrautils2.block.transfer.TransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class TransferNodePing {

    private BlockPos homePos;
    private Direction homeDir;
    private BlockPos currentPos;
    private Direction direction;

    public void init(BlockPos home, Direction homeDir) {
        this.homePos = home;
        this.homeDir = homeDir;
        this.direction = homeDir;
        this.currentPos = home.relative(homeDir);
    }

    public void resetPosition() {
        if (homePos != null && homeDir != null) {
            this.currentPos = homePos.relative(homeDir);
            this.direction = homeDir;
        }
    }

    public BlockPos getCurrentPos() {
        return currentPos;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isInitialized() {
        return homePos != null;
    }

    public boolean advance(Level level) {
        if (currentPos == null) return false;
        Direction backtrack = direction.getOpposite();
        Direction[] shuffled = TransferHelper.getShuffledDirections();

        for (Direction dir : shuffled) {
            if (dir == backtrack) continue;
            BlockPos next = currentPos.relative(dir);
            if (TransferHelper.isPipeNotBlocked(level, next, dir.getOpposite())) {
                currentPos = next;
                direction = dir;
                return true;
            }
            if (TransferHelper.isNode(level, next)) {
                currentPos = next;
                direction = dir;
                return true;
            }
        }

        resetPosition();
        return false;
    }

    public void save(CompoundTag tag) {
        if (homePos != null) {
            tag.putIntArray("PingHome", new int[]{homePos.getX(), homePos.getY(), homePos.getZ(), homeDir.ordinal()});
        }
        if (currentPos != null) {
            tag.putIntArray("PingCurrent", new int[]{currentPos.getX(), currentPos.getY(), currentPos.getZ(), direction.ordinal()});
        }
    }

    public void load(CompoundTag tag) {
        if (tag.contains("PingHome")) {
            int[] h = tag.getIntArray("PingHome");
            if (h.length == 4) {
                homePos = new BlockPos(h[0], h[1], h[2]);
                homeDir = Direction.values()[h[3]];
            }
        }
        if (tag.contains("PingCurrent")) {
            int[] c = tag.getIntArray("PingCurrent");
            if (c.length == 4) {
                currentPos = new BlockPos(c[0], c[1], c[2]);
                direction = Direction.values()[c[3]];
            }
        }
    }
}
