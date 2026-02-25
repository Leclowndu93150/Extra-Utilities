package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.blockentity.ResonatorBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModMenus;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import com.leclowndu93150.extrautils2.util.StringHelper;
import net.minecraft.ChatFormatting;
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

public class ResonatorMenu extends XUBaseMenu implements HasUpgradeSlot, HasProgressArrow, HasRedstoneControl {

    public final ResonatorBlockEntity tile;
    private final ContainerData data;
    private final int upgradeSlotIndex;

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_ENERGY = 1;
    public static final int DATA_GP_POWERED = 2;
    public static final int DATA_REDSTONE_STATE = 3;
    public static final int DATA_SPEED_LEVEL = 4;
    public static final int DATA_COUNT = 5;

    private static final int INPUT_X = 50;
    private static final int SLOT_Y = 32;
    private static final int OUTPUT_X = 102;
    private static final int ARROW_X = 74;
    private static final int UPGRADE_X = 148;
    private static final int UPGRADE_Y = 32;
    private static final int REDSTONE_X = 4;
    private static final int REDSTONE_Y = 16;
    private static final int PLAYER_INV_X = 7;
    private static final int PLAYER_INV_Y = 71;
    private static final int BUTTON_REDSTONE = 1;

    public ResonatorMenu(int id, Inventory playerInv, ResonatorBlockEntity tile) {
        super(ModMenus.RESONATOR.get(), id);
        this.tile = tile;

        this.data = new SimpleContainerData(DATA_COUNT);
        this.addDataSlots(data);

        addSlot(new SlotItemHandler(tile.getInput(), 0, menuSlotX(INPUT_X), menuSlotY(SLOT_Y)));
        addSlot(new SlotItemHandler(tile.getOutput(), 0, menuSlotX(OUTPUT_X), menuSlotY(SLOT_Y)) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.upgradeSlotIndex = addUpgradeSlotAndGetIndex(tile.getUpgrades(), UPGRADE_X, UPGRADE_Y);

        addPlayerSlots(playerInv, PLAYER_INV_X, PLAYER_INV_Y);
    }

    public static ResonatorMenu fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        BlockEntity be = playerInv.player.level().getBlockEntity(buf.readBlockPos());
        if (be instanceof ResonatorBlockEntity tile) return new ResonatorMenu(id, playerInv, tile);
        throw new IllegalStateException("Not a ResonatorBlockEntity at that position");
    }

    @Override
    public void broadcastChanges() {
        data.set(DATA_PROGRESS, tile.getProgress());
        data.set(DATA_ENERGY, tile.getCurrentEnergy());
        data.set(DATA_GP_POWERED, tile.isGpPowered() ? 1 : 0);
        data.set(DATA_REDSTONE_STATE, tile.getRedstoneState().ordinal());
        data.set(DATA_SPEED_LEVEL, tile.getSpeedLevel());
        super.broadcastChanges();
    }

    public int getProgress() { return data.get(DATA_PROGRESS); }
    public int getEnergy() { return data.get(DATA_ENERGY); }
    public boolean isGpPowered() { return data.get(DATA_GP_POWERED) != 0; }
    public int getSpeedLevel() { return data.get(DATA_SPEED_LEVEL); }

    @Override
    public int getArrowX() { return ARROW_X; }
    @Override
    public int getArrowY() { return SLOT_Y; }

    @Override
    public float getArrowProgress() {
        int energy = getEnergy();
        if (energy <= 0) return 0f;
        return (float) getProgress() / energy;
    }

    @Override
    public boolean isArrowOverloaded() {
        return !isGpPowered();
    }

    @Override
    public List<Component> getArrowTooltip() {
        List<Component> lines = new ArrayList<>();
        int energy = getEnergy();
        if (energy > 0) {
            int speedLevel = getSpeedLevel();
            float gpCurrent = getProgress() * 0.01f * (1 + speedLevel);
            float gpTotal = energy * 0.01f * (1 + speedLevel);
            lines.add(Component.literal("Power: " + StringHelper.format(gpCurrent) + " / " + StringHelper.format(gpTotal) + " GP"));
        }
        return lines;
    }

    @Override
    public List<Component> getArrowErrorTooltip() {
        return List.of(Component.translatable("tooltip.extrautils2.grid_overloaded"));
    }

    @Override
    public int getUpgradeX() { return UPGRADE_X; }
    @Override
    public int getUpgradeY() { return UPGRADE_Y; }

    @Override
    public int getRedstoneX() { return REDSTONE_X; }
    @Override
    public int getRedstoneY() { return REDSTONE_Y; }
    @Override
    public int getRedstoneButtonId() { return BUTTON_REDSTONE; }

    @Override
    public RedstoneState getRedstoneState() {
        int v = data.get(DATA_REDSTONE_STATE);
        RedstoneState[] values = RedstoneState.values();
        return v >= 0 && v < values.length ? values[v] : RedstoneState.OPERATE_ALWAYS;
    }

    @Override
    public boolean hasRedstonePulseMode() { return true; }

    @Override
    public void cycleRedstone() { tile.cycleRedstoneState(hasRedstonePulseMode()); }

    public int getPlayerInvX() { return PLAYER_INV_X; }
    public int getPlayerInvY() { return PLAYER_INV_Y; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        int playerStart = 3;
        int playerEnd = slots.size();

        if (index == 0) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (index == 1) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (index == upgradeSlotIndex) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else {
            if (slots.get(upgradeSlotIndex).mayPlace(stack)) {
                if (!moveItemStackTo(stack, upgradeSlotIndex, upgradeSlotIndex + 1, false)) {
                    if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
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
