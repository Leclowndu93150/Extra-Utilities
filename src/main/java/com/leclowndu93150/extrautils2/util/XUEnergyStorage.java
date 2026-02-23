package com.leclowndu93150.extrautils2.util;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.EnergyStorage;

public class XUEnergyStorage extends EnergyStorage {
    private final boolean allowReceive;
    private final boolean allowExtract;

    public XUEnergyStorage(int capacity, int maxReceive, int maxExtract, boolean allowReceive, boolean allowExtract) {
        super(capacity, maxReceive, maxExtract);
        this.allowReceive = allowReceive;
        this.allowExtract = allowExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!allowReceive) return 0;
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!allowExtract) return 0;
        return super.extractEnergy(maxExtract, simulate);
    }

    @Override
    public boolean canReceive() {
        return allowReceive;
    }

    @Override
    public boolean canExtract() {
        return allowExtract;
    }

    public void setEnergy(int energy) {
        this.energy = Mth.clamp(energy, 0, capacity);
    }

    public int addEnergy(int amount) {
        if (amount <= 0) return 0;
        int accepted = Math.min(capacity - energy, amount);
        energy += accepted;
        return accepted;
    }

    public int getMaxExtract() {
        return maxExtract;
    }
}
