package com.leclowndu93150.extrautils2.block.machine;

import com.leclowndu93150.extrautils2.block.XUEntityBlock;
import com.leclowndu93150.extrautils2.blockentity.machine.CrusherBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CrusherBlock extends MachineBlock {

    public CrusherBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (BlockEntityTicker<T>) XUEntityBlock.createTicker(type, ModBlockEntities.MACHINE_CRUSHER.get(), CrusherBlockEntity::tick);
    }
}
