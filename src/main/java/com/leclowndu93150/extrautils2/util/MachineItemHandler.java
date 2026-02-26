package com.leclowndu93150.extrautils2.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class MachineItemHandler implements IItemHandlerModifiable {

    private final IItemHandlerModifiable[] inputs;
    private final IItemHandlerModifiable[] outputs;
    private final int totalSlots;
    private final int inputSlots;

    public MachineItemHandler(IItemHandlerModifiable[] inputs, IItemHandlerModifiable[] outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        int ins = 0;
        for (IItemHandler h : inputs) ins += h.getSlots();
        this.inputSlots = ins;
        int outs = 0;
        for (IItemHandler h : outputs) outs += h.getSlots();
        this.totalSlots = ins + outs;
    }

    @Override
    public int getSlots() {
        return totalSlots;
    }

    private SlotRef resolve(int slot) {
        int idx = slot;
        for (IItemHandlerModifiable h : inputs) {
            if (idx < h.getSlots()) return new SlotRef(h, idx, true);
            idx -= h.getSlots();
        }
        for (IItemHandlerModifiable h : outputs) {
            if (idx < h.getSlots()) return new SlotRef(h, idx, false);
            idx -= h.getSlots();
        }
        throw new IndexOutOfBoundsException("Slot " + slot + " out of range [0," + totalSlots + ")");
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        SlotRef ref = resolve(slot);
        return ref.handler.getStackInSlot(ref.index);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        SlotRef ref = resolve(slot);
        if (!ref.isInput) return stack;
        return ref.handler.insertItem(ref.index, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        SlotRef ref = resolve(slot);
        if (ref.isInput) return ItemStack.EMPTY;
        return ref.handler.extractItem(ref.index, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        SlotRef ref = resolve(slot);
        return ref.handler.getSlotLimit(ref.index);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        SlotRef ref = resolve(slot);
        if (!ref.isInput) return false;
        return ref.handler.isItemValid(ref.index, stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        SlotRef ref = resolve(slot);
        ref.handler.setStackInSlot(ref.index, stack);
    }

    private record SlotRef(IItemHandlerModifiable handler, int index, boolean isInput) {}
}
