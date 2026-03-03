package com.leclowndu93150.extrautils2.recipe;

import com.leclowndu93150.extrautils2.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

public class XPCraftingRecipe extends ShapelessRecipe {

    private final int xpCost;

    public XPCraftingRecipe(String group, CraftingBookCategory category,
                            ItemStack result, NonNullList<Ingredient> ingredients, int xpCost) {
        super(group, category, result, ingredients);
        this.xpCost = xpCost;
    }

    public int getXpCost() {
        return xpCost;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        Player player = CommonHooks.getCraftingPlayer();
        if (player != null && !player.isCreative() && player.experienceLevel < xpCost) {
            return ItemStack.EMPTY;
        }
        return super.assemble(input, provider);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.XP_CRAFTING_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<XPCraftingRecipe> {

        private static final MapCodec<XPCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(XPCraftingRecipe::getGroup),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.getResultItem(null)),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap(
                        list -> {
                            NonNullList<Ingredient> nonnull = NonNullList.create();
                            nonnull.addAll(list);
                            return nonnull;
                        },
                        list -> list.stream().toList()
                ).forGetter(ShapelessRecipe::getIngredients),
                Codec.INT.fieldOf("xp_cost").forGetter(XPCraftingRecipe::getXpCost)
        ).apply(inst, XPCraftingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, XPCraftingRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public XPCraftingRecipe decode(RegistryFriendlyByteBuf buf) {
                String group = ByteBufCodecs.STRING_UTF8.decode(buf);
                CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                int ingredientCount = buf.readVarInt();
                NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
                for (int i = 0; i < ingredientCount; i++) {
                    ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                }
                ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                int xpCost = buf.readVarInt();
                return new XPCraftingRecipe(group, category, result, ingredients, xpCost);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, XPCraftingRecipe recipe) {
                ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getGroup());
                buf.writeEnum(recipe.category());
                buf.writeVarInt(recipe.getIngredients().size());
                for (Ingredient ingredient : recipe.getIngredients()) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                }
                ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
                buf.writeVarInt(recipe.getXpCost());
            }
        };

        @Override
        public MapCodec<XPCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, XPCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
