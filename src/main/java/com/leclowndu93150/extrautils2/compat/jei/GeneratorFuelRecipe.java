package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public record GeneratorFuelRecipe(MachineGeneratorType type, List<ItemStack> inputs,
                                   FluidStack fluidInput, int totalEnergy, float rfPerTick) {

    public static List<GeneratorFuelRecipe> getRecipesFor(MachineGeneratorType type) {
        if (type == MachineGeneratorType.LAVA) {
            return List.of(new GeneratorFuelRecipe(type, List.of(),
                    new FluidStack(Fluids.LAVA, 50), 5000, 40f));
        }
        if (type == MachineGeneratorType.REDSTONE) {
            return List.of(new GeneratorFuelRecipe(type, List.of(new ItemStack(Items.REDSTONE)),
                    new FluidStack(Fluids.LAVA, 50), 20000, 160f));
        }
        if (type == MachineGeneratorType.SLIME) {
            return List.of(new GeneratorFuelRecipe(type,
                    List.of(new ItemStack(Items.SLIME_BALL, 4), new ItemStack(Items.MILK_BUCKET)),
                    FluidStack.EMPTY, 192000, 400f));
        }

        List<GeneratorFuelRecipe> recipes = new ArrayList<>();
        for (var item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);
            var result = type.getFuelResult(stack);
            if (result != null) {
                recipes.add(new GeneratorFuelRecipe(type, List.of(stack),
                        FluidStack.EMPTY, result.totalEnergy(), result.gpRate()));
            }
        }
        return recipes;
    }
}
