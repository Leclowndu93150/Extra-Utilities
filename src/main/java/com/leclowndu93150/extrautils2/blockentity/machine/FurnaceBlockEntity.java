package com.leclowndu93150.extrautils2.blockentity.machine;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.XUEnergyStorage;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.gui.machine.FurnaceMenu;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.upgrade.UpgradeStackHandler;
import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class FurnaceBlockEntity extends XUBlockEntity implements MenuProvider, IGpSource, MachineBlock.IGpMachine, MachineBlock.IDroppableInventory {

    private final XUEnergyStorage energy = new XUEnergyStorage(10000, 80, 0, true, false);

    private int progress = 0;
    private int totalTime = 0;
    private int energyPerTick = 20;

    private int ownerFrequency = 0;
    private boolean registered = false;
    private RedstoneState redstoneState = RedstoneState.OPERATE_ALWAYS;
    private boolean redstonePowered = false;
    private int redstonePulses = 0;

    private final ItemStackHandler input = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            updateRecipe();
        }
    };

    private final ItemStackHandler output = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };

    private final UpgradeStackHandler upgrades = new UpgradeStackHandler(EnumSet.of(UpgradeType.SPEED), () -> {
        setChanged();
        if (ownerFrequency != 0) GpManager.INSTANCE.markSourceDirty(this);
    });

    private @Nullable SmeltingRecipe currentRecipe = null;

    public FurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MACHINE_FURNACE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FurnaceBlockEntity tile) {
        if (level.isClientSide) return;

        if (!tile.registered && tile.ownerFrequency != 0) {
            GpManager.INSTANCE.addSource(tile);
            tile.registered = true;
        }

        tile.updateRedstoneState(level, pos);

        boolean active = false;
        if (tile.isGpPowered() && tile.canRunByRedstone() && tile.currentRecipe != null) {
            int speedFactor = 1 + tile.getSpeedLevel();
            int cost = tile.energyPerTick * speedFactor;
            if (tile.energy.getEnergyStored() >= cost) {
                tile.energy.extractEnergy(cost, false);
                tile.progress += speedFactor;
                active = true;
                tile.setChanged();

                if (tile.progress >= tile.totalTime) {
                    ItemStack result = tile.currentRecipe.getResultItem(level.registryAccess()).copy();
                    tile.input.extractItem(0, 1, false);
                    ItemStack existing = tile.output.getStackInSlot(0);
                    if (existing.isEmpty()) {
                        tile.output.setStackInSlot(0, result);
                    } else {
                        existing.grow(result.getCount());
                    }
                    tile.progress = 0;
                    tile.updateRecipe();
                }
            }
        } else if (tile.currentRecipe == null) {
            tile.progress = 0;
        }

        boolean wasActive = state.getValue(MachineBlock.ACTIVE);
        if (wasActive != active) {
            level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, active), 3);
        }
    }

    private void updateRecipe() {
        if (level == null || level.isClientSide) return;
        ItemStack inputStack = input.getStackInSlot(0);
        if (inputStack.isEmpty()) {
            currentRecipe = null;
            totalTime = 0;
            return;
        }
        SingleRecipeInput ri = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<SmeltingRecipe>> found = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, ri, level);
        if (found.isPresent()) {
            SmeltingRecipe recipe = found.get().value();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            ItemStack outputStack = output.getStackInSlot(0);
            if (!outputStack.isEmpty() && (!ItemStack.isSameItemSameComponents(outputStack, result)
                    || outputStack.getCount() + result.getCount() > outputStack.getMaxStackSize())) {
                currentRecipe = null;
                totalTime = 0;
                return;
            }
            currentRecipe = recipe;
            totalTime = 100;
        } else {
            currentRecipe = null;
            totalTime = 0;
        }
    }

    public ItemStackHandler getInput() { return input; }
    public ItemStackHandler getOutput() { return output; }
    public UpgradeStackHandler getUpgrades() { return upgrades; }
    public XUEnergyStorage getEnergyStorage() { return energy; }
    public int getProgress() { return progress; }
    public int getTotalTime() { return totalTime; }
    public int getEnergyStored() { return energy.getEnergyStored(); }
    public int getEnergyCapacity() { return energy.getMaxEnergyStored(); }
    public int getSpeedLevel() { return upgrades.getLevel(UpgradeType.SPEED); }

    @Override
    public void setOwnerFrequency(int freq) {
        this.ownerFrequency = freq;
        setChanged();
        if (level != null && !level.isClientSide && ownerFrequency != 0 && !registered) {
            GpManager.INSTANCE.addSource(this);
            registered = true;
        }
    }

    public boolean isGpPowered() {
        if (level == null || level.isClientSide) return false;
        if (ownerFrequency == 0) return false;
        GpFrequency freq = GpManager.INSTANCE.getOrCreateFreq(ownerFrequency);
        return freq.isPowered();
    }

    public RedstoneState getRedstoneState() { return redstoneState; }

    public void cycleRedstoneState(boolean allowPulse) {
        redstoneState = redstoneState.next(allowPulse);
        redstonePulses = 0;
        setChanged();
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public IItemHandler[] getDroppableInventories() {
        return new IItemHandler[]{input, output, upgrades};
    }

    @Override
    public float getGp() {
        int speedLevel = getSpeedLevel();
        if (speedLevel <= 0) return 0;
        return UpgradeType.SPEED.getPowerUse(speedLevel);
    }

    @Override public int frequency() { return ownerFrequency; }
    @Override public void onPowerChanged(boolean powered) {}
    @Override public boolean isLoaded() { return level != null && !level.isClientSide && !isRemoved(); }
    @Override public @Nullable Level level() { return level; }
    @Override public @Nullable BlockPos getPos() { return worldPosition; }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (registered) { GpManager.INSTANCE.removeSource(this); registered = false; }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide && ownerFrequency != 0 && !registered) {
            GpManager.INSTANCE.addSource(this);
            registered = true;
        }
        updateRecipe();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.extrautils2.machine_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new FurnaceMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Input", input.serializeNBT(provider));
        tag.put("Output", output.serializeNBT(provider));
        tag.put("Upgrades", upgrades.serializeNBT(provider));
        tag.putInt("Progress", progress);
        tag.putInt("TotalTime", totalTime);
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("OwnerFreq", ownerFrequency);
        tag.putInt("RedstoneState", redstoneState.ordinal());
        tag.putBoolean("RedstonePowered", redstonePowered);
        tag.putInt("RedstonePulses", redstonePulses);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        input.deserializeNBT(provider, tag.getCompound("Input"));
        output.deserializeNBT(provider, tag.getCompound("Output"));
        if (tag.contains("Upgrades")) upgrades.deserializeNBT(provider, tag.getCompound("Upgrades"));
        progress = tag.getInt("Progress");
        totalTime = tag.getInt("TotalTime");
        energy.setEnergy(tag.getInt("Energy"));
        ownerFrequency = tag.getInt("OwnerFreq");
        int rs = tag.getInt("RedstoneState");
        RedstoneState[] values = RedstoneState.values();
        redstoneState = rs >= 0 && rs < values.length ? values[rs] : RedstoneState.OPERATE_ALWAYS;
        redstonePowered = tag.getBoolean("RedstonePowered");
        redstonePulses = tag.getInt("RedstonePulses");
    }

    private boolean canRunByRedstone() {
        return switch (redstoneState) {
            case OPERATE_ALWAYS -> true;
            case OPERATE_REDSTONE_ON -> redstonePowered;
            case OPERATE_REDSTONE_OFF -> !redstonePowered;
            case OPERATE_REDSTONE_PULSE -> redstonePulses > 0;
        };
    }

    private void updateRedstoneState(Level level, BlockPos pos) {
        boolean newPower = level.hasNeighborSignal(pos);
        if (newPower != redstonePowered) {
            redstonePowered = newPower;
            if (newPower && redstoneState == RedstoneState.OPERATE_REDSTONE_PULSE) redstonePulses++;
            setChanged();
        }
    }
}
