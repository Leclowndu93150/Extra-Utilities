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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

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

        lassoRecipes(output);
        opiniumRecipes(output);
        resonatorRecipes(output);
        crusherRecipes(output);
        enchanterRecipes(output);
        generatorFuelRecipes(output);
    }

    private void lassoRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GOLDEN_LASSO.get())
                .pattern("GSG")
                .pattern("S S")
                .pattern("GSG")
                .define('G', Items.GOLD_NUGGET)
                .define('S', Items.STRING)
                .unlockedBy("has_gold_nugget", has(Items.GOLD_NUGGET))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.CURSED_LASSO.get())
                .requires(ModItems.GOLDEN_LASSO.get())
                .requires(ModItems.EVIL_DROP.get())
                .unlockedBy("has_golden_lasso", has(ModItems.GOLDEN_LASSO.get()))
                .save(output);
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

    private void crusherRecipes(RecipeOutput output) {
        CrusherRecipeBuilder.crusher(Ingredient.of(Items.BLAZE_ROD), Items.BLAZE_POWDER, 2, 4000, 200)
                .secondary(Items.BLAZE_POWDER, 3, 0.4f)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/blaze_rod"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.BONE), Items.BONE_MEAL, 3, 4000, 200)
                .secondary(Items.BONE_MEAL, 3, 0.5f)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/bone"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.COBBLESTONE), Items.GRAVEL, 1, 4000, 200)
                .secondary(Items.SAND, 1, 0.1f)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/cobblestone"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.GRAVEL), Items.SAND, 1, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/gravel"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.GLOWSTONE), Items.GLOWSTONE_DUST, 4, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/glowstone"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.COAL_ORE), Items.COAL, 4, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/coal_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.DIAMOND_ORE), Items.DIAMOND, 1, 4000, 200)
                .secondary(Items.DIAMOND, 3, 0.2f)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/diamond_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.EMERALD_ORE), Items.EMERALD, 1, 4000, 200)
                .secondary(Items.EMERALD, 3, 0.2f)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/emerald_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.LAPIS_ORE), Items.LAPIS_LAZULI, 8, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/lapis_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.REDSTONE_ORE), Items.REDSTONE, 8, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/redstone_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.NETHER_QUARTZ_ORE), Items.QUARTZ, 1, 4000, 200)
                .secondary(Items.QUARTZ, 3, 0.2f)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/quartz_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.IRON_ORE), Items.RAW_IRON, 2, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/iron_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.GOLD_ORE), Items.RAW_GOLD, 2, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/gold_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.COPPER_ORE), Items.RAW_COPPER, 6, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/copper_ore"));

        CrusherRecipeBuilder.crusher(Ingredient.of(ItemTags.WOOL), Items.STRING, 3, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/wool"));

        CrusherRecipeBuilder.crusher(Ingredient.of(Items.BEETROOT), Items.RED_DYE, 2, 4000, 200)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "crusher/beetroot"));
    }

    private void enchanterRecipes(RecipeOutput output) {
        EnchanterRecipeBuilder.enchanter(Ingredient.of(Items.LAPIS_LAZULI), "lowest", 12000, 6000)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "enchanter/lapis"));

        EnchanterRecipeBuilder.enchanter(Ingredient.of(Items.NETHER_STAR), "highest", 72000, 36000)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "enchanter/nether_star"));
    }

    private void generatorFuelRecipes(RecipeOutput output) {
        GeneratorFuelRecipeBuilder.fuel("tnt", Ingredient.of(Blocks.TNT), 512000, 160)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/tnt/tnt_block"));
        GeneratorFuelRecipeBuilder.fuel("tnt", Ingredient.of(Items.GUNPOWDER), 64000, 160)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/tnt/gunpowder"));

        GeneratorFuelRecipeBuilder.fluidFuel("lava", new FluidStack(Fluids.LAVA, 50), 5000, 40)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/lava/lava"));

        GeneratorFuelRecipeBuilder.fuel("netherstar", Ingredient.of(Items.NETHER_STAR), 9600000, 4000)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/netherstar/nether_star"));

        GeneratorFuelRecipeBuilder.fuel("ender", Ingredient.of(Items.ENDER_PEARL), 64000, 40)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/ender/ender_pearl"));
        GeneratorFuelRecipeBuilder.fuel("ender", Ingredient.of(Items.ENDER_EYE), 256000, 80)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/ender/ender_eye"));

        GeneratorFuelRecipeBuilder.fuel("redstone", Ingredient.of(Items.REDSTONE), 20000, 160)
                .withFluid(new FluidStack(Fluids.LAVA, 50))
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/redstone/redstone_lava"));

        GeneratorFuelRecipeBuilder.fuel("dragon", Ingredient.of(Items.DRAGON_BREATH), 480000, 40)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/dragon/dragon_breath"));

        GeneratorFuelRecipeBuilder.fuel("ice", Ingredient.of(Blocks.ICE), 1600, 40)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/ice/ice"));
        GeneratorFuelRecipeBuilder.fuel("ice", Ingredient.of(Blocks.PACKED_ICE), 1600, 40)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/ice/packed_ice"));
        GeneratorFuelRecipeBuilder.fuel("ice", Ingredient.of(Items.SNOWBALL), 200, 40)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/ice/snowball"));
        GeneratorFuelRecipeBuilder.fuel("ice", Ingredient.of(Blocks.SNOW_BLOCK), 800, 40)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/ice/snow_block"));

        GeneratorFuelRecipeBuilder.fuel("death", Ingredient.of(Items.BONE), 16000, 1000)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/death/bone"));
        GeneratorFuelRecipeBuilder.fuel("death", Ingredient.of(Blocks.BONE_BLOCK), 48000, 1000)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/death/bone_block"));
        GeneratorFuelRecipeBuilder.fuel("death", Ingredient.of(Items.ROTTEN_FLESH), 8000, 1000)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/death/rotten_flesh"));
        GeneratorFuelRecipeBuilder.fuel("death", Ingredient.of(Items.WITHER_SKELETON_SKULL), 60000, 1000)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/death/wither_skull"));

        GeneratorFuelRecipeBuilder.dualFuel("slime", Ingredient.of(Items.SLIME_BALL), 4,
                        Ingredient.of(Items.MILK_BUCKET), 1, 192000, 400)
                .save(output, ResourceLocation.fromNamespaceAndPath("extrautils2", "generator/slime/slimeball_milk"));
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
