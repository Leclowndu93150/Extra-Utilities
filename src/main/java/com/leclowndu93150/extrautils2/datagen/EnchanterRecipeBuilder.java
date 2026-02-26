package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.recipe.EnchanterRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class EnchanterRecipeBuilder implements RecipeBuilder {

    private final Ingredient catalyst;
    private final String enchantMode;
    private final int energy;
    private final int processingTime;

    private EnchanterRecipeBuilder(Ingredient catalyst, String enchantMode, int energy, int processingTime) {
        this.catalyst = catalyst;
        this.enchantMode = enchantMode;
        this.energy = energy;
        this.processingTime = processingTime;
    }

    public static EnchanterRecipeBuilder enchanter(Ingredient catalyst, String enchantMode, int energy, int processingTime) {
        return new EnchanterRecipeBuilder(catalyst, enchantMode, energy, processingTime);
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
        return Items.ENCHANTED_BOOK;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        output.accept(id, new EnchanterRecipe(catalyst, enchantMode, energy, processingTime), null);
    }
}
