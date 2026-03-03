package com.leclowndu93150.extrautils2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class WandHelper {

    public static boolean isGrassLike(Block block) {
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.MYCELIUM || block == Blocks.PODZOL;
    }

    public static boolean blocksMatch(BlockState a, BlockState b) {
        if (a.getBlock() == b.getBlock()) return true;
        return isGrassLike(a.getBlock()) && isGrassLike(b.getBlock());
    }

    public static EnumSet<Direction> getSearchDirections(Direction clickedFace, Player player, boolean sneak, boolean sprint) {
        EnumSet<Direction> dirs = EnumSet.noneOf(Direction.class);

        for (Direction d : Direction.values()) {
            if (d == clickedFace || d == clickedFace.getOpposite()) continue;
            dirs.add(d);
        }

        if (sneak && !sprint) {
            Direction facing = player.getDirection();
            if (clickedFace.getAxis() == Direction.Axis.Y) {
                dirs.remove(facing);
                dirs.remove(facing.getOpposite());
            } else {
                dirs.remove(Direction.UP);
                dirs.remove(Direction.DOWN);
            }
        } else if (sneak && sprint) {
            Direction facing = player.getDirection();
            if (clickedFace.getAxis() == Direction.Axis.Y) {
                Direction cross = facing.getClockWise();
                dirs.remove(cross);
                dirs.remove(cross.getCounterClockWise().getCounterClockWise());
            } else {
                dirs.removeIf(d -> d.getAxis() != Direction.Axis.Y);
            }
        }

        return dirs;
    }

    public static List<BlockPos> getSelection(Level level, BlockPos origin, Direction face,
                                               Player player, int maxBlocks) {
        BlockState targetState = level.getBlockState(origin);
        if (targetState.isAir()) return List.of();

        boolean sneak = player.isShiftKeyDown();
        boolean sprint = player.isSprinting();
        EnumSet<Direction> searchDirs = getSearchDirections(face, player, sneak, sprint);

        List<int[]> vectors = new ArrayList<>();
        for (Direction d : searchDirs) {
            vectors.add(new int[]{d.getStepX(), d.getStepY(), d.getStepZ()});
        }
        for (Direction d1 : searchDirs) {
            for (Direction d2 : searchDirs) {
                if (d1.ordinal() >= d2.ordinal()) continue;
                int dx = d1.getStepX() + d2.getStepX();
                int dy = d1.getStepY() + d2.getStepY();
                int dz = d1.getStepZ() + d2.getStepZ();
                if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || Math.abs(dz) > 1) continue;
                vectors.add(new int[]{dx, dy, dz});
            }
        }

        Set<Long> visited = new HashSet<>();
        LinkedList<BlockPos> queue = new LinkedList<>();
        List<BlockPos> result = new ArrayList<>();

        visited.add(origin.asLong());
        queue.add(origin);
        result.add(origin);

        while (!queue.isEmpty() && result.size() < maxBlocks) {
            BlockPos current = queue.poll();
            for (int[] v : vectors) {
                BlockPos next = current.offset(v[0], v[1], v[2]);
                if (!visited.add(next.asLong())) continue;
                BlockState nextState = level.getBlockState(next);
                if (!blocksMatch(targetState, nextState)) continue;
                result.add(next);
                if (result.size() >= maxBlocks) break;
                queue.add(next);
            }
        }

        return result;
    }
}
