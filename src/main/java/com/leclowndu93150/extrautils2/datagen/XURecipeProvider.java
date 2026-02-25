package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class XURecipeProvider extends RecipeProvider {
    public XURecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ANGEL_BLOCK.get())
                .pattern("SSS")
                .pattern("SCS")
                .pattern("SSS")
                .define('S', Items.STONE)
                .define('C', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.SPIKE_WOOD.get(), 4)
                .pattern(" S ")
                .pattern("SIS")
                .pattern("IBI")
                .define('S', Items.WOODEN_SWORD)
                .define('I', ItemTags.PLANKS)
                .define('B', Items.OAK_LOG)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.SPIKE_STONE.get(), 4)
                .pattern(" S ")
                .pattern("SIS")
                .pattern("IBI")
                .define('S', Items.STONE_SWORD)
                .define('I', Items.COBBLESTONE)
                .define('B', Items.COBBLESTONE)
                .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.SPIKE_IRON.get(), 4)
                .pattern(" S ")
                .pattern("SIS")
                .pattern("IBI")
                .define('S', Items.IRON_SWORD)
                .define('I', Items.IRON_INGOT)
                .define('B', Items.IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.SPIKE_GOLD.get(), 4)
                .pattern(" S ")
                .pattern("SIS")
                .pattern("IBI")
                .define('S', Items.GOLDEN_SWORD)
                .define('I', Items.GOLD_INGOT)
                .define('B', Items.GOLD_BLOCK)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModBlocks.SPIKE_DIAMOND.get(), 4)
                .pattern(" S ")
                .pattern("SIS")
                .pattern("IBI")
                .define('S', Items.DIAMOND_SWORD)
                .define('I', Items.DIAMOND)
                .define('B', Items.DIAMOND_BLOCK)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(output);

        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_1.get(), Items.COBBLESTONE);
        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_2.get(), ModBlocks.COMPRESSED_COBBLESTONE_1.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_3.get(), ModBlocks.COMPRESSED_COBBLESTONE_2.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_4.get(), ModBlocks.COMPRESSED_COBBLESTONE_3.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_5.get(), ModBlocks.COMPRESSED_COBBLESTONE_4.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_6.get(), ModBlocks.COMPRESSED_COBBLESTONE_5.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_7.get(), ModBlocks.COMPRESSED_COBBLESTONE_6.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_COBBLESTONE_8.get(), ModBlocks.COMPRESSED_COBBLESTONE_7.get().asItem());

        compressedRecipe(output, ModBlocks.COMPRESSED_DIRT_1.get(), Items.DIRT);
        compressedRecipe(output, ModBlocks.COMPRESSED_DIRT_2.get(), ModBlocks.COMPRESSED_DIRT_1.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_DIRT_3.get(), ModBlocks.COMPRESSED_DIRT_2.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_DIRT_4.get(), ModBlocks.COMPRESSED_DIRT_3.get().asItem());

        compressedRecipe(output, ModBlocks.COMPRESSED_SAND_1.get(), Items.SAND);
        compressedRecipe(output, ModBlocks.COMPRESSED_SAND_2.get(), ModBlocks.COMPRESSED_SAND_1.get().asItem());

        compressedRecipe(output, ModBlocks.COMPRESSED_GRAVEL_1.get(), Items.GRAVEL);
        compressedRecipe(output, ModBlocks.COMPRESSED_GRAVEL_2.get(), ModBlocks.COMPRESSED_GRAVEL_1.get().asItem());

        compressedRecipe(output, ModBlocks.COMPRESSED_NETHERRACK_1.get(), Items.NETHERRACK);
        compressedRecipe(output, ModBlocks.COMPRESSED_NETHERRACK_2.get(), ModBlocks.COMPRESSED_NETHERRACK_1.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_NETHERRACK_3.get(), ModBlocks.COMPRESSED_NETHERRACK_2.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_NETHERRACK_4.get(), ModBlocks.COMPRESSED_NETHERRACK_3.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_NETHERRACK_5.get(), ModBlocks.COMPRESSED_NETHERRACK_4.get().asItem());
        compressedRecipe(output, ModBlocks.COMPRESSED_NETHERRACK_6.get(), ModBlocks.COMPRESSED_NETHERRACK_5.get().asItem());

        opiniumRecipes(output);
        resonatorRecipes(output);
    }

    private void opiniumRecipes(RecipeOutput output) {
        ItemLike[] tierMaterials = {
                Items.CHARCOAL,
                Items.IRON_BLOCK,
                Items.GOLD_BLOCK,
                Items.DIAMOND_BLOCK,
                Items.EMERALD_BLOCK,
                Items.CHORUS_FLOWER,
                Items.EXPERIENCE_BOTTLE,
                Items.ELYTRA,
                Items.NETHER_STAR,
                Items.IRON_INGOT
        };

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.OPINIUM_CORE_0.get())
                .pattern(" o ")
                .pattern("oio")
                .pattern(" o ")
                .define('o', tierMaterials[0])
                .define('i', tierMaterials[1])
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .save(output);

        for (int i = 1; i <= 8; i++) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.getOpiniumCore(i).get())
                    .pattern(" o ")
                    .pattern("mim")
                    .pattern(" o ")
                    .define('o', ModItems.getOpiniumCore(i - 1).get())
                    .define('m', tierMaterials[i])
                    .define('i', tierMaterials[i + 1])
                    .unlockedBy("has_prev_core", has(ModItems.getOpiniumCore(i - 1).get()))
                    .save(output);
        }
    }

    private void resonatorRecipes(RecipeOutput output) {
        ResonatorRecipeBuilder.resonator(
                Ingredient.of(Items.STONE),
                ModBlocks.MOON_STONE.get(), 800
        ).save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "resonator/moon_stone"));
    }

    private void compressedRecipe(RecipeOutput output, net.minecraft.world.level.block.Block result, net.minecraft.world.item.Item ingredient) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ingredient)
                .unlockedBy("has_ingredient", has(ingredient))
                .save(output);
    }
}
