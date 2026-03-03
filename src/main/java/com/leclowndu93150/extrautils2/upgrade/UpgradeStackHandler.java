package com.leclowndu93150.extrautils2.upgrade;

import com.leclowndu93150.extrautils2.item.UpgradeItem;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class UpgradeStackHandler extends ItemStackHandler {
    private final Runnable onChange;
    private final EnumSet<UpgradeType> allowed;

    public UpgradeStackHandler(EnumSet<UpgradeType> allowed, Runnable onChange) {
        this(1, allowed, onChange);
    }

    public UpgradeStackHandler(int slots, EnumSet<UpgradeType> allowed, Runnable onChange) {
        super(slots);
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
        if (!allowed.contains(item.getType())) return false;

        for (int i = 0; i < getSlots(); i++) {
            if (i == slot) continue;
            ItemStack existing = getStackInSlot(i);
            if (existing.isEmpty()) continue;
            if (existing.getItem() instanceof UpgradeItem other && other.getType() == item.getType()) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected int getStackLimit(int slot, ItemStack stack) {
        return stack.getMaxStackSize();
    }

    public int getLevel(UpgradeType type) {
        int level = 0;
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof UpgradeItem item)) continue;
            if (item.getType() != type) continue;
            level += Math.min(stack.getCount(), stack.getMaxStackSize());
        }
        return Math.min(level, type.maxLevel);
    }
}
