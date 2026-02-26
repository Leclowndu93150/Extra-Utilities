package com.leclowndu93150.extrautils2.block.machine;

import com.leclowndu93150.extrautils2.block.XUEntityBlock;
import com.leclowndu93150.extrautils2.blockentity.machine.EnchanterBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EnchanterBlock extends MachineBlock {

    public EnchanterBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchanterBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (BlockEntityTicker<T>) XUEntityBlock.createTicker(type, ModBlockEntities.MACHINE_ENCHANTER.get(), EnchanterBlockEntity::tick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            for (int i = 0; i < 3; i++) {
                double x = pos.getX() + random.nextFloat();
                double y = pos.getY() + 2.0;
                double z = pos.getZ() + random.nextFloat();
                double dx = (random.nextFloat() - 0.5) * 0.5;
                double dy = (random.nextFloat() - 0.5) * 0.5;
                double dz = (random.nextFloat() - 0.5) * 0.5;
                level.addParticle(ParticleTypes.ENCHANT, x, y, z, dx, dy, dz);
            }
        }
    }
}
