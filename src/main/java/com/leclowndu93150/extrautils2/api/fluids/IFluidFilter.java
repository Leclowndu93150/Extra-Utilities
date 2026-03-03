package com.leclowndu93150.extrautils2.api.fluids;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IFluidFilter {
    boolean isFluidFilter(ItemStack filterStack);

    boolean matches(ItemStack filterStack, FluidStack target);
}
