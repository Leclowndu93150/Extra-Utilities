package com.leclowndu93150.extrautils2.upgrade;

import com.leclowndu93150.extrautils2.item.UpgradeItem;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class UpgradeStackHandler extends ItemStackHandler {
    private final Runnable onChange;
    private final EnumSet<UpgradeType> allowed;

    public UpgradeStackHandler(EnumSet<UpgradeType> allowed, Runnable onChange) {
        super(1);
        this.allowed = allowed;
        this.onChange = onChange;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (onChange != null) onChange.run();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!(stack.getItem() instanceof UpgradeItem item)) return false;
        return allowed.contains(item.getType());
    }

    @Override
    protected int getStackLimit(int slot, ItemStack stack) {
        return stack.getMaxStackSize();
    }

    public int getLevel(UpgradeType type) {
        ItemStack stack = getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof UpgradeItem item)) return 0;
        if (item.getType() != type) return 0;
        return Math.min(stack.getCount(), stack.getMaxStackSize());
    }
}
