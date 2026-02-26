package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.recipe.CrusherRecipe;
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

public class CrusherCategory extends AbstractRecipeCategory<RecipeHolder<CrusherRecipe>> {

    private static final int W = 140;
    private static final int H = 40;

    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;

    public CrusherCategory(RecipeType<RecipeHolder<CrusherRecipe>> recipeType, IGuiHelper guiHelper) {
        super(recipeType,
                ModBlocks.MACHINE_CRUSHER.get().getName(),
                guiHelper.createDrawableItemLike(ModBlocks.MACHINE_CRUSHER.get()),
                W, H);
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createDrawable(
                ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png"),
                79, 35, 24, 17);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CrusherRecipe> holder, IFocusGroup focuses) {
        CrusherRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, W - 39, 5)
                .addItemStack(recipe.output());
        if (!recipe.secondaryOutput().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, W - 17, 5)
                    .addItemStack(recipe.secondaryOutput())
                    .addRichTooltipCallback((view, tooltip) ->
                            tooltip.add(Component.literal(String.format("%.0f%% chance", recipe.secondaryChance() * 100))));
        }
    }

    @Override
    public void draw(RecipeHolder<CrusherRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        slot.draw(graphics, 0, 4);
        slot.draw(graphics, W - 40, 4);
        if (!holder.value().secondaryOutput().isEmpty()) {
            slot.draw(graphics, W - 18, 4);
        }
        arrow.draw(graphics, (W - 24) / 2 - 10, 5);

        CrusherRecipe recipe = holder.value();
        int ticks = recipe.processingTime();
        String info = StringHelper.formatDurationSeconds(ticks, false);
        var font = Minecraft.getInstance().font;
        int textW = font.width(info);
        graphics.drawString(font, info, (W - textW) / 2, H - 9, Color.GRAY.getRGB(), false);
    }
}
