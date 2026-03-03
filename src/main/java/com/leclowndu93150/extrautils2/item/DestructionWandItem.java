package com.leclowndu93150.extrautils2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class DestructionWandItem extends Item {

    private final int range;

    public DestructionWandItem(int range) {
        super(new Properties().stacksTo(1));
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (isCorrectToolForDrops(stack, state)) return 6.0f;
        return 1.0f;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, net.minecraft.world.entity.LivingEntity miner) {
        if (level.isClientSide() || !(miner instanceof ServerPlayer player)) return true;

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) return true;

        Direction face = hit.getDirection();
        List<BlockPos> blocks = getDestructionPositions(level, pos, face, player);

        for (BlockPos bp : blocks) {
            if (bp.equals(pos)) continue;
            BlockState bs = level.getBlockState(bp);
            if (bs.isAir()) continue;

            if (!player.isCreative()) {
                BlockEntity be = level.getBlockEntity(bp);
                Block.dropResources(bs, level, bp, be, player, stack);
            }
            level.destroyBlock(bp, false, player);
        }

        return true;
    }

    public List<BlockPos> getDestructionPositions(Level level, BlockPos origin, Direction face, Player player) {
        BlockState targetState = level.getBlockState(origin);
        if (targetState.isAir()) return List.of();
        if (!isCorrectToolForDrops(player.getMainHandItem(), targetState) && !targetState.requiresCorrectToolForDrops())
            return WandHelper.getSelection(level, origin, face, player, range);
        if (!isCorrectToolForDrops(player.getMainHandItem(), targetState)) return List.of();
        return WandHelper.getSelection(level, origin, face, player, range);
    }

    public float getAdjustedSpeed(Player player, BlockState state, BlockPos pos) {
        Level level = player.level();
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) return 0f;
        if (!hit.getBlockPos().equals(pos)) return 0f;

        List<BlockPos> blocks = getDestructionPositions(level, pos, hit.getDirection(), player);
        if (blocks.isEmpty()) return 0f;

        float base = getDestroySpeed(player.getMainHandItem(), state);
        return base / blocks.size();
    }
}
