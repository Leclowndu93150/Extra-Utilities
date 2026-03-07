package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.blockentity.LargishChestBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LargishChestMenu extends XUBaseMenu {
    public static final int GUI_W = 176;
    public static final int GUI_H = 166;
    public static final int SLOT_START_X = 7;
    public static final int SLOT_START_Y = 18;
    public static final int PLAYER_INV_X = 7;
    public static final int PLAYER_INV_Y = 84;
    private static final int CONTAINER_SLOTS = 27;

    private final LargishChestBlockEntity tile;

    public LargishChestMenu(int id, Inventory playerInv, LargishChestBlockEntity tile) {
        super(ModMenus.LARGISH_CHEST.get(), id);
        this.tile = tile;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new SlotItemHandler(tile.getInventory(), row * 9 + col,
                        menuSlotX(SLOT_START_X + col * 18), menuSlotY(SLOT_START_Y + row * 18)));
            }
        }

        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static LargishChestMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof LargishChestBlockEntity tile) return new LargishChestMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a LargishChestBlockEntity at that position");
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < CONTAINER_SLOTS) {
            if (!moveItemStackTo(stack, CONTAINER_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, CONTAINER_SLOTS, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile.stillValid(player);
    }
}
