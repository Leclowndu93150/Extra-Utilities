package com.leclowndu93150.extrautils2.gui.filter;

import com.leclowndu93150.extrautils2.gui.GhostSlot;
import com.leclowndu93150.extrautils2.gui.XUBaseMenu;
import com.leclowndu93150.extrautils2.item.ItemFilterFluids;
import com.leclowndu93150.extrautils2.registry.ModItems;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FluidFilterMenu extends XUBaseMenu {
    public static final int GUI_W = 170;
    public static final int GUI_H = 223;
    public static final int GRID_X = 49;
    public static final int GRID_Y = 32;
    public static final int PLAYER_INV_X = 4;
    public static final int PLAYER_INV_Y = 128;
    public static final int BUTTON_W = 78;
    public static final int BUTTON_H = 18;
    public static final int LEFT_BUTTON_X = 5;
    public static final int RIGHT_BUTTON_X = 87;
    public static final int BUTTON_ROW_Y = 108;
    private static final int PLAYER_INV_SLOT_X = 4;
    private static final int PLAYER_INV_SLOT_Y = 128;

    private final InteractionHand hand;
    private final Inventory playerInventory;
    private final ItemStackHandler ghosts = new ItemStackHandler(ItemFilterFluids.NUM_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            ItemFilterFluids.setGhostStack(getHeldStack(), slot, getStackInSlot(slot));
        }
    };

    public FluidFilterMenu(int id, Inventory playerInventory, InteractionHand hand) {
        super(ModMenus.FLUID_FILTER.get(), id);
        this.hand = hand;
        this.playerInventory = playerInventory;

        ItemStack held = getHeldStack();
        for (int i = 0; i < ItemFilterFluids.NUM_SLOTS; i++) {
            ghosts.setStackInSlot(i, ItemFilterFluids.getGhostStack(held, i));
        }

        for (int i = 0; i < ItemFilterFluids.NUM_SLOTS; i++) {
            int x = GRID_X + (i % 4) * 18;
            int y = GRID_Y + (i / 4) * 18;
            addSlot(new GhostSlot(ghosts, i, menuSlotX(x), menuSlotY(y)));
        }

        addPlayerSlots(playerInventory, PLAYER_INV_SLOT_X, PLAYER_INV_SLOT_Y);
        lockHeldSlotIfPresent();
    }

    public static FluidFilterMenu fromNetwork(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        return new FluidFilterMenu(id, playerInventory, buf.readEnum(InteractionHand.class));
    }

    private void lockHeldSlotIfPresent() {
        if (hand != InteractionHand.MAIN_HAND) return;
        int lockedSlot = playerInventory.selected;
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.container == playerInventory && slot.getContainerSlot() == lockedSlot) {
                int x = slot.x;
                int y = slot.y;
                slots.set(i, new Slot(playerInventory, lockedSlot, x, y) {
                    @Override public boolean mayPickup(Player player) { return false; }
                    @Override public boolean mayPlace(ItemStack stack) { return false; }
                });
                break;
            }
        }
    }

    public ItemStack getHeldStack() {
        return hand == InteractionHand.MAIN_HAND
                ? playerInventory.items.get(playerInventory.selected)
                : playerInventory.offhand.get(0);
    }

    public Component getButtonLabel(int id) {
        ItemFilterFluids.Flag flag = ItemFilterFluids.Flag.values()[id - 1];
        return Component.translatable(isFlagEnabled(flag) ? flag.onKey : flag.offKey);
    }

    public boolean isFlagEnabled(ItemFilterFluids.Flag flag) {
        return ItemFilterFluids.getFlag(getHeldStack(), flag);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 1 && id <= ItemFilterFluids.Flag.values().length) {
            ItemFilterFluids.Flag flag = ItemFilterFluids.Flag.values()[id - 1];
            ItemFilterFluids.setFlag(getHeldStack(), flag, !isFlagEnabled(flag));
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < ItemFilterFluids.NUM_SLOTS) {
            ItemStack carried = getCarried();
            if (carried.isEmpty() || carried.is(ModItems.FILTER_FLUID.get()) || FluidUtil.getFluidContained(carried).isPresent()) {
                ghosts.setStackInSlot(slotId, carried.isEmpty() ? ItemStack.EMPTY : carried.copyWithCount(1));
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < ItemFilterFluids.NUM_SLOTS) {
            ghosts.setStackInSlot(index, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        if (!stack.is(ModItems.FILTER_FLUID.get()) && FluidUtil.getFluidContained(stack).isEmpty()) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < ItemFilterFluids.NUM_SLOTS; i++) {
            if (ghosts.getStackInSlot(i).isEmpty()) {
                ghosts.setStackInSlot(i, stack.copyWithCount(1));
                break;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack held = getHeldStack();
        return !held.isEmpty() && held.is(ModItems.FILTER_FLUID.get());
    }
}
