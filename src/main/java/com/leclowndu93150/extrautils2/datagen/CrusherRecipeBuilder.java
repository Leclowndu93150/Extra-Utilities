package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.recipe.CrusherRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class CrusherRecipeBuilder implements RecipeBuilder {

    private final Ingredient input;
    private final ItemStack output;
    private ItemStack secondaryOutput = ItemStack.EMPTY;
    private float secondaryChance = 0f;
    private final int energy;
    private final int processingTime;

    private CrusherRecipeBuilder(Ingredient input, ItemStack output, int energy, int processingTime) {
        this.input = input;
        this.output = output;
        this.energy = energy;
        this.processingTime = processingTime;
    }

    public static CrusherRecipeBuilder crusher(Ingredient input, ItemLike output, int count, int energy, int processingTime) {
        return new CrusherRecipeBuilder(input, new ItemStack(output, count), energy, processingTime);
    }

    public static CrusherRecipeBuilder crusher(Ingredient input, ItemLike output, int energy, int processingTime) {
        return crusher(input, output, 1, energy, processingTime);
    }

    public CrusherRecipeBuilder secondary(ItemLike item, int count, float chance) {
        this.secondaryOutput = new ItemStack(item, count);
        this.secondaryChance = chance;
        return this;
    }

    public CrusherRecipeBuilder secondary(ItemLike item, float chance) {
        return secondary(item, 1, chance);
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
        output.accept(id, new CrusherRecipe(input, this.output, secondaryOutput, secondaryChance, energy, processingTime), null);
    }
}
