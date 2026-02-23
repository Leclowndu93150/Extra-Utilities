package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.blockentity.generator.MachineGeneratorTile;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class MachineGeneratorMenu extends XUBaseMenu {

    public final MachineGeneratorTile tile;
    private final ContainerData data;
    private final Layout layout;

    private static final int GUI_W = 176;
    private static final int LAYOUT_W = 170;
    private static final int LAYOUT_LEFT = (GUI_W - LAYOUT_W) / 2;
    private static final int CENTER_X = 85;
    private static final int SLOT_Y = 32;
    private static final int ENERGY_Y = 16;
    private static final int FLUID_H = 33;
    private static final int FLUID_Y = SLOT_Y + 9 - FLUID_H / 2;
    private static final int INPUT_SLOT_X_OFFSET = 1;
    private static final int INPUT_SLOT_Y_OFFSET = 1;

    public static final int DATA_FUEL_REMAINING  = 0;
    public static final int DATA_FUEL_TOTAL      = 1;
    public static final int DATA_GP_RATE_X100    = 2;
    public static final int DATA_CURRENT_GP_X100 = 3;
    public static final int DATA_FLUID_AMOUNT    = 4;
    public static final int DATA_FLUID_CAPACITY  = 5;
    public static final int DATA_COUNT           = 6;

    public MachineGeneratorMenu(int id, Inventory playerInv, MachineGeneratorTile tile) {
        super(ModMenus.MACHINE_GENERATOR.get(), id);
        this.tile = tile;
        this.layout = Layout.forTile(tile);

        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(data);

        IItemHandler inv = tile.getInventory();
        int slots = inv.getSlots();
        if (slots >= 1) addSlot(new SlotItemHandler(inv, 0, layout.slotStartX + INPUT_SLOT_X_OFFSET, SLOT_Y + INPUT_SLOT_Y_OFFSET));
        if (slots >= 2) addSlot(new SlotItemHandler(inv, 1, layout.slotStartX + 22 + INPUT_SLOT_X_OFFSET - 2, SLOT_Y + INPUT_SLOT_Y_OFFSET));

        addPlayerSlots(playerInv, layout.playerInvX, layout.playerInvY);
    }

    public static MachineGeneratorMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof MachineGeneratorTile tile) return new MachineGeneratorMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a MachineGeneratorTile at that position");
    }

    @Override
    public void broadcastChanges() {
        data.set(DATA_FUEL_REMAINING,  tile.getFuelRemaining());
        data.set(DATA_FUEL_TOTAL,      tile.getFuelTotalEnergy());
        data.set(DATA_GP_RATE_X100,    (int) (tile.getCurrentGp() * 100));
        data.set(DATA_CURRENT_GP_X100, (int) (tile.getCurrentGp() * 100));
        var tank = tile.getFluidTank();
        if (tank != null) {
            data.set(DATA_FLUID_AMOUNT,   tank.getFluidAmount());
            data.set(DATA_FLUID_CAPACITY, tank.getCapacity());
        }
        super.broadcastChanges();
    }

    public int getFuelRemaining()  { return data.get(DATA_FUEL_REMAINING); }
    public int getFuelTotal()      { return data.get(DATA_FUEL_TOTAL); }
    public float getGpRate()       { return data.get(DATA_GP_RATE_X100) / 100f; }
    public int getFluidAmount()    { return data.get(DATA_FLUID_AMOUNT); }
    public int getFluidCapacity()  { return data.get(DATA_FLUID_CAPACITY); }
    public MachineGeneratorType getGeneratorType() { return tile.getGeneratorType(); }
    public int getEnergyX() { return layout.energyX; }
    public int getEnergyY() { return ENERGY_Y; }
    public int getFluidX() { return layout.fluidX; }
    public int getFluidY() { return FLUID_Y; }
    public int getArrowX() { return layout.arrowX; }
    public int getArrowY() { return SLOT_Y; }
    public int getSlotStartX() { return layout.slotStartX; }
    public int getSlotY() { return SLOT_Y; }
    public int getPlayerInvX() { return layout.playerInvX; }
    public int getPlayerInvY() { return layout.playerInvY; }

    public float getFuelProgress() {
        int total = getFuelTotal();
        if (total <= 0) return 0f;
        return (float) getFuelRemaining() / total * tile.getGpRate();
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
        int playerStart = invSlots;
        int playerEnd = totalSlots;

        if (index < invSlots) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, invSlots, false)) return ItemStack.EMPTY;
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
