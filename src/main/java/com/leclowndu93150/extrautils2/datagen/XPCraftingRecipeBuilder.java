package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.recipe.XPCraftingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class XPCraftingRecipeBuilder implements RecipeBuilder {

    private final ItemStack result;
    private final int xpCost;
    private final List<Ingredient> ingredients = new ArrayList<>();

    private XPCraftingRecipeBuilder(ItemLike result, int count, int xpCost) {
        this.result = new ItemStack(result, count);
        this.xpCost = xpCost;
    }

    public static XPCraftingRecipeBuilder shapeless(ItemLike result, int count, int xpCost) {
        return new XPCraftingRecipeBuilder(result, count, xpCost);
    }

    public static XPCraftingRecipeBuilder shapeless(ItemLike result, int xpCost) {
        return new XPCraftingRecipeBuilder(result, 1, xpCost);
    }

    public XPCraftingRecipeBuilder requires(ItemLike item) {
        ingredients.add(Ingredient.of(item));
        return this;
    }

    public XPCraftingRecipeBuilder requires(Ingredient ingredient) {
        ingredients.add(ingredient);
        return this;
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
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        output.accept(id, new XPCraftingRecipe("", CraftingBookCategory.MISC, result, list, xpCost), null);
    }
}
