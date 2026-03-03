package com.leclowndu93150.extrautils2.api.items;

import net.minecraft.world.item.ItemStack;

public interface IItemFilter {
    boolean isItemFilter(ItemStack filterStack);

    boolean matches(ItemStack filterStack, ItemStack target);
}
