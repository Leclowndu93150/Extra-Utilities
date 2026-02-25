package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.blockentity.CreativeChestBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CreativeChestMenu extends XUBaseMenu implements HasProgressArrow {

    public final CreativeChestBlockEntity tile;

    private static final int INPUT_X = 50;
    private static final int OUTPUT_X = 102;
    private static final int SLOT_Y = 32;
    private static final int PLAYER_INV_X = 7;
    private static final int PLAYER_INV_Y = 71;

    public CreativeChestMenu(int id, Inventory playerInv, CreativeChestBlockEntity tile) {
        super(ModMenus.CREATIVE_CHEST.get(), id);
        this.tile = tile;

        addSlot(new Slot(new SimpleItemContainer(tile), 0, menuSlotX(INPUT_X), menuSlotY(SLOT_Y)) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return playerInv.player.isCreative();
            }
        });

        addSlot(new Slot(new InfiniteOutputContainer(tile), 0, menuSlotX(OUTPUT_X), menuSlotY(SLOT_Y)) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static CreativeChestMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof CreativeChestBlockEntity tile) return new CreativeChestMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a CreativeChestBlockEntity");
    }

    private static final int ARROW_X = 72;
    private static final int ARROW_Y = 33;

    @Override public int getArrowX() { return ARROW_X; }
    @Override public int getArrowY() { return ARROW_Y; }
    @Override public float getArrowProgress() { return tile.getHeldStack().isEmpty() ? 0f : 1f; }
    @Override public boolean isArrowOverloaded() { return false; }
    @Override public java.util.List<Component> getArrowTooltip() { return java.util.List.of(); }
    @Override public java.util.List<Component> getArrowErrorTooltip() { return java.util.List.of(); }

    public int getPlayerInvX() { return PLAYER_INV_X; }
    public int getPlayerInvY() { return PLAYER_INV_Y; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        int playerStart = 2;
        int playerEnd = slots.size();

        if (index == 0) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (index == 1) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (player.isCreative()) {
                if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile.stillValid(player);
    }

    private static class SimpleItemContainer implements net.minecraft.world.Container {
        private final CreativeChestBlockEntity tile;
        SimpleItemContainer(CreativeChestBlockEntity tile) { this.tile = tile; }

        @Override public int getContainerSize() { return 1; }
        @Override public boolean isEmpty() { return tile.getHeldStack().isEmpty(); }
        @Override public ItemStack getItem(int slot) { return tile.getHeldStack(); }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack old = tile.getHeldStack();
            tile.setHeldStack(ItemStack.EMPTY);
            return old;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack old = tile.getHeldStack();
            tile.setHeldStack(ItemStack.EMPTY);
            return old;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            tile.setHeldStack(stack);
        }

        @Override public void setChanged() {}
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { tile.setHeldStack(ItemStack.EMPTY); }
    }

    private static class InfiniteOutputContainer implements net.minecraft.world.Container {
        private final CreativeChestBlockEntity tile;
        InfiniteOutputContainer(CreativeChestBlockEntity tile) { this.tile = tile; }

        @Override public int getContainerSize() { return 1; }
        @Override public boolean isEmpty() { return tile.getHeldStack().isEmpty(); }

        @Override
        public ItemStack getItem(int slot) {
            if (tile.getHeldStack().isEmpty()) return ItemStack.EMPTY;
            ItemStack display = tile.getHeldStack().copy();
            display.setCount(display.getMaxStackSize());
            return display;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            if (tile.getHeldStack().isEmpty()) return ItemStack.EMPTY;
            ItemStack result = tile.getHeldStack().copy();
            result.setCount(Math.min(amount, result.getMaxStackSize()));
            return result;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return removeItem(0, 64);
        }

        @Override public void setItem(int slot, ItemStack stack) {}
        @Override public void setChanged() {}
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() {}
    }
}
