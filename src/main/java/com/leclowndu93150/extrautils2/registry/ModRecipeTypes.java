package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.recipe.ResonatorRecipe;
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

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
