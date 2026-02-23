package com.leclowndu93150.extrautils2.gui;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public interface HasFluidBar {
    int getFluidBarX();
    int getFluidBarY();
    int getFluidAmount();
    int getFluidCapacity();
    FluidStack getFluidStack();
    int getFluidBarButtonId();
    @Nullable IFluidHandler getFluidHandler();
}
