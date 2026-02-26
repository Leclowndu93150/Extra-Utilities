package com.leclowndu93150.extrautils2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RedstoneClockBlock extends XUBlock {

    public enum PowerState implements StringRepresentable {
        DISABLED, ENABLED_NOT_POWERED, ENABLED_POWERED;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static final EnumProperty<PowerState> POWER_STATE = EnumProperty.create("power_state", PowerState.class);

    private boolean canProvidePower = true;
    private boolean changing = false;

    public RedstoneClockBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(POWER_STATE, PowerState.DISABLED));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER_STATE);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide) scheduleCycle(level, pos, state);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return canProvidePower && state.getValue(POWER_STATE) == PowerState.ENABLED_POWERED ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (changing || level.isClientSide) return;
        PowerState ps = state.getValue(POWER_STATE);
        boolean powered = isExternallyPowered(level, pos);
        changing = true;
        if (powered && ps != PowerState.DISABLED) {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.DISABLED), 3);
            level.updateNeighborsAt(pos, this);
        } else if (!powered && ps == PowerState.DISABLED) {
            scheduleCycle(level, pos, state);
        }
        changing = false;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (state.getValue(POWER_STATE) == PowerState.DISABLED) return;
        int l = (int)(level.getGameTime() % 20L);
        changing = true;
        if (l < 2) {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.ENABLED_POWERED), 3);
            level.updateNeighborsAt(pos, this);
            level.scheduleTick(pos, this, 2 - l);
        } else {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.ENABLED_NOT_POWERED), 3);
            level.updateNeighborsAt(pos, this);
            if (isExternallyPowered(level, pos)) {
                level.setBlock(pos, state.setValue(POWER_STATE, PowerState.DISABLED), 3);
                level.updateNeighborsAt(pos, this);
            } else {
                level.scheduleTick(pos, this, 20 - l);
            }
        }
        changing = false;
    }

    private void scheduleCycle(Level level, BlockPos pos, BlockState state) {
        int l = (int)(level.getGameTime() % 20L);
        if (l < 2) {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.ENABLED_POWERED), 3);
            level.updateNeighborsAt(pos, this);
            level.scheduleTick(pos, this, 2 - l);
        } else {
            level.setBlock(pos, state.setValue(POWER_STATE, PowerState.ENABLED_NOT_POWERED), 3);
            level.updateNeighborsAt(pos, this);
            level.scheduleTick(pos, this, 20 - l);
        }
    }

    private boolean isExternallyPowered(Level level, BlockPos pos) {
        canProvidePower = false;
        boolean powered = false;
        for (Direction dir : Direction.values()) {
            if (level.getDirectSignal(pos.relative(dir), dir) > 0) {
                powered = true;
                break;
            }
        }
        canProvidePower = true;
        return powered;
    }
}
