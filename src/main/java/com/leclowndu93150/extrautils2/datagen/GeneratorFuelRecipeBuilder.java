package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.recipe.GeneratorFuelRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class GeneratorFuelRecipeBuilder implements RecipeBuilder {

    private final String generatorType;
    private Ingredient input = Ingredient.EMPTY;
    private int inputCount = 1;
    private Ingredient secondaryInput = Ingredient.EMPTY;
    private int secondaryCount = 0;
    private FluidStack fluidInput = FluidStack.EMPTY;
    private final int totalEnergy;
    private final int energyPerTick;

    private GeneratorFuelRecipeBuilder(String generatorType, int totalEnergy, int energyPerTick) {
        this.generatorType = generatorType;
        this.totalEnergy = totalEnergy;
        this.energyPerTick = energyPerTick;
    }

    public static GeneratorFuelRecipeBuilder fuel(String generatorType, Ingredient input, int totalEnergy, int energyPerTick) {
        var builder = new GeneratorFuelRecipeBuilder(generatorType, totalEnergy, energyPerTick);
        builder.input = input;
        return builder;
    }

    public static GeneratorFuelRecipeBuilder fuel(String generatorType, Ingredient input, int inputCount, int totalEnergy, int energyPerTick) {
        var builder = new GeneratorFuelRecipeBuilder(generatorType, totalEnergy, energyPerTick);
        builder.input = input;
        builder.inputCount = inputCount;
        return builder;
    }

    public static GeneratorFuelRecipeBuilder fluidFuel(String generatorType, FluidStack fluid, int totalEnergy, int energyPerTick) {
        var builder = new GeneratorFuelRecipeBuilder(generatorType, totalEnergy, energyPerTick);
        builder.fluidInput = fluid;
        return builder;
    }

    public GeneratorFuelRecipeBuilder withFluid(FluidStack fluid) {
        this.fluidInput = fluid;
        return this;
    }

    public GeneratorFuelRecipeBuilder withSecondary(Ingredient secondary, int count) {
        this.secondaryInput = secondary;
        this.secondaryCount = count;
        return this;
    }

    public static GeneratorFuelRecipeBuilder dualFuel(String generatorType, Ingredient input, int inputCount,
                                                       Ingredient secondary, int secondaryCount,
                                                       int totalEnergy, int energyPerTick) {
        var builder = new GeneratorFuelRecipeBuilder(generatorType, totalEnergy, energyPerTick);
        builder.input = input;
        builder.inputCount = inputCount;
        builder.secondaryInput = secondary;
        builder.secondaryCount = secondaryCount;
        return builder;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        output.accept(id, new GeneratorFuelRecipe(
                generatorType, input, inputCount, secondaryInput, secondaryCount,
                fluidInput, totalEnergy, energyPerTick
        ), null);
    }
}
