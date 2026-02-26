package com.leclowndu93150.extrautils2.gui.machine;

import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.blockentity.machine.CrusherBlockEntity;
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

public class CrusherMenu extends XUBaseMenu implements HasUpgradeSlot, HasProgressArrow, HasRedstoneControl, HasEnergyBar, HasMachinePreview {

    public final CrusherBlockEntity tile;
    private final ContainerData data;
    private final int upgradeSlotIndex;

    private static final int DATA_PROGRESS = 0;
    private static final int DATA_TOTAL_TIME = 1;
    private static final int DATA_ENERGY = 2;
    private static final int DATA_ENERGY_CAP = 3;
    private static final int DATA_GP_POWERED = 4;
    private static final int DATA_REDSTONE_STATE = 5;
    private static final int DATA_SPEED_LEVEL = 6;
    private static final int DATA_COUNT = 7;

    private static final int INPUT_X = 40;
    private static final int SLOT_Y = 32;
    private static final int OUTPUT_X = 92;
    private static final int SECONDARY_X = 114;
    private static final int ARROW_X = 62;
    private static final int UPGRADE_X = 4;
    private static final int UPGRADE_Y = 16;
    private static final int REDSTONE_X = 4;
    private static final int REDSTONE_Y = 35;
    private static final int ENERGY_X = 152;
    private static final int ENERGY_Y = 16;
    private static final int PLAYER_INV_X = 7;
    private static final int PLAYER_INV_Y = 71;

    public CrusherMenu(int id, Inventory playerInv, CrusherBlockEntity tile) {
        super(ModMenus.MACHINE_CRUSHER.get(), id);
        this.tile = tile;
        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(data);

        addSlot(new SlotItemHandler(tile.getInput(), 0, menuSlotX(INPUT_X), menuSlotY(SLOT_Y)));
        addSlot(new SlotItemHandler(tile.getOutput(), 0, menuSlotX(OUTPUT_X), menuSlotY(SLOT_Y)) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addSlot(new SlotItemHandler(tile.getSecondaryOutput(), 0, menuSlotX(SECONDARY_X), menuSlotY(SLOT_Y)) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.upgradeSlotIndex = addUpgradeSlotAndGetIndex(tile.getUpgrades(), UPGRADE_X, UPGRADE_Y);
        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static CrusherMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof CrusherBlockEntity tile) return new CrusherMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a CrusherBlockEntity");
    }

    @Override
    public void broadcastChanges() {
        data.set(DATA_PROGRESS, tile.getProgress());
        data.set(DATA_TOTAL_TIME, tile.getTotalTime());
        data.set(DATA_ENERGY, tile.getEnergyStored());
        data.set(DATA_ENERGY_CAP, tile.getEnergyCapacity());
        data.set(DATA_GP_POWERED, tile.isGpPowered() ? 1 : 0);
        data.set(DATA_REDSTONE_STATE, tile.getRedstoneState().ordinal());
        data.set(DATA_SPEED_LEVEL, tile.getSpeedLevel());
        super.broadcastChanges();
    }

    @Override public int getArrowX() { return ARROW_X; }
    @Override public int getArrowY() { return SLOT_Y; }

    @Override
    public float getArrowProgress() {
        int total = data.get(DATA_TOTAL_TIME);
        if (total <= 0) return 0f;
        return (float) data.get(DATA_PROGRESS) / total;
    }

    @Override public boolean isArrowOverloaded() { return data.get(DATA_GP_POWERED) == 0; }

    @Override
    public List<Component> getArrowTooltip() {
        List<Component> lines = new ArrayList<>();
        int total = data.get(DATA_TOTAL_TIME);
        if (total > 0) lines.add(Component.literal(data.get(DATA_PROGRESS) + " / " + total + " ticks"));
        return lines;
    }

    @Override
    public List<Component> getArrowErrorTooltip() {
        return List.of(Component.translatable("tooltip.extrautils2.grid_overloaded"));
    }

    @Override public int getUpgradeX() { return UPGRADE_X; }
    @Override public int getUpgradeY() { return UPGRADE_Y; }
    @Override public int getRedstoneX() { return REDSTONE_X; }
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

    @Override public String getPreviewBaseTexture() { return "machine/machine_base_side"; }
    @Override
    public String getPreviewFrontTexture() {
        return tile.getBlockState().getValue(MachineBlock.ACTIVE)
                ? "machine/crusher_on" : "machine/crusher_off";
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int playerStart = 4;
        int playerEnd = slots.size();

        if (index < playerStart) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (slots.get(upgradeSlotIndex).mayPlace(stack)) {
                if (!moveItemStackTo(stack, upgradeSlotIndex, upgradeSlotIndex + 1, false))
                    if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }

    @Override
    public boolean stillValid(Player player) { return tile.stillValid(player); }
}
