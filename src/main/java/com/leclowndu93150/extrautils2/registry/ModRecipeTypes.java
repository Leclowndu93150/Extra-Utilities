package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.recipe.CrusherRecipe;
import com.leclowndu93150.extrautils2.recipe.EnchanterRecipe;
import com.leclowndu93150.extrautils2.recipe.GeneratorFuelRecipe;
import com.leclowndu93150.extrautils2.recipe.LassoRecipe;
import com.leclowndu93150.extrautils2.recipe.ResonatorRecipe;
import com.leclowndu93150.extrautils2.recipe.XPCraftingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ExtraUtilities.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ExtraUtilities.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ResonatorRecipe>> RESONATOR =
            RECIPE_TYPES.register("resonator", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "extrautils2:resonator";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, ResonatorRecipe.Serializer> RESONATOR_SERIALIZER =
            RECIPE_SERIALIZERS.register("resonator", ResonatorRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrusherRecipe>> CRUSHER =
            RECIPE_TYPES.register("crusher", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "extrautils2:crusher";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, CrusherRecipe.Serializer> CRUSHER_SERIALIZER =
            RECIPE_SERIALIZERS.register("crusher", CrusherRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<EnchanterRecipe>> ENCHANTER =
            RECIPE_TYPES.register("enchanter", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "extrautils2:enchanter";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, EnchanterRecipe.Serializer> ENCHANTER_SERIALIZER =
            RECIPE_SERIALIZERS.register("enchanter", EnchanterRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<GeneratorFuelRecipe>> GENERATOR_FUEL =
            RECIPE_TYPES.register("generator_fuel", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "extrautils2:generator_fuel";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, GeneratorFuelRecipe.Serializer> GENERATOR_FUEL_SERIALIZER =
            RECIPE_SERIALIZERS.register("generator_fuel", GeneratorFuelRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, LassoRecipe.Serializer> LASSO_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("lasso_recipe", LassoRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, XPCraftingRecipe.Serializer> XP_CRAFTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("xp_crafting", XPCraftingRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
