package com.leclowndu93150.extrautils2.blockentity;

import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CreativeEnergyBlockEntity extends XUBlockEntity {

    private static final int MAX_SEND = 16777216;

    public static final IEnergyStorage INFINITE_ENERGY = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return maxExtract; }
        @Override public int getEnergyStored() { return MAX_SEND; }
        @Override public int getMaxEnergyStored() { return MAX_SEND; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    };

    public CreativeEnergyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_ENERGY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CreativeEnergyBlockEntity tile) {
        for (Direction dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
            if (neighbor == null) continue;
            IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighbor.getBlockPos(), dir.getOpposite());
            if (storage == null || !storage.canReceive()) continue;
            for (int i = 0; i < 100; i++) {
                int received = storage.receiveEnergy(MAX_SEND, false);
                if (received == 0) break;
            }
        }
    }
}
