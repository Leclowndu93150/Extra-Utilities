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
import net.minecraft.world.level.Level;

public record EnchanterRecipe(
        Ingredient catalyst,
        String enchantMode,
        int energy,
        int processingTime
) implements Recipe<EnchanterRecipe.EnchanterInput> {

    @Override
    public boolean matches(EnchanterInput container, Level level) {
        return catalyst.test(container.catalyst());
    }

    @Override
    public ItemStack assemble(EnchanterInput container, HolderLookup.Provider provider) {
        return container.item().copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.MACHINE_ENCHANTER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ENCHANTER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ENCHANTER.get();
    }

    public boolean isLowest() {
        return "lowest".equals(enchantMode);
    }

    public record EnchanterInput(ItemStack item, ItemStack catalyst) implements net.minecraft.world.item.crafting.RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? item : catalyst;
        }

        @Override
        public int size() {
            return 2;
        }
    }

    public static class Serializer implements RecipeSerializer<EnchanterRecipe> {
        public static final MapCodec<EnchanterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("catalyst").forGetter(EnchanterRecipe::catalyst),
                Codec.STRING.fieldOf("enchant_mode").forGetter(EnchanterRecipe::enchantMode),
                Codec.INT.fieldOf("energy").forGetter(EnchanterRecipe::energy),
                Codec.INT.fieldOf("processing_time").forGetter(EnchanterRecipe::processingTime)
        ).apply(inst, EnchanterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchanterRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, EnchanterRecipe::catalyst,
                ByteBufCodecs.STRING_UTF8, EnchanterRecipe::enchantMode,
                ByteBufCodecs.INT, EnchanterRecipe::energy,
                ByteBufCodecs.INT, EnchanterRecipe::processingTime,
                EnchanterRecipe::new
        );

        @Override
        public MapCodec<EnchanterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchanterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
