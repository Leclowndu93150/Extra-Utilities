package com.leclowndu93150.extrautils2.gui.transfer;

import com.leclowndu93150.extrautils2.blockentity.transfer.ItemTransferNodeBlockEntity;
import com.leclowndu93150.extrautils2.gui.FilterSlot;
import com.leclowndu93150.extrautils2.gui.HasUpgradeSlot;
import com.leclowndu93150.extrautils2.gui.XUBaseMenu;
import com.leclowndu93150.extrautils2.registry.ModItems;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class TransferNodeMenu extends XUBaseMenu implements HasUpgradeSlot {
    public static final int GUI_W = 170;
    public static final int GUI_H = 197;
    public static final int TITLE_X = 5;
    public static final int TITLE_Y = 5;
    public static final int BUFFER_X = 76;
    public static final int BUFFER_Y = 31;
    public static final int FILTER_X = 4;
    public static final int FILTER_Y = 80;
    public static final int UPGRADE_CENTER_X = 85;
    public static final int UPGRADE_Y = 80;
    public static final int PING_X = 4;
    public static final int PING_Y = 68;
    public static final int TEXT_W = 154;
    public static final int TEXT_CENTER_X = PING_X + TEXT_W / 2;
    public static final int PLAYER_INV_X = 4;
    public static final int PLAYER_INV_Y = 102;
    private static final int PLAYER_INV_SLOT_X = 4;
    private static final int PLAYER_INV_SLOT_Y = 102;

    private static final int DATA_PING_X = 0;
    private static final int DATA_PING_Y = 1;
    private static final int DATA_PING_Z = 2;
    private static final int DATA_COUNT = 3;

    public final ItemTransferNodeBlockEntity tile;
    private final ContainerData data;
    private final int filterSlotIndex;
    private final int upgradeSlotStart;
    private final int upgradeSlotCount;

    public TransferNodeMenu(int id, Inventory playerInv, ItemTransferNodeBlockEntity tile) {
        super(ModMenus.TRANSFER_NODE.get(), id);
        this.tile = tile;
        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(data);

        addSlot(new SlotItemHandler(tile.getBuffer(), 0, menuSlotX(BUFFER_X), menuSlotY(BUFFER_Y)));
        this.filterSlotIndex = slots.size();
        addSlot(new FilterSlot(tile.getFilterSlot(), 0, menuSlotX(FILTER_X), menuSlotY(FILTER_Y),
                stack -> stack.is(ModItems.FILTER_ITEM.get()), ModItems.FILTER_ITEM.get()));

        this.upgradeSlotCount = tile.getUpgrades().getSlots();
        this.upgradeSlotStart = addUpgradeSlotsAndGetIndex(tile.getUpgrades(), this::getUpgradeX, this::getUpgradeY);

        addPlayerSlots(playerInv, PLAYER_INV_SLOT_X, PLAYER_INV_SLOT_Y);
    }

    public static TransferNodeMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof ItemTransferNodeBlockEntity tile) return new TransferNodeMenu(id, playerInv, tile);
        throw new IllegalStateException("Not an ItemTransferNodeBlockEntity");
    }

    @Override
    public void broadcastChanges() {
        BlockPos pingPos = tile.getCurrentPingPos();
        if (pingPos == null) {
            data.set(DATA_PING_X, 0);
            data.set(DATA_PING_Y, 0);
            data.set(DATA_PING_Z, 0);
        } else {
            BlockPos rel = pingPos.subtract(tile.getBlockPos());
            data.set(DATA_PING_X, rel.getX());
            data.set(DATA_PING_Y, rel.getY());
            data.set(DATA_PING_Z, rel.getZ());
        }
        super.broadcastChanges();
    }

    public Component getPingText() {
        return Component.literal("x = " + data.get(DATA_PING_X)
                + ", y = " + data.get(DATA_PING_Y)
                + ", z = " + data.get(DATA_PING_Z));
    }

    public int getFilterSlotIndex() {
        return filterSlotIndex;
    }

    @Override
    public int getUpgradeX() {
        return getUpgradeX(0);
    }

    @Override
    public int getUpgradeY() {
        return UPGRADE_Y;
    }

    @Override
    public int getUpgradeSlotCount() {
        return upgradeSlotCount;
    }

    @Override
    public int getUpgradeX(int index) {
        return getCenteredUpgradeX(UPGRADE_CENTER_X, upgradeSlotCount, index);
    }

    @Override
    public int getUpgradeY(int index) {
        return UPGRADE_Y;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int machineEnd = upgradeSlotStart + upgradeSlotCount;
        int playerStart = machineEnd;
        int playerEnd = slots.size();

        if (index < machineEnd) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (slots.get(filterSlotIndex).mayPlace(stack)) {
                if (!moveItemStackTo(stack, filterSlotIndex, filterSlotIndex + 1, false)) return ItemStack.EMPTY;
            } else if (moveItemStackTo(stack, upgradeSlotStart, machineEnd, false)) {
                // moved into upgrades
            } else if (!moveItemStackTo(stack, 0, 1, false)) {
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
}
