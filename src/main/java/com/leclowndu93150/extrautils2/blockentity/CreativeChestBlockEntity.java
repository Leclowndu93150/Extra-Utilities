package com.leclowndu93150.extrautils2.blockentity;

import com.leclowndu93150.extrautils2.gui.CreativeChestMenu;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class CreativeChestBlockEntity extends XUBlockEntity implements MenuProvider {

    private ItemStack heldStack = ItemStack.EMPTY;

    private final IItemHandler handler = new IItemHandler() {
        @Override public int getSlots() { return 1; }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (heldStack.isEmpty()) return ItemStack.EMPTY;
            ItemStack display = heldStack.copy();
            display.setCount(display.getMaxStackSize());
            return display;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (heldStack.isEmpty()) return ItemStack.EMPTY;
            ItemStack result = heldStack.copy();
            result.setCount(Math.min(amount, result.getMaxStackSize()));
            return result;
        }

        @Override public int getSlotLimit(int slot) { return 64; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };

    public CreativeChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_CHEST.get(), pos, state);
    }

    public ItemStack getHeldStack() { return heldStack; }

    public void setHeldStack(ItemStack stack) {
        this.heldStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        setChanged();
        sync();
    }

    public IItemHandler getHandler() { return handler; }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.extrautils2.creative_chest");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CreativeChestMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!heldStack.isEmpty()) tag.put("HeldStack", heldStack.save(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        heldStack = tag.contains("HeldStack")
                ? ItemStack.parse(provider, tag.getCompound("HeldStack")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }
}
