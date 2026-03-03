package com.leclowndu93150.extrautils2.gui;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Predicate;

public class FilterSlot extends SlotItemHandler {
    private final Predicate<ItemStack> validator;
    private final Item expectedItem;

    public FilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> validator, Item expectedItem) {
        super(itemHandler, index, xPosition, yPosition);
        this.validator = validator;
        this.expectedItem = expectedItem;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return validator.test(stack) && super.mayPlace(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    public Item getExpectedItem() {
        return expectedItem;
    }
}
