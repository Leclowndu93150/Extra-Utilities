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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record EnchanterRecipe(
        Ingredient catalyst,
        String enchantMode,
        int energy,
        int processingTime,
        Optional<Ingredient> input,
        Optional<ItemStack> result,
        int inputCount,
        int outputCount
) implements Recipe<EnchanterRecipe.EnchanterInput> {

    public EnchanterRecipe(Ingredient catalyst, String enchantMode, int energy, int processingTime) {
        this(catalyst, enchantMode, energy, processingTime, Optional.empty(), Optional.empty(), 1, 1);
    }

    @Override
    public boolean matches(EnchanterInput container, Level level) {
        if (!catalyst.test(container.catalyst())) return false;
        ItemStack item = container.item();
        if (item.isEmpty()) return false;

        if (isTransformation()) {
            return input.get().test(item) && item.getCount() >= inputCount;
        }
        return item.is(Items.BOOK) || item.isEnchantable();
    }

    @Override
    public ItemStack assemble(EnchanterInput container, HolderLookup.Provider provider) {
        if (isTransformation()) {
            ItemStack out = result.get().copy();
            out.setCount(outputCount);
            return out;
        }
        return container.item().copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        if (isTransformation()) {
            ItemStack out = result.get().copy();
            out.setCount(outputCount);
            return out;
        }
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

    public boolean isTransformation() {
        return input.isPresent() && result.isPresent();
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
                Codec.INT.fieldOf("processing_time").forGetter(EnchanterRecipe::processingTime),
                Ingredient.CODEC.optionalFieldOf("input").forGetter(EnchanterRecipe::input),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("result").forGetter(EnchanterRecipe::result),
                Codec.INT.optionalFieldOf("input_count", 1).forGetter(EnchanterRecipe::inputCount),
                Codec.INT.optionalFieldOf("output_count", 1).forGetter(EnchanterRecipe::outputCount)
        ).apply(inst, EnchanterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchanterRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public EnchanterRecipe decode(RegistryFriendlyByteBuf buf) {
                Ingredient catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                String enchantMode = ByteBufCodecs.STRING_UTF8.decode(buf);
                int energy = buf.readInt();
                int processingTime = buf.readInt();
                Optional<Ingredient> input = ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC).decode(buf);
                Optional<ItemStack> result = ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(buf);
                int inputCount = buf.readInt();
                int outputCount = buf.readInt();
                return new EnchanterRecipe(catalyst, enchantMode, energy, processingTime, input, result, inputCount, outputCount);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, EnchanterRecipe recipe) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.catalyst());
                ByteBufCodecs.STRING_UTF8.encode(buf, recipe.enchantMode());
                buf.writeInt(recipe.energy());
                buf.writeInt(recipe.processingTime());
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC).encode(buf, recipe.input());
                ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(buf, recipe.result());
                buf.writeInt(recipe.inputCount());
                buf.writeInt(recipe.outputCount());
            }
        };

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
