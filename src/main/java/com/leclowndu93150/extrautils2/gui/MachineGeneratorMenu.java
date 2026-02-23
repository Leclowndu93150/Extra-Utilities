package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.blockentity.generator.MachineGeneratorTile;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import com.leclowndu93150.extrautils2.util.StringHelper;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MachineGeneratorMenu extends XUBaseMenu implements HasUpgradeSlot, HasProgressArrow, HasRedstoneControl, HasEnergyBar, HasFluidBar {

    public final MachineGeneratorTile tile;
    private final ContainerData data;
    private final Layout layout;
    private final int upgradeSlotIndex;

    private static final int GUI_W = 176;
    private static final int LAYOUT_W = 170;
    private static final int LAYOUT_LEFT = (GUI_W - LAYOUT_W) / 2;
    private static final int CENTER_X = 85;
    private static final int SLOT_Y = 32;
    private static final int ENERGY_Y = 16;
    private static final int FLUID_H = 33;
    private static final int FLUID_Y = SLOT_Y + 9 - FLUID_H / 2;
    private static final int INPUT_SLOT_X_OFFSET = 0;
    private static final int INPUT_SLOT_Y_OFFSET = 0;
    private static final int UPGRADE_X = 4;
    private static final int UPGRADE_Y = 16;

    public static final int DATA_ENERGY_STORED       = 0;
    public static final int DATA_ENERGY_CAPACITY     = 1;
    public static final int DATA_FUEL_TICKS_REMAIN   = 2;
    public static final int DATA_FUEL_TICKS_TOTAL    = 3;
    public static final int DATA_FLUID_AMOUNT        = 4;
    public static final int DATA_FLUID_CAPACITY      = 5;
    public static final int DATA_GP_POWERED          = 6;
    public static final int DATA_REDSTONE_STATE      = 7;
    public static final int DATA_COUNT               = 8;

    private static final int REDSTONE_X = UPGRADE_X;
    private static final int REDSTONE_Y = UPGRADE_Y + 19;
    private static final int BUTTON_REDSTONE = 1;
    private static final int BUTTON_FLUID = 2;

    public MachineGeneratorMenu(int id, Inventory playerInv, MachineGeneratorTile tile) {
        super(ModMenus.MACHINE_GENERATOR.get(), id);
        this.tile = tile;
        this.layout = Layout.forTile(tile);

        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(data);

        IItemHandler inv = tile.getInventory();
        int slots = inv.getSlots();
        if (slots >= 1) addSlot(new SlotItemHandler(inv, 0, menuSlotX(layout.slotStartX + INPUT_SLOT_X_OFFSET), menuSlotY(SLOT_Y + INPUT_SLOT_Y_OFFSET)));
        if (slots >= 2) addSlot(new SlotItemHandler(inv, 1, menuSlotX(layout.slotStartX + 22 + INPUT_SLOT_X_OFFSET - 2), menuSlotY(SLOT_Y + INPUT_SLOT_Y_OFFSET)));

        this.upgradeSlotIndex = addUpgradeSlotAndGetIndex(tile.getUpgrades(), UPGRADE_X, UPGRADE_Y);

        addPlayerSlots(playerInv, layout.playerInvX, layout.playerInvY);
    }

    public static MachineGeneratorMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof MachineGeneratorTile tile) return new MachineGeneratorMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a MachineGeneratorTile at that position");
    }

    @Override
    public void broadcastChanges() {
        data.set(DATA_ENERGY_STORED,   tile.getEnergyStored());
        data.set(DATA_ENERGY_CAPACITY, tile.getEnergyCapacity());
        data.set(DATA_FUEL_TICKS_REMAIN, tile.getFuelRemainingTicks());
        data.set(DATA_FUEL_TICKS_TOTAL, tile.getFuelTotalTicks());
        var tank = tile.getFluidTank();
        if (tank != null) {
            data.set(DATA_FLUID_AMOUNT,   tank.getFluidAmount());
            data.set(DATA_FLUID_CAPACITY, tank.getCapacity());
        }
        data.set(DATA_GP_POWERED, tile.isGpPowered() ? 1 : 0);
        data.set(DATA_REDSTONE_STATE, tile.getRedstoneState().ordinal());
        super.broadcastChanges();
    }

    public int getEnergyStored()   { return data.get(DATA_ENERGY_STORED); }
    public int getEnergyCapacity() { return data.get(DATA_ENERGY_CAPACITY); }
    public int getFuelRemainingTicks() { return data.get(DATA_FUEL_TICKS_REMAIN); }
    public int getFuelTotalTicks()     { return data.get(DATA_FUEL_TICKS_TOTAL); }
    public int getFluidAmount()    { return data.get(DATA_FLUID_AMOUNT); }
    public int getFluidCapacity()  { return data.get(DATA_FLUID_CAPACITY); }
    public FluidStack getFluidStack() {
        var tank = tile.getFluidTank();
        return tank != null ? tank.getFluid() : FluidStack.EMPTY;
    }
    public boolean isGpPowered()   { return data.get(DATA_GP_POWERED) != 0; }
    public MachineGeneratorType getGeneratorType() { return tile.getGeneratorType(); }
    public int getEnergyBarX() { return layout.energyX; }
    public int getEnergyBarY() { return ENERGY_Y; }
    public int getFluidBarX() { return layout.fluidX; }
    public int getFluidBarY() { return FLUID_Y; }
    public int getFluidBarButtonId() { return BUTTON_FLUID; }
    public IFluidHandler getFluidHandler() { return tile.getFluidTank(); }
    public void onFluidChanged() { tile.setChanged(); tile.sync(); }
    public int getArrowX() { return layout.arrowX; }
    public int getArrowY() { return SLOT_Y; }
    public int getSlotStartX() { return layout.slotStartX; }
    public int getSlotY() { return SLOT_Y; }
    public int getPlayerInvX() { return layout.playerInvX; }
    public int getPlayerInvY() { return layout.playerInvY; }
    public int getUpgradeX() { return UPGRADE_X; }
    public int getUpgradeY() { return UPGRADE_Y; }
    public int getRedstoneX() { return REDSTONE_X; }
    public int getRedstoneY() { return REDSTONE_Y; }
    public int getRedstoneButtonId() { return BUTTON_REDSTONE; }
    public RedstoneState getRedstoneState() {
        int v = data.get(DATA_REDSTONE_STATE);
        RedstoneState[] values = RedstoneState.values();
        return v >= 0 && v < values.length ? values[v] : RedstoneState.OPERATE_ALWAYS;
    }
    public boolean hasRedstonePulseMode() { return true; }
    public void cycleRedstone() { tile.cycleRedstoneState(hasRedstonePulseMode()); }

    @Override
    public float getArrowProgress() {
        int total = getFuelTotalTicks();
        if (total <= 0) return 0f;
        int elapsed = Math.max(0, total - getFuelRemainingTicks());
        return (float) elapsed / total;
    }

    @Override
    public boolean isArrowOverloaded() {
        return !isGpPowered();
    }

    @Override
    public List<Component> getArrowTooltip() {
        int total = getFuelTotalTicks();
        if (total <= 0) return List.of();
        int elapsed = Math.max(0, total - getFuelRemainingTicks());
        return List.of(
                Component.literal(String.format("%s / %s ",
                        StringHelper.formatDurationSeconds(elapsed, true),
                        StringHelper.formatDurationSeconds(total, false))),
                Component.literal(ChatFormatting.GRAY
                        + NumberFormat.getPercentInstance(Locale.UK).format((double) elapsed / (double) total)
                        + ChatFormatting.RESET)
        );
    }

    @Override
    public List<Component> getArrowErrorTooltip() {
        return List.of(Component.translatable("tooltip.extrautils2.grid_overloaded"));
    }

    private static final class Layout {
        final int slotStartX;
        final int arrowX;
        final int energyX;
        final int fluidX;
        final int playerInvX;
        final int playerInvY;

        private Layout(int slotStartX, int arrowX, int energyX, int fluidX, int playerInvX, int playerInvY) {
            this.slotStartX = slotStartX;
            this.arrowX = arrowX;
            this.energyX = energyX;
            this.fluidX = fluidX;
            this.playerInvX = playerInvX;
            this.playerInvY = playerInvY;
        }

        static Layout forTile(MachineGeneratorTile tile) {
            MachineGeneratorType type = tile.getGeneratorType();
            boolean hasFluid = type != null && type.usesFluid();
            int inputSlots = tile.getInventory().getSlots();
            int inputGroups = inputSlots + (hasFluid ? 1 : 0);
            int w = inputGroups * 22 + 4 + 22;
            int x = Math.max(4, CENTER_X - w / 2);
            int fluidX = LAYOUT_LEFT + x;
            int slotStartX = LAYOUT_LEFT + x + (hasFluid ? 22 : 0);
            int arrowX = slotStartX + inputSlots * 22;
            int energyX = LAYOUT_LEFT + (LAYOUT_W - 24);
            int playerInvX = LAYOUT_LEFT + (LAYOUT_W - 162) / 2;
            int playerInvY = 166 - 95;
            return new Layout(slotStartX, arrowX, energyX, fluidX, playerInvX, playerInvY);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        int invSlots = tile.getInventory().getSlots();
        int totalSlots = slots.size();
        int playerStart = invSlots + 1;
        int playerEnd = totalSlots;

        if (index < invSlots) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (index == upgradeSlotIndex) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (slots.get(upgradeSlotIndex).mayPlace(stack)) {
                if (!moveItemStackTo(stack, upgradeSlotIndex, upgradeSlotIndex + 1, false)) {
                    if (!moveItemStackTo(stack, 0, invSlots, false)) return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(stack, 0, invSlots, false)) return ItemStack.EMPTY;
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
