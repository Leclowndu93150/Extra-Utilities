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

public record ResonatorRecipe(Ingredient input, ItemStack output, int energy) implements Recipe<SingleRecipeInput> {

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
        return new ItemStack(ModBlocks.RESONATOR.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.RESONATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.RESONATOR.get();
    }

    public static class Serializer implements RecipeSerializer<ResonatorRecipe> {
        public static final MapCodec<ResonatorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(ResonatorRecipe::input),
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter(ResonatorRecipe::output),
                Codec.INT.fieldOf("energy").forGetter(ResonatorRecipe::energy)
        ).apply(inst, ResonatorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ResonatorRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ResonatorRecipe::input,
                ItemStack.STREAM_CODEC, ResonatorRecipe::output,
                ByteBufCodecs.INT, ResonatorRecipe::energy,
                ResonatorRecipe::new
        );

        @Override
        public MapCodec<ResonatorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ResonatorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
