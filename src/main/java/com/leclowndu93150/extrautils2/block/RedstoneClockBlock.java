package com.leclowndu93150.extrautils2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RedstoneClockBlock extends Block {

    public enum PowerState implements StringRepresentable {
        DISABLED, ENABLED_NOT_POWERED, ENABLED_POWERED;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static final EnumProperty<PowerState> POWER_STATE = EnumProperty.create("power_state", PowerState.class);

    public RedstoneClockBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(POWER_STATE, PowerState.DISABLED));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER_STATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(POWER_STATE, PowerState.DISABLED);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWER_STATE) == PowerState.ENABLED_POWERED ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        PowerState ps = state.getValue(POWER_STATE);
        boolean externallyPowered = isExternallyPowered(level, pos);

        if (externallyPowered && ps != PowerState.DISABLED) {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.DISABLED), 3);
        } else if (!externallyPowered && ps == PowerState.DISABLED) {
            scheduleCycle(level, pos, state);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (state.getValue(POWER_STATE) == PowerState.DISABLED) return;
        if (isExternallyPowered(level, pos)) {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.DISABLED), 3);
            return;
        }
        scheduleCycle(level, pos, state);
    }

    private void scheduleCycle(Level level, BlockPos pos, BlockState state) {
        int l = (int)(level.getGameTime() % 20L);
        if (l < 2) {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.ENABLED_POWERED), 2);
            level.scheduleTick(pos, this, 2 - l);
        } else {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.ENABLED_NOT_POWERED), 2);
            level.scheduleTick(pos, this, 20 - l);
        }
    }

    private boolean isExternallyPowered(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            if (level.getBlockState(neighbor).getSignal(level, neighbor, dir.getOpposite()) > 0) {
                return true;
            }
        }
        return false;
    }
}
