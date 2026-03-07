package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.blockentity.MiniChestBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MiniChestMenu extends XUBaseMenu {
    public static final int GUI_W = 176;
    public static final int GUI_H = 130;
    public static final int SLOT_X = 79;
    public static final int SLOT_Y = 20;
    public static final int PLAYER_INV_X = 7;
    public static final int PLAYER_INV_Y = 35;

    private final MiniChestBlockEntity tile;

    public MiniChestMenu(int id, Inventory playerInv, MiniChestBlockEntity tile) {
        super(ModMenus.MINI_CHEST.get(), id);
        this.tile = tile;

        addSlot(new SlotItemHandler(tile.getInventory(), 0, menuSlotX(SLOT_X), menuSlotY(SLOT_Y)));
        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static MiniChestMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof MiniChestBlockEntity tile) return new MiniChestMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a MiniChestBlockEntity at that position");
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == 0) {
            if (!moveItemStackTo(stack, 1, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 1, false)) {
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
