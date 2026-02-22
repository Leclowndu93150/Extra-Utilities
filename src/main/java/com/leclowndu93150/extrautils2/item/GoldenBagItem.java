package com.leclowndu93150.extrautils2.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class GoldenBagItem extends XUItem {

    private static final int SIZE = 54;

    public GoldenBagItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            int bagSlot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            player.openMenu(new BagMenuProvider(stack, bagSlot));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private class BagMenuProvider implements MenuProvider {
        private final ItemStack bag;
        private final int bagSlot;

        BagMenuProvider(ItemStack bag, int bagSlot) {
            this.bag = bag;
            this.bagSlot = bagSlot;
        }

        @Override
        public Component getDisplayName() {
            return bag.getHoverName();
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
            NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
            bag.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);

            Container bagContainer = new Container() {
                @Override public int getContainerSize() { return SIZE; }
                @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
                @Override public ItemStack getItem(int slot) { return items.get(slot); }
                @Override public ItemStack removeItem(int slot, int amount) {
                    ItemStack result = items.get(slot).split(amount);
                    setChanged();
                    return result;
                }
                @Override public ItemStack removeItemNoUpdate(int slot) {
                    ItemStack existing = items.get(slot);
                    items.set(slot, ItemStack.EMPTY);
                    return existing;
                }
                @Override public void setItem(int slot, ItemStack stack) {
                    items.set(slot, stack);
                    setChanged();
                }
                @Override public boolean stillValid(Player p) {
                    ItemStack held = bagSlot == 40
                            ? p.getInventory().offhand.get(0)
                            : p.getInventory().items.get(bagSlot);
                    return held.getItem() == GoldenBagItem.this;
                }
                @Override public void clearContent() { items.replaceAll(s -> ItemStack.EMPTY); }
                @Override public boolean canPlaceItem(int slot, ItemStack stack) {
                    return stack.getItem() != GoldenBagItem.this;
                }
                @Override public void setChanged() {
                    bag.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
                }
                @Override public void stopOpen(Player p) {
                    bag.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
                }
            };

            ChestMenu menu = new ChestMenu(MenuType.GENERIC_9x6, windowId, playerInventory, bagContainer, 6) {
                @Override
                public boolean stillValid(Player p) {
                    return bagContainer.stillValid(p);
                }
            };

            for (int i = 0; i < menu.slots.size(); i++) {
                Slot s = menu.slots.get(i);
                if (s.container == playerInventory && s.getContainerSlot() == bagSlot) {
                    final int x = s.x, y = s.y;
                    menu.slots.set(i, new Slot(playerInventory, bagSlot, x, y) {
                        @Override public boolean mayPickup(Player p) { return false; }
                        @Override public boolean mayPlace(ItemStack stack) { return false; }
                    });
                    break;
                }
            }

            return menu;
        }
    }
}
