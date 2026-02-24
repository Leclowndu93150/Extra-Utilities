package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.block.TrashCanBlock;
import com.leclowndu93150.extrautils2.blockentity.TrashCanBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class TrashCanMenu extends XUBaseMenu implements HasFluidBar {

    public final TrashCanBlockEntity tile;
    private static final int GUI_W = 176;
    private static final int PLAYER_INV_X = (GUI_W - 162) / 2;
    private static final int PLAYER_INV_Y = 166 - 95;
    private static final int FLUID_BAR_X = 80;
    private static final int FLUID_BAR_Y = 25;
    private static final int BUTTON_FLUID = 1;

    public TrashCanMenu(int id, Inventory playerInv, TrashCanBlockEntity tile) {
        super(ModMenus.TRASH_CAN.get(), id);
        this.tile = tile;

        if (tile.getTrashType() == TrashCanBlock.TrashType.ITEM) {
            addSlot(new SlotItemHandler(tile.itemHandler, 0, menuSlotX(80), menuSlotY(35)));
        }

        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static TrashCanMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof TrashCanBlockEntity tile) return new TrashCanMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a TrashCanBlockEntity at that position");
    }

    @Override
    public int getFluidBarX() { return FLUID_BAR_X; }

    @Override
    public int getFluidBarY() { return FLUID_BAR_Y; }

    @Override
    public int getFluidAmount() { return 0; }

    @Override
    public int getFluidCapacity() { return Integer.MAX_VALUE; }

    @Override
    public FluidStack getFluidStack() { return FluidStack.EMPTY; }

    @Override
    public int getFluidBarButtonId() { return BUTTON_FLUID; }

    @Override
    public @Nullable IFluidHandler getFluidHandler() {
        return tile.getTrashType() == TrashCanBlock.TrashType.FLUID ? tile.fluidHandler : null;
    }

    @Override
    public void onFluidChanged() { }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();

        if (tile.getTrashType() == TrashCanBlock.TrashType.ITEM && index == 0) {
            return ItemStack.EMPTY;
        }

        if (tile.getTrashType() == TrashCanBlock.TrashType.ITEM) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile.stillValid(player);
    }
}
