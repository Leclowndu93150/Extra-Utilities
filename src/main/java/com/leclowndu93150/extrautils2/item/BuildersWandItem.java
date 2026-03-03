package com.leclowndu93150.extrautils2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BuildersWandItem extends Item {

    private final int range;

    public BuildersWandItem(int range) {
        super(new Properties().stacksTo(1));
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        var player = context.getPlayer();
        if (player == null || !player.mayBuild()) return InteractionResult.FAIL;

        BlockPos origin = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState targetState = level.getBlockState(origin);
        if (targetState.isAir()) return InteractionResult.FAIL;

        BlockPos testPos = origin.relative(face);
        if (!level.getBlockState(testPos).canBeReplaced()) return InteractionResult.FAIL;

        int available = player.isCreative() ? range : countMatchingBlocks(player, targetState);
        if (available <= 0) return InteractionResult.FAIL;

        List<BlockPos> surface = WandHelper.getSelection(level, origin, face, player, Math.min(available, range));

        List<BlockPos> placements = new ArrayList<>();
        for (BlockPos surfacePos : surface) {
            BlockPos placePos = surfacePos.relative(face);
            if (!level.getBlockState(placePos).canBeReplaced()) continue;
            if (!player.canInteractWithBlock(placePos, 0)) continue;
            placements.add(placePos);
        }

        if (placements.isEmpty()) return InteractionResult.FAIL;

        int placed = 0;
        for (BlockPos placePos : placements) {
            ItemStack sourceStack = player.isCreative() ? getMatchingStack(targetState) : findAndConsumeBlock(player, targetState);
            if (sourceStack.isEmpty()) break;

            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(placePos), face.getOpposite(), placePos, false);
            BlockPlaceContext placeContext = new BlockPlaceContext(level, player, context.getHand(), sourceStack, hit);
            BlockState placeState = targetState.getBlock().getStateForPlacement(placeContext);
            if (placeState == null) placeState = targetState;

            level.setBlock(placePos, placeState, 3);
            level.blockUpdated(placePos, placeState.getBlock());
            placeState.getBlock().setPlacedBy(level, placePos, placeState, player, sourceStack);
            placed++;
        }

        if (placed > 0 && player instanceof ServerPlayer sp) {
            sp.inventoryMenu.broadcastChanges();
        }

        return placed > 0 ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    public List<BlockPos> getPlacementPositions(Level level, BlockPos origin, Direction face, net.minecraft.world.entity.player.Player player) {
        BlockState targetState = level.getBlockState(origin);
        if (targetState.isAir()) return List.of();

        BlockPos testPos = origin.relative(face);
        if (!level.getBlockState(testPos).canBeReplaced()) return List.of();

        int available = player.isCreative() ? range : countMatchingBlocks(player, targetState);
        if (available <= 0) return List.of();

        List<BlockPos> surface = WandHelper.getSelection(level, origin, face, player, Math.min(available, range));

        List<BlockPos> placements = new ArrayList<>();
        for (BlockPos surfacePos : surface) {
            BlockPos placePos = surfacePos.relative(face);
            if (!level.getBlockState(placePos).canBeReplaced()) continue;
            placements.add(placePos);
        }
        return placements;
    }

    private int countMatchingBlocks(net.minecraft.world.entity.player.Player player, BlockState targetState) {
        int count = 0;
        Block targetBlock = targetState.getBlock();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof BlockItem bi) {
                if (bi.getBlock() == targetBlock || WandHelper.isGrassLike(targetBlock) && WandHelper.isGrassLike(bi.getBlock())) {
                    count += stack.getCount();
                }
            }
        }
        return count;
    }

    private ItemStack findAndConsumeBlock(net.minecraft.world.entity.player.Player player, BlockState targetState) {
        Block targetBlock = targetState.getBlock();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof BlockItem bi) {
                if (bi.getBlock() == targetBlock || WandHelper.isGrassLike(targetBlock) && WandHelper.isGrassLike(bi.getBlock())) {
                    ItemStack taken = stack.split(1);
                    return taken;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack getMatchingStack(BlockState state) {
        return new ItemStack(state.getBlock().asItem());
    }
}
