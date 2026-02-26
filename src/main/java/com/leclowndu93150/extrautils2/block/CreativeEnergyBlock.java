package com.leclowndu93150.extrautils2.block;

import com.leclowndu93150.extrautils2.blockentity.CreativeEnergyBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeEnergyBlock extends XUEntityBlock {

    public CreativeEnergyBlock(Properties props) {
        super(props.strength(-1.0f, 3600000.0f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEnergyBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTicker(type, ModBlockEntities.CREATIVE_ENERGY.get(), CreativeEnergyBlockEntity::tick);
    }
}
