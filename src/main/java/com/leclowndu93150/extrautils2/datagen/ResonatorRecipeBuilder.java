package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.recipe.ResonatorRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class ResonatorRecipeBuilder implements RecipeBuilder {

    private final Ingredient input;
    private final ItemStack output;
    private final int energy;

    private ResonatorRecipeBuilder(Ingredient input, ItemStack output, int energy) {
        this.input = input;
        this.output = output;
        this.energy = energy;
    }

    public static ResonatorRecipeBuilder resonator(Ingredient input, ItemLike output, int energy) {
        return new ResonatorRecipeBuilder(input, new ItemStack(output), energy);
    }

    public static ResonatorRecipeBuilder resonator(Ingredient input, ItemStack output, int energy) {
        return new ResonatorRecipeBuilder(input, output, energy);
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
        return output.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        output.accept(id, new ResonatorRecipe(input, this.output, energy), null);
    }
}
