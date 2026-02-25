package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.recipe.ResonatorRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;

import java.awt.*;

public class ResonatorCategory extends AbstractRecipeCategory<RecipeHolder<ResonatorRecipe>> {

    private static final int W = 120;
    private static final int H = 40;

    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;

    public ResonatorCategory(RecipeType<RecipeHolder<ResonatorRecipe>> recipeType, IGuiHelper guiHelper) {
        super(recipeType,
                ModBlocks.RESONATOR.get().getName(),
                guiHelper.createDrawableItemLike(ModBlocks.RESONATOR.get()),
                W, H);
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createDrawable(
                ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"),
                79, 35, 24, 17);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ResonatorRecipe> holder, IFocusGroup focuses) {
        ResonatorRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, W - 17, 5)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(RecipeHolder<ResonatorRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        slot.draw(graphics, 0, 4);
        slot.draw(graphics, W - 18, 4);
        arrow.draw(graphics, (W - 24) / 2, 5);

        ResonatorRecipe recipe = holder.value();
        int ticks = recipe.energy() / 4;
        String info = StringHelper.formatDurationSeconds(ticks, false);
        var font = Minecraft.getInstance().font;
        int textW = font.width(info);
        graphics.drawString(font, info, (W - textW) / 2, H - 9, Color.GRAY.getRGB(), false);
    }
}
