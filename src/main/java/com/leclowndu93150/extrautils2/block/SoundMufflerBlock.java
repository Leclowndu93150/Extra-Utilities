package com.leclowndu93150.extrautils2.block;

import com.leclowndu93150.extrautils2.blockentity.SoundMufflerBlockEntity;
import com.leclowndu93150.extrautils2.event.SoundMufflerEvents;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SoundMufflerBlock extends XUBlock implements EntityBlock {
    public SoundMufflerBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoundMufflerBlockEntity(ModBlockEntities.SOUND_MUFFLER.get(), pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.SOUND_MUFFLER.get()) return null;
        return (lvl, pos, st, be) -> SoundMufflerBlockEntity.tick(lvl, pos, st, (SoundMufflerBlockEntity) be);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide) {
            SoundMufflerEvents.addMuffler(pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.isClientSide && !(newState.getBlock() instanceof SoundMufflerBlock)) {
            SoundMufflerEvents.removeMuffler(pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
