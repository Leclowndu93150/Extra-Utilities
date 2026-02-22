package com.leclowndu93150.extrautils2.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class XUBlock extends Block {
    public XUBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    public static BlockBehaviour.Properties defaultProps() {
        return BlockBehaviour.Properties.of()
                .strength(1.5f, 6f)
                .requiresCorrectToolForDrops();
    }

    public static class Facing extends XUBlock {
        public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

        public Facing(BlockBehaviour.Properties props) {
            super(props);
            registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext ctx) {
            return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
        }
    }

    public static class FacingAll extends XUBlock {
        public static final DirectionProperty FACING = BlockStateProperties.FACING;

        public FacingAll(BlockBehaviour.Properties props) {
            super(props);
            registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FACING);
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext ctx) {
            return defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
        }
    }
}
