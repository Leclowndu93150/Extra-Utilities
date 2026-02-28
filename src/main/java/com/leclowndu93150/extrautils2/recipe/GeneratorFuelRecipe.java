package com.leclowndu93150.extrautils2.recipe;

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
import net.neoforged.neoforge.fluids.FluidStack;

public record GeneratorFuelRecipe(
        String generatorType,
        Ingredient input,
        int inputCount,
        Ingredient secondaryInput,
        int secondaryCount,
        FluidStack fluidInput,
        int totalEnergy,
        int energyPerTick
) implements Recipe<GeneratorFuelRecipe.GeneratorInput> {

    @Override
    public boolean matches(GeneratorInput container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(GeneratorInput container, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
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
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.GENERATOR_FUEL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.GENERATOR_FUEL.get();
    }

    public boolean matchesGeneratorAndInputs(String genType, ItemStack primary, ItemStack secondary, FluidStack fluid) {
        if (!this.generatorType.equals(genType)) return false;

        if (input != null && !input.isEmpty()) {
            if (!input.test(primary) || primary.getCount() < inputCount) return false;
        }

        if (secondaryInput != null && !secondaryInput.isEmpty()) {
            if (!secondaryInput.test(secondary) || secondary.getCount() < secondaryCount) return false;
        }

        if (!fluidInput.isEmpty()) {
            if (fluid.isEmpty() || !FluidStack.isSameFluidSameComponents(fluid, fluidInput) || fluid.getAmount() < fluidInput.getAmount()) return false;
        }

        return true;
    }

    public record GeneratorInput(ItemStack primary, ItemStack secondary, FluidStack fluid) implements net.minecraft.world.item.crafting.RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> primary;
                case 1 -> secondary;
                default -> ItemStack.EMPTY;
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }

    public static class Serializer implements RecipeSerializer<GeneratorFuelRecipe> {
        public static final MapCodec<GeneratorFuelRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.fieldOf("generator_type").forGetter(GeneratorFuelRecipe::generatorType),
                Ingredient.CODEC.optionalFieldOf("input", Ingredient.EMPTY).forGetter(GeneratorFuelRecipe::input),
                Codec.INT.optionalFieldOf("input_count", 1).forGetter(GeneratorFuelRecipe::inputCount),
                Ingredient.CODEC.optionalFieldOf("secondary_input", Ingredient.EMPTY).forGetter(GeneratorFuelRecipe::secondaryInput),
                Codec.INT.optionalFieldOf("secondary_count", 0).forGetter(GeneratorFuelRecipe::secondaryCount),
                FluidStack.OPTIONAL_CODEC.optionalFieldOf("fluid_input", FluidStack.EMPTY).forGetter(GeneratorFuelRecipe::fluidInput),
                Codec.INT.fieldOf("total_energy").forGetter(GeneratorFuelRecipe::totalEnergy),
                Codec.INT.fieldOf("energy_per_tick").forGetter(GeneratorFuelRecipe::energyPerTick)
        ).apply(inst, GeneratorFuelRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorFuelRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public GeneratorFuelRecipe decode(RegistryFriendlyByteBuf buf) {
                String genType = ByteBufCodecs.STRING_UTF8.decode(buf);
                Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                int inputCount = buf.readVarInt();
                Ingredient secondary = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                int secondaryCount = buf.readVarInt();
                FluidStack fluid = FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
                int totalEnergy = buf.readVarInt();
                int energyPerTick = buf.readVarInt();
                return new GeneratorFuelRecipe(genType, input, inputCount, secondary, secondaryCount, fluid, totalEnergy, energyPerTick);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, GeneratorFuelRecipe recipe) {
                ByteBufCodecs.STRING_UTF8.encode(buf, recipe.generatorType);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
                buf.writeVarInt(recipe.inputCount);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.secondaryInput);
                buf.writeVarInt(recipe.secondaryCount);
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.fluidInput);
                buf.writeVarInt(recipe.totalEnergy);
                buf.writeVarInt(recipe.energyPerTick);
            }
        };

        @Override
        public MapCodec<GeneratorFuelRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GeneratorFuelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
