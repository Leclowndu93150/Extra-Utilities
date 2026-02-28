package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.util.StringHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;

import java.awt.*;
import java.util.List;

public class GeneratorFuelCategory extends AbstractRecipeCategory<GeneratorFuelDisplay> {

    private static final int W = 150;
    private static final int H = 40;

    private final IDrawableStatic slot;

    public GeneratorFuelCategory(RecipeType<GeneratorFuelDisplay> recipeType, Block catalystBlock, IGuiHelper guiHelper) {
        super(recipeType,
                catalystBlock.getName(),
                guiHelper.createDrawableItemLike(catalystBlock),
                W, H);
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GeneratorFuelDisplay recipe, IFocusGroup focuses) {
        List<ItemStack> inputs = recipe.inputs();
        FluidStack fluid = recipe.fluidInput();
        boolean hasFluid = !fluid.isEmpty();
        int slotCount = inputs.size() + (hasFluid ? 1 : 0);

        int totalWidth = slotCount * 18 + (slotCount - 1) * 2;
        int startX = (W - totalWidth) / 2;

        for (int i = 0; i < inputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, startX + i * 20 + 1, 1)
                    .addItemStack(inputs.get(i));
        }

        if (hasFluid) {
            int fluidX = startX + inputs.size() * 20;
            builder.addSlot(RecipeIngredientRole.INPUT, fluidX + 1, 1)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluid)
                    .setFluidRenderer(1000, false, 16, 16);
        }
    }

    @Override
    public void draw(GeneratorFuelDisplay recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        List<ItemStack> inputs = recipe.inputs();
        FluidStack fluid = recipe.fluidInput();
        boolean hasFluid = !fluid.isEmpty();
        int slotCount = inputs.size() + (hasFluid ? 1 : 0);

        int totalWidth = slotCount * 18 + (slotCount - 1) * 2;
        int startX = (W - totalWidth) / 2;

        for (int i = 0; i < slotCount; i++) {
            slot.draw(graphics, startX + i * 20, 0);
        }

        int rate = recipe.energyPerTick();
        int time = recipe.totalEnergy() / Math.max(1, rate);
        String info = StringHelper.format(recipe.totalEnergy()) + "RF "
                + StringHelper.formatDurationSeconds(time, false) + " "
                + StringHelper.format(rate) + "RF/T";

        var font = Minecraft.getInstance().font;
        int textW = font.width(info);
        graphics.drawString(font, info, (W - textW) / 2, H - 9, Color.GRAY.getRGB(), false);
    }
}
