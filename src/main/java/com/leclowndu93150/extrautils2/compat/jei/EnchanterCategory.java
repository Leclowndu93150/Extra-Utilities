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
import java.util.Arrays;
import java.util.List;

public class EnchanterCategory extends AbstractRecipeCategory<RecipeHolder<EnchanterRecipe>> {

    private static final int W = 140;
    private static final int H = 50;

    private static final int SLOT1_X = 25;
    private static final int SLOT2_X = 47;
    private static final int ARROW_X = 69;
    private static final int SLOT3_X = 97;
    private static final int SLOT_Y = 5;

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

        if (recipe.isTransformation()) {
            var inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, SLOT1_X + 1, SLOT_Y);
            List<ItemStack> inputItems = Arrays.asList(recipe.input().get().getItems());
            if (recipe.inputCount() > 1) {
                inputItems = inputItems.stream()
                        .map(s -> { ItemStack c = s.copy(); c.setCount(recipe.inputCount()); return c; })
                        .toList();
            }
            inputSlot.addItemStacks(inputItems);

            builder.addSlot(RecipeIngredientRole.CATALYST, SLOT2_X + 1, SLOT_Y)
                    .addIngredients(recipe.catalyst());

            ItemStack out = recipe.result().get().copy();
            out.setCount(recipe.outputCount());
            builder.addSlot(RecipeIngredientRole.OUTPUT, SLOT3_X + 1, SLOT_Y)
                    .addItemStack(out);
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, SLOT1_X + 1, SLOT_Y)
                    .addItemStacks(List.of(
                            new ItemStack(Items.BOOK),
                            new ItemStack(Items.IRON_SWORD),
                            new ItemStack(Items.IRON_PICKAXE),
                            new ItemStack(Items.IRON_CHESTPLATE),
                            new ItemStack(Items.IRON_HELMET),
                            new ItemStack(Items.IRON_BOOTS),
                            new ItemStack(Items.DIAMOND_SWORD),
                            new ItemStack(Items.DIAMOND_PICKAXE),
                            new ItemStack(Items.DIAMOND_CHESTPLATE),
                            new ItemStack(Items.BOW),
                            new ItemStack(Items.FISHING_ROD),
                            new ItemStack(Items.TRIDENT)
                    ))
                    .addRichTooltipCallback((view, tooltip) ->
                            tooltip.add(Component.literal("Any enchantable item or Book")));

            builder.addSlot(RecipeIngredientRole.CATALYST, SLOT2_X + 1, SLOT_Y)
                    .addIngredients(recipe.catalyst());

            builder.addSlot(RecipeIngredientRole.OUTPUT, SLOT3_X + 1, SLOT_Y)
                    .addItemStack(new ItemStack(Items.ENCHANTED_BOOK))
                    .addRichTooltipCallback((view, tooltip) ->
                            tooltip.add(Component.literal(recipe.isLowest() ? "Lowest enchant levels" : "Highest enchant levels")));
        }
    }

    @Override
    public void draw(RecipeHolder<EnchanterRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        slot.draw(graphics, SLOT1_X, SLOT_Y - 1);
        slot.draw(graphics, SLOT2_X, SLOT_Y - 1);
        slot.draw(graphics, SLOT3_X, SLOT_Y - 1);
        arrow.draw(graphics, ARROW_X, SLOT_Y);

        EnchanterRecipe recipe = holder.value();
        int ticks = recipe.processingTime();
        String info = StringHelper.formatDurationSeconds(ticks, false);
        var font = Minecraft.getInstance().font;
        int textW = font.width(info);
        graphics.drawString(font, info, (W - textW) / 2, 28, Color.GRAY.getRGB(), false);

        if (!recipe.isTransformation()) {
            String mode = recipe.isLowest() ? "Lowest Enchants" : "Highest Enchants";
            int modeW = font.width(mode);
            graphics.drawString(font, mode, (W - modeW) / 2, H - 9, Color.GRAY.getRGB(), false);
        }
    }
}
