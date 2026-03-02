package com.leclowndu93150.extrautils2.gui.machine;

import com.leclowndu93150.extrautils2.blockentity.machine.AnalogCrafterBlockEntity;
import com.leclowndu93150.extrautils2.gui.*;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import com.leclowndu93150.extrautils2.util.RedstoneState;
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

import java.util.ArrayList;
import java.util.List;

public class AnalogCrafterMenu extends XUBaseMenu implements HasProgressArrow, HasRedstoneControl {

    public final AnalogCrafterBlockEntity tile;
    private final ContainerData data;

    private static final int DATA_PROGRESS = 0;
    private static final int DATA_MAX_PROGRESS = 1;
    private static final int DATA_REDSTONE_STATE = 2;
    private static final int DATA_STICKY = 3;
    private static final int DATA_SPREAD = 4;
    private static final int DATA_SLOT_SIDES_START = 5;
    private static final int DATA_COUNT = 14;

    public static final int BUTTON_REDSTONE = 1;
    public static final int BUTTON_STICKY = 2;
    public static final int BUTTON_SPREAD = 3;
    public static final int BUTTON_SLOT_SIDE_BASE = 10;
    public static final int BUTTON_ROW_SIDE_BASE = 20;
    public static final int BUTTON_SLOT_SIDE_REVERSE_BASE = 30;

    private static final int GRID_X = 4;
    private static final int GRID_Y = 18;
    private static final int ARROW_X = 58;
    private static final int ARROW_Y = 36;
    private static final int OUTPUT_X = 86;
    private static final int OUTPUT_Y = 36;

    private static final int SIDE_BTN_X = 112;
    private static final int SIDE_BTN_Y = 18;

    private static final int STICKY_X = 4;
    private static final int STICKY_Y = 76;
    private static final int SPREAD_X = 24;
    private static final int SPREAD_Y = 76;
    private static final int REDSTONE_X = 44;
    private static final int REDSTONE_Y = 76;

    private static final int PLAYER_INV_X = 7;
    private static final int PLAYER_INV_Y = 92;

    public AnalogCrafterMenu(int id, Inventory playerInv, AnalogCrafterBlockEntity tile) {
        super(ModMenus.ANALOG_CRAFTER.get(), id);
        this.tile = tile;
        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(data);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new SlotItemHandler(tile.getGrid(), row * 3 + col, menuSlotX(GRID_X + col * 18), menuSlotY(GRID_Y + row * 18)));
            }
        }

        addSlot(new SlotItemHandler(tile.getOutput(), 0, menuSlotX(OUTPUT_X), menuSlotY(OUTPUT_Y)) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static AnalogCrafterMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof AnalogCrafterBlockEntity tile) return new AnalogCrafterMenu(id, playerInv, tile);
        throw new IllegalStateException("Not an AnalogCrafterBlockEntity");
    }

    @Override
    public void broadcastChanges() {
        data.set(DATA_PROGRESS, tile.getProgress());
        data.set(DATA_MAX_PROGRESS, tile.getMaxProgress());
        data.set(DATA_REDSTONE_STATE, tile.getRedstoneState().ordinal());
        data.set(DATA_STICKY, tile.isSticky() ? 1 : 0);
        data.set(DATA_SPREAD, tile.isSpread() ? 1 : 0);
        byte[] sides = tile.getSlotSides();
        for (int i = 0; i < 9; i++) {
            data.set(DATA_SLOT_SIDES_START + i, sides[i]);
        }
        super.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_STICKY) {
            tile.toggleSticky();
            return true;
        }
        if (id == BUTTON_SPREAD) {
            tile.toggleSpread();
            return true;
        }
        if (id >= BUTTON_SLOT_SIDE_BASE && id < BUTTON_SLOT_SIDE_BASE + 9) {
            tile.cycleSlotSide(id - BUTTON_SLOT_SIDE_BASE);
            return true;
        }
        if (id >= BUTTON_ROW_SIDE_BASE && id < BUTTON_ROW_SIDE_BASE + 3) {
            int row = id - BUTTON_ROW_SIDE_BASE;
            tile.cycleSlotSide(row * 3);
            byte val = tile.getSlotSides()[row * 3];
            tile.getSlotSides()[row * 3 + 1] = val;
            tile.getSlotSides()[row * 3 + 2] = val;
            tile.setChanged();
            return true;
        }
        if (id >= BUTTON_SLOT_SIDE_REVERSE_BASE && id < BUTTON_SLOT_SIDE_REVERSE_BASE + 9) {
            tile.cycleSlotSideReverse(id - BUTTON_SLOT_SIDE_REVERSE_BASE);
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override public int getArrowX() { return ARROW_X; }
    @Override public int getArrowY() { return ARROW_Y; }

    @Override
    public float getArrowProgress() {
        int max = data.get(DATA_MAX_PROGRESS);
        if (max <= 0) return 0f;
        return (float) data.get(DATA_PROGRESS) / max;
    }

    @Override public boolean isArrowOverloaded() { return false; }

    @Override
    public List<Component> getArrowTooltip() {
        List<Component> lines = new ArrayList<>();
        int max = data.get(DATA_MAX_PROGRESS);
        if (max > 0) lines.add(Component.literal(data.get(DATA_PROGRESS) + " / " + max + " ticks"));
        return lines;
    }

    @Override
    public List<Component> getArrowErrorTooltip() { return List.of(); }

    @Override public int getRedstoneX() { return REDSTONE_X; }
    @Override public int getRedstoneY() { return REDSTONE_Y; }
    @Override public int getRedstoneButtonId() { return BUTTON_REDSTONE; }

    @Override
    public RedstoneState getRedstoneState() {
        int v = data.get(DATA_REDSTONE_STATE);
        RedstoneState[] values = RedstoneState.values();
        return v >= 0 && v < values.length ? values[v] : RedstoneState.OPERATE_ALWAYS;
    }

    @Override public boolean hasRedstonePulseMode() { return true; }
    @Override public void cycleRedstone() { tile.cycleRedstoneState(true); }

    public int getPlayerInvX() { return PLAYER_INV_X; }
    public int getPlayerInvY() { return PLAYER_INV_Y; }
    public int getGridX() { return GRID_X; }
    public int getGridY() { return GRID_Y; }
    public int getOutputSlotX() { return OUTPUT_X; }
    public int getOutputSlotY() { return OUTPUT_Y; }
    public int getSideBtnX() { return SIDE_BTN_X; }
    public int getSideBtnY() { return SIDE_BTN_Y; }
    public int getStickyX() { return STICKY_X; }
    public int getStickyY() { return STICKY_Y; }
    public int getSpreadX() { return SPREAD_X; }
    public int getSpreadY() { return SPREAD_Y; }
    public boolean isSticky() { return data.get(DATA_STICKY) == 1; }
    public boolean isSpread() { return data.get(DATA_SPREAD) == 1; }

    public int getSlotSide(int slot) {
        if (slot < 0 || slot >= 9) return AnalogCrafterBlockEntity.ANY_FACE;
        return data.get(DATA_SLOT_SIDES_START + slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        int gridEnd = 9;
        int outputEnd = 10;
        int playerStart = 10;
        int playerEnd = slots.size();

        if (index < outputEnd) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, gridEnd, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }

    @Override
    public boolean stillValid(Player player) { return tile.stillValid(player); }
}
