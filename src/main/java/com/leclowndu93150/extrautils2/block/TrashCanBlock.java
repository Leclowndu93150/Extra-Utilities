package com.leclowndu93150.extrautils2.block;

import com.leclowndu93150.extrautils2.blockentity.TrashCanBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrashCanBlock extends XUEntityBlock {

    public enum TrashType { ITEM, FLUID, ENERGY }

    private static final VoxelShape SHAPE = Shapes.join(
            Shapes.or(
                    Block.box(2, 0, 2, 14, 10, 14),
                    Block.box(1, 10, 1, 15, 14, 15),
                    Block.box(5, 14, 7, 11, 15, 9)
            ),
            Shapes.empty(), BooleanOp.OR);

    private final TrashType trashType;

    public TrashCanBlock(Properties props, TrashType trashType) {
        super(props);
        this.trashType = trashType;
    }

    public TrashType getTrashType() {
        return trashType;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (trashType == TrashType.ENERGY) return InteractionResult.PASS;
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TrashCanBlockEntity tile) {
            player.openMenu(tile, buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrashCanBlockEntity(ModBlockEntities.TRASH_CAN.get(), pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
