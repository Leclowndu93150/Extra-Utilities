package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.recipe.EnchanterRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnchanterRecipeBuilder implements RecipeBuilder {

    private final Ingredient catalyst;
    private final String enchantMode;
    private final int energy;
    private final int processingTime;
    private Optional<Ingredient> input = Optional.empty();
    private Optional<ItemStack> result = Optional.empty();
    private int inputCount = 1;
    private int outputCount = 1;

    private EnchanterRecipeBuilder(Ingredient catalyst, String enchantMode, int energy, int processingTime) {
        this.catalyst = catalyst;
        this.enchantMode = enchantMode;
        this.energy = energy;
        this.processingTime = processingTime;
    }

    public static EnchanterRecipeBuilder enchanter(Ingredient catalyst, String enchantMode, int energy, int processingTime) {
        return new EnchanterRecipeBuilder(catalyst, enchantMode, energy, processingTime);
    }

    public static EnchanterRecipeBuilder transformation(Ingredient catalyst, Ingredient input, int inputCount,
                                                         ItemLike output, int outputCount, int energy, int processingTime) {
        EnchanterRecipeBuilder builder = new EnchanterRecipeBuilder(catalyst, "transform", energy, processingTime);
        builder.input = Optional.of(input);
        builder.result = Optional.of(new ItemStack(output));
        builder.inputCount = inputCount;
        builder.outputCount = outputCount;
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
        return result.map(ItemStack::getItem).orElse(Items.ENCHANTED_BOOK);
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        output.accept(id, new EnchanterRecipe(catalyst, enchantMode, energy, processingTime,
                input, result, inputCount, outputCount), null);
    }
}
