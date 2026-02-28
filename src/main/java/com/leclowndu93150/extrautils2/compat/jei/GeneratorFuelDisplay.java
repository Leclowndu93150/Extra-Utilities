package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record GeneratorFuelDisplay(
        MachineGeneratorType type,
        List<ItemStack> inputs,
        FluidStack fluidInput,
        int totalEnergy,
        int energyPerTick
) {
}
