package com.leclowndu93150.extrautils2.block;

import com.leclowndu93150.extrautils2.blockentity.CreativeHarvestBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CreativeHarvestBlock extends XUEntityBlock {

    public CreativeHarvestBlock(Properties props) {
        super(props.strength(-1.0f, 3600000.0f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeHarvestBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(pos) instanceof CreativeHarvestBlockEntity tile)) return InteractionResult.PASS;

        if (player.isShiftKeyDown() && tile.hasMimic()) {
            Block mimicBlock = tile.getMimicState().getBlock();
            ItemStack drop = new ItemStack(mimicBlock);
            if (!drop.isEmpty() && !player.getInventory().add(drop)) {
                player.drop(drop, false);
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(pos) instanceof CreativeHarvestBlockEntity tile)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (stack.getItem() instanceof BlockItem blockItem && player.isCreative()) {
            Block block = blockItem.getBlock();
            if (!(block instanceof CreativeHarvestBlock)) {
                tile.setMimicState(block.defaultBlockState());
                return ItemInteractionResult.CONSUME;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
