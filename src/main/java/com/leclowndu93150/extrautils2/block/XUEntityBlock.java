package com.leclowndu93150.extrautils2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;

public abstract class XUEntityBlock extends XUBlock implements EntityBlock {
    public XUEntityBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
            BlockEntityType<?> given,
            BlockEntityType<T> expected,
            BlockEntityTicker<? super T> ticker) {
        return given == expected ? (BlockEntityTicker<T>) ticker : null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (fluidHandler != null) {
            if (FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                if (!level.isClientSide) {
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be != null) be.setChanged();
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static abstract class Facing extends XUBlock.Facing implements EntityBlock {
        public Facing(BlockBehaviour.Properties props) {
            super(props);
        }

        @Override
        protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            var fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if (fluidHandler != null) {
                if (FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                    if (!level.isClientSide) {
                        BlockEntity be = level.getBlockEntity(pos);
                        if (be != null) be.setChanged();
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    public static abstract class FacingAll extends XUBlock.FacingAll implements EntityBlock {
        public FacingAll(BlockBehaviour.Properties props) {
            super(props);
        }

        @Override
        protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            var fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            if (fluidHandler != null) {
                if (FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                    if (!level.isClientSide) {
                        BlockEntity be = level.getBlockEntity(pos);
                        if (be != null) be.setChanged();
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }
}
