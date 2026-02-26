package com.leclowndu93150.extrautils2.recipe;

import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record CrusherRecipe(
        Ingredient input,
        ItemStack output,
        ItemStack secondaryOutput,
        float secondaryChance,
        int energy,
        int processingTime
) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput container, Level level) {
        return input.test(container.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput container, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.MACHINE_CRUSHER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CRUSHER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CRUSHER.get();
    }

    public static class Serializer implements RecipeSerializer<CrusherRecipe> {
        public static final MapCodec<CrusherRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(CrusherRecipe::input),
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter(CrusherRecipe::output),
                ItemStack.STRICT_CODEC.optionalFieldOf("secondary_output", ItemStack.EMPTY).forGetter(CrusherRecipe::secondaryOutput),
                Codec.FLOAT.optionalFieldOf("secondary_chance", 0f).forGetter(CrusherRecipe::secondaryChance),
                Codec.INT.fieldOf("energy").forGetter(CrusherRecipe::energy),
                Codec.INT.fieldOf("processing_time").forGetter(CrusherRecipe::processingTime)
        ).apply(inst, CrusherRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CrusherRecipe::input,
                ItemStack.STREAM_CODEC, CrusherRecipe::output,
                ItemStack.OPTIONAL_STREAM_CODEC, CrusherRecipe::secondaryOutput,
                ByteBufCodecs.FLOAT, CrusherRecipe::secondaryChance,
                ByteBufCodecs.INT, CrusherRecipe::energy,
                ByteBufCodecs.INT, CrusherRecipe::processingTime,
                CrusherRecipe::new
        );

        @Override
        public MapCodec<CrusherRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
