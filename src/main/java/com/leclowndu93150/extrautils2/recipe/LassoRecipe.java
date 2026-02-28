package com.leclowndu93150.extrautils2.recipe;

import com.leclowndu93150.extrautils2.item.GoldenLassoItem;
import com.leclowndu93150.extrautils2.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class LassoRecipe extends ShapedRecipe {

    private final String requiredEntity;
    private final ShapedRecipePattern storedPattern;
    private final ItemStack storedResult;

    public LassoRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, String requiredEntity) {
        super(group, category, pattern, result);
        this.requiredEntity = requiredEntity;
        this.storedPattern = pattern;
        this.storedResult = result;
    }

    public String getRequiredEntity() {
        return requiredEntity;
    }

    public ShapedRecipePattern getPattern() {
        return storedPattern;
    }

    public ItemStack getStoredResult() {
        return storedResult;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!super.matches(input, level)) return false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof GoldenLassoItem) {
                CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                if (!data.contains(GoldenLassoItem.TAG_ENTITY)) return false;
                CompoundTag tag = data.copyTag();
                CompoundTag entityTag = tag.getCompound(GoldenLassoItem.TAG_ENTITY);
                String id = entityTag.getString("id");
                if (id.equals(requiredEntity)) return true;
            }
        }
        return false;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof GoldenLassoItem) {
                ItemStack emptyLasso = new ItemStack(stack.getItem());
                remaining.set(i, emptyLasso);
            } else if (stack.getItem().hasCraftingRemainingItem()) {
                remaining.set(i, new ItemStack(stack.getItem().getCraftingRemainingItem()));
            }
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.LASSO_RECIPE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<LassoRecipe> {
        public static final MapCodec<LassoRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(LassoRecipe::category),
                ShapedRecipePattern.MAP_CODEC.forGetter(LassoRecipe::getPattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(LassoRecipe::getStoredResult),
                Codec.STRING.fieldOf("required_entity").forGetter(LassoRecipe::getRequiredEntity)
        ).apply(inst, LassoRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LassoRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapedRecipe::getGroup,
                CraftingBookCategory.STREAM_CODEC, LassoRecipe::category,
                ShapedRecipePattern.STREAM_CODEC, LassoRecipe::getPattern,
                ItemStack.STREAM_CODEC, LassoRecipe::getStoredResult,
                ByteBufCodecs.STRING_UTF8, LassoRecipe::getRequiredEntity,
                LassoRecipe::new
        );

        @Override
        public MapCodec<LassoRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LassoRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
