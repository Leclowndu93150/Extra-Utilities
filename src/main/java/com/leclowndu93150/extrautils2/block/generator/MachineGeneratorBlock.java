package com.leclowndu93150.extrautils2.block.generator;

import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.blockentity.generator.MachineGeneratorTile;
import com.leclowndu93150.extrautils2.block.XUEntityBlock;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MachineGeneratorBlock extends MachineBlock {

    public static final net.minecraft.world.level.block.state.properties.BooleanProperty POWERED = ACTIVE;

    public final MachineGeneratorType generatorType;

    public MachineGeneratorBlock(MachineGeneratorType type, BlockBehaviour.Properties props) {
        super(props);
        this.generatorType = type;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MachineGeneratorTile(ModBlockEntities.MACHINE_GENERATOR.get(), pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (BlockEntityTicker<T>) XUEntityBlock.createTicker(type, ModBlockEntities.MACHINE_GENERATOR.get(), MachineGeneratorTile::tick);
    }
}
