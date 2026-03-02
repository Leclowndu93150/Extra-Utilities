package com.leclowndu93150.extrautils2.gui.machine;

import com.leclowndu93150.extrautils2.blockentity.machine.CrafterBlockEntity;
import com.leclowndu93150.extrautils2.gui.*;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class CrafterMenu extends XUBaseMenu implements HasUpgradeSlot, HasProgressArrow, HasRedstoneControl, HasEnergyBar {

    public final CrafterBlockEntity tile;
    private final ContainerData data;
    private final int upgradeSlotIndex;

    private static final int DATA_COOLDOWN = 0;
    private static final int DATA_ENERGY = 1;
    private static final int DATA_ENERGY_CAP = 2;
    private static final int DATA_GP_POWERED = 3;
    private static final int DATA_REDSTONE_STATE = 4;
    private static final int DATA_SPEED_LEVEL = 5;
    private static final int DATA_CONTAINER_MODE = 6;
    private static final int DATA_COUNT = 7;

    private static final int GHOST_X = 19;
    private static final int GHOST_Y = 18;
    private static final int ARROW_X = 73;
    private static final int ARROW_Y = 36;
    private static final int GHOST_OUTPUT_X = 101;
    private static final int GHOST_OUTPUT_Y = 36;

    private static final int RIGHT_COL_X = 140;
    private static final int REDSTONE_Y = 18;
    private static final int UPGRADE_X = 140;
    private static final int UPGRADE_Y = 54;
    private static final int CONTAINER_MODE_Y = 36;
    private static final int ENERGY_X = 122;
    private static final int ENERGY_Y = 18;

    private static final int INPUT_X = 7;
    private static final int INPUT_Y = 84;
    private static final int OUTPUT_X = 7;
    private static final int OUTPUT_Y = 116;
    private static final int PLAYER_INV_X = 7;
    private static final int PLAYER_INV_Y = 134;
    private static final int CONTAINER_MODE_BUTTON_ID = 2;

    public CrafterMenu(int id, Inventory playerInv, CrafterBlockEntity tile) {
        super(ModMenus.CRAFTER.get(), id);
        this.tile = tile;
        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(data);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new GhostSlot(tile.getGhostSlots(), row * 3 + col, menuSlotX(GHOST_X + col * 18), menuSlotY(GHOST_Y + row * 18)));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(tile.getInput(), i, menuSlotX(INPUT_X + i * 18), menuSlotY(INPUT_Y)));
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(tile.getOutput(), i, menuSlotX(OUTPUT_X + i * 18), menuSlotY(OUTPUT_Y)) {
                @Override public boolean mayPlace(ItemStack stack) { return false; }
            });
        }

        this.upgradeSlotIndex = addUpgradeSlotAndGetIndex(tile.getUpgrades(), UPGRADE_X, UPGRADE_Y);
        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static CrafterMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof CrafterBlockEntity tile) return new CrafterMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a CrafterBlockEntity");
    }

    @Override
    public void broadcastChanges() {
        data.set(DATA_COOLDOWN, tile.getCooldown());
        data.set(DATA_ENERGY, tile.getEnergyStorage().getEnergyStored());
        data.set(DATA_ENERGY_CAP, tile.getEnergyStorage().getMaxEnergyStored());
        data.set(DATA_GP_POWERED, tile.isGpPowered() ? 1 : 0);
        data.set(DATA_REDSTONE_STATE, tile.getRedstoneState().ordinal());
        data.set(DATA_SPEED_LEVEL, tile.getSpeedLevel());
        data.set(DATA_CONTAINER_MODE, tile.isContainerItemsToOutput() ? 1 : 0);
        super.broadcastChanges();
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < 9) {
            ItemStack carried = getCarried();
            if (carried.isEmpty()) {
                tile.getGhostSlots().setStackInSlot(slotId, ItemStack.EMPTY);
            } else {
                ItemStack ghost = carried.copy();
                ghost.setCount(1);
                tile.getGhostSlots().setStackInSlot(slotId, ghost);
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == CONTAINER_MODE_BUTTON_ID) {
            tile.toggleContainerMode();
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override public int getArrowX() { return ARROW_X; }
    @Override public int getArrowY() { return ARROW_Y; }

    @Override
    public float getArrowProgress() {
        return (float) data.get(DATA_COOLDOWN) / 20f;
    }

    @Override public boolean isArrowOverloaded() { return data.get(DATA_GP_POWERED) == 0; }

    @Override
    public List<Component> getArrowTooltip() {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(data.get(DATA_COOLDOWN) + " / 20 ticks"));
        return lines;
    }

    @Override
    public List<Component> getArrowErrorTooltip() {
        return List.of(Component.translatable("tooltip.extrautils2.grid_overloaded"));
    }

    @Override public int getUpgradeX() { return UPGRADE_X; }
    @Override public int getUpgradeY() { return UPGRADE_Y; }
    @Override public int getRedstoneX() { return RIGHT_COL_X; }
    @Override public int getRedstoneY() { return REDSTONE_Y; }
    @Override public int getRedstoneButtonId() { return 1; }

    @Override
    public RedstoneState getRedstoneState() {
        int v = data.get(DATA_REDSTONE_STATE);
        RedstoneState[] values = RedstoneState.values();
        return v >= 0 && v < values.length ? values[v] : RedstoneState.OPERATE_ALWAYS;
    }

    @Override public boolean hasRedstonePulseMode() { return true; }
    @Override public void cycleRedstone() { tile.cycleRedstoneState(true); }

    @Override public int getEnergyBarX() { return ENERGY_X; }
    @Override public int getEnergyBarY() { return ENERGY_Y; }
    @Override public int getEnergyStored() { return data.get(DATA_ENERGY); }
    @Override public int getEnergyCapacity() { return data.get(DATA_ENERGY_CAP); }

    public int getPlayerInvX() { return PLAYER_INV_X; }
    public int getPlayerInvY() { return PLAYER_INV_Y; }
    public int getContainerMode() { return data.get(DATA_CONTAINER_MODE); }
    public int getContainerModeButtonId() { return CONTAINER_MODE_BUTTON_ID; }
    public int getContainerModeX() { return RIGHT_COL_X; }
    public int getContainerModeY() { return CONTAINER_MODE_Y; }
    public int getGhostOutputX() { return GHOST_OUTPUT_X; }
    public int getGhostOutputY() { return GHOST_OUTPUT_Y; }
    public int getGhostX() { return GHOST_X; }
    public int getGhostY() { return GHOST_Y; }
    public int getInputX() { return INPUT_X; }
    public int getInputY() { return INPUT_Y; }
    public int getOutputX() { return OUTPUT_X; }
    public int getOutputY() { return OUTPUT_Y; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        int ghostEnd = 9;
        int inputStart = 9;
        int inputEnd = 18;
        int outputEnd = 27;
        int upgradeIdx = 27;
        int playerStart = 28;
        int playerEnd = slots.size();

        if (index < ghostEnd) {
            slot.set(ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

        if (index >= inputStart && index < outputEnd) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (index >= playerStart) {
            if (slots.get(upgradeIdx).mayPlace(stack)) {
                if (!moveItemStackTo(stack, upgradeIdx, upgradeIdx + 1, false))
                    if (!moveItemStackTo(stack, inputStart, inputEnd, false)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, inputStart, inputEnd, false)) return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }

    @Override
    public boolean stillValid(Player player) { return tile.stillValid(player); }
}
