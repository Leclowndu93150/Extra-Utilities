package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.recipe.EnchanterRecipe;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.util.StringHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.awt.*;

public class EnchanterCategory extends AbstractRecipeCategory<RecipeHolder<EnchanterRecipe>> {

    private static final int W = 140;
    private static final int H = 50;

    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;

    public EnchanterCategory(RecipeType<RecipeHolder<EnchanterRecipe>> recipeType, IGuiHelper guiHelper) {
        super(recipeType,
                ModBlocks.MACHINE_ENCHANTER.get().getName(),
                guiHelper.createDrawableItemLike(ModBlocks.MACHINE_ENCHANTER.get()),
                W, H);
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createDrawable(
                ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"),
                79, 35, 24, 17);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EnchanterRecipe> holder, IFocusGroup focuses) {
        EnchanterRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5)
                .addItemStack(new ItemStack(Items.BOOK))
                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.literal("Any enchantable item or Book")));
        builder.addSlot(RecipeIngredientRole.CATALYST, 23, 5)
                .addIngredients(recipe.catalyst());
        builder.addSlot(RecipeIngredientRole.OUTPUT, W - 17, 5)
                .addItemStack(new ItemStack(Items.ENCHANTED_BOOK))
                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.literal(recipe.isLowest() ? "Lowest enchant levels" : "Highest enchant levels")));
    }

    @Override
    public void draw(RecipeHolder<EnchanterRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        slot.draw(graphics, 0, 4);
        slot.draw(graphics, 22, 4);
        slot.draw(graphics, W - 18, 4);
        arrow.draw(graphics, (W - 24) / 2, 5);

        EnchanterRecipe recipe = holder.value();
        int ticks = recipe.processingTime();
        String info = StringHelper.formatDurationSeconds(ticks, false);
        var font = Minecraft.getInstance().font;
        int textW = font.width(info);
        graphics.drawString(font, info, (W - textW) / 2, 28, Color.GRAY.getRGB(), false);

        String mode = recipe.isLowest() ? "Lowest Enchants" : "Highest Enchants";
        int modeW = font.width(mode);
        graphics.drawString(font, mode, (W - modeW) / 2, H - 9, Color.GRAY.getRGB(), false);
    }
}
