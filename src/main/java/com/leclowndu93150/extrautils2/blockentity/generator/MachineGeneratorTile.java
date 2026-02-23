package com.leclowndu93150.extrautils2.blockentity.generator;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.upgrade.UpgradeStackHandler;
import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import com.leclowndu93150.extrautils2.blockentity.XUEnergyStorage;
import com.leclowndu93150.extrautils2.blockentity.XUFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class MachineGeneratorTile extends XUBlockEntity implements MenuProvider, IGpSource {

    private final ItemStackHandler inventory;
    private final @Nullable XUFluidTank fluidTank;
    private final UpgradeStackHandler upgrades;

    private final XUEnergyStorage energyStorage;

    private int fuelTotalEnergy = 0;
    private int fuelRemainingTicks = 0;
    private int fuelTotalTicks = 0;
    private int energyPerTick = 0;

    private int ownerFrequency = 0;
    private boolean registered = false;
    private RedstoneState redstoneState = RedstoneState.OPERATE_ALWAYS;
    private boolean redstonePowered = false;
    private int redstonePulses = 0;

    public MachineGeneratorTile(BlockEntityType<?> beType, BlockPos pos, BlockState state) {
        super(beType, pos, state);
        MachineGeneratorType genType = getType(state);
        boolean hasSecondary = genType != null && genType.needsSecondarySlot();
        this.inventory = new ItemStackHandler(hasSecondary ? 2 : 1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.fluidTank = (genType != null && genType.usesFluid())
                ? new XUFluidTank(4000).setListener(this::setChanged)
                : null;
        this.upgrades = new UpgradeStackHandler(java.util.EnumSet.of(UpgradeType.SPEED), () -> {
            setChanged();
            if (ownerFrequency != 0) {
                GpManager.INSTANCE.markSourceDirty(this);
            }
        });
        int capacity = genType != null ? genType.energyCapacity : 10000;
        int maxExtract = genType != null ? genType.maxExtract : 1000;
        this.energyStorage = new XUEnergyStorage(capacity, 0, maxExtract, false, true);
    }

    private static @Nullable MachineGeneratorType getType(BlockState state) {
        if (state.getBlock() instanceof MachineGeneratorBlock b) return b.generatorType;
        return null;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MachineGeneratorTile tile) {
        if (level.isClientSide) return;

        if (!tile.registered && tile.ownerFrequency != 0) {
            GpManager.INSTANCE.addSource(tile);
            tile.registered = true;
        }

        tile.updateRedstoneState(level, pos);

        MachineGeneratorType type = getType(state);
        if (type == null) return;

        boolean active = tile.isGpPowered() && tile.canRunByRedstone() && tile.processFuel(type);
        tile.pushEnergy();

        boolean lit = active;
        if (state.getValue(MachineGeneratorBlock.POWERED) != lit) {
            level.setBlock(pos, state.setValue(MachineGeneratorBlock.POWERED, lit), 3);
        }
    }

    private boolean processFuel(MachineGeneratorType type) {
        if (fuelRemainingTicks > 0) {
            boolean produced = false;
            if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                int speedFactor = 1 + getSpeedLevel();
                int toAdd = energyPerTick * speedFactor;
                int added = energyStorage.addEnergy(toAdd);
                if (added > 0) {
                    produced = true;
                    setChanged();
                    fuelRemainingTicks = Math.max(0, fuelRemainingTicks - speedFactor);
                    if (fuelRemainingTicks <= 0) {
                        fuelTotalEnergy = 0;
                        fuelTotalTicks = 0;
                        energyPerTick = 0;
                        setChanged();
                    }
                }
            }
            return produced && fuelRemainingTicks > 0;
        }

        if (energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
            return false;
        }

        int newRate = 0;
        int newTotalEnergy = 0;
        int newTotalTicks = 0;

        if (type == MachineGeneratorType.LAVA) {
            FuelValues v = tryConsumeLava();
            if (v != null) {
                newTotalEnergy = v.totalEnergy;
                newRate = v.energyPerTick;
                newTotalTicks = v.totalTicks;
            }
        } else if (type == MachineGeneratorType.REDSTONE) {
            FuelValues v = tryConsumeRedstone();
            if (v != null) {
                newTotalEnergy = v.totalEnergy;
                newRate = v.energyPerTick;
                newTotalTicks = v.totalTicks;
            }
        } else if (type == MachineGeneratorType.SLIME) {
            FuelValues v = tryConsumeSlime();
            if (v != null) {
                newTotalEnergy = v.totalEnergy;
                newRate = v.energyPerTick;
                newTotalTicks = v.totalTicks;
            }
        } else {
            ItemStack input = inventory.getStackInSlot(0);
            if (input.isEmpty()) return false;

            MachineGeneratorType.FuelResult result = type.getFuelResult(input);
            if (result == null) return false;

            inventory.extractItem(0, 1, false);
            newTotalEnergy = result.totalEnergy();
            newRate = Math.max(1, Math.round(result.gpRate()));
            newTotalTicks = newTotalEnergy / newRate;
        }

        if (newTotalTicks <= 0 || newRate <= 0) return false;
        fuelTotalEnergy = newTotalEnergy;
        energyPerTick = newRate;
        fuelTotalTicks = newTotalTicks;
        fuelRemainingTicks = newTotalTicks;
        if (redstoneState == RedstoneState.OPERATE_REDSTONE_PULSE && redstonePulses > 0) {
            redstonePulses--;
        }
        setChanged();
        return true;
    }

    private @Nullable FuelValues tryConsumeLava() {
        if (fluidTank == null) return null;
        FluidStack lava = fluidTank.getFluid();
        if (lava.isEmpty() || lava.getAmount() < 50) return null;
        fluidTank.drain(50, IFluidHandler.FluidAction.EXECUTE);
        return FuelValues.of(5000, 40);
    }

    private @Nullable FuelValues tryConsumeRedstone() {
        if (fluidTank == null) return null;
        ItemStack dust = inventory.getStackInSlot(0);
        if (!dust.is(net.minecraft.world.item.Items.REDSTONE)) return null;
        FluidStack lava = fluidTank.getFluid();
        if (lava.isEmpty() || lava.getAmount() < 50) return null;
        inventory.extractItem(0, 1, false);
        fluidTank.drain(50, IFluidHandler.FluidAction.EXECUTE);
        return FuelValues.of(20000, 160);
    }

    private @Nullable FuelValues tryConsumeSlime() {
        ItemStack slimeballs = inventory.getStackInSlot(0);
        ItemStack milk = inventory.getStackInSlot(1);
        if (!slimeballs.is(Items.SLIME_BALL) || slimeballs.getCount() < 4) return null;
        if (!milk.is(Items.MILK_BUCKET)) return null;
        inventory.extractItem(0, 4, false);
        inventory.setStackInSlot(1, new ItemStack(Items.BUCKET));
        return FuelValues.of(192000, 400);
    }

    private void pushEnergy() {
        if (level == null) return;
        int available = energyStorage.getEnergyStored();
        if (available <= 0) return;
        int speedFactor = 1 + getSpeedLevel();
        int remaining = Math.min(energyStorage.getMaxExtract() * speedFactor, available);
        for (var dir : net.minecraft.core.Direction.values()) {
            if (remaining <= 0) break;
            var target = level.getBlockEntity(worldPosition.relative(dir));
            if (target == null) continue;
            IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, target.getBlockPos(), dir.getOpposite());
            if (cap == null || !cap.canReceive()) continue;
            int sent = cap.receiveEnergy(remaining, false);
            if (sent > 0) {
                energyStorage.extractEnergy(sent, false);
                setChanged();
                remaining -= sent;
            }
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public UpgradeStackHandler getUpgrades() {
        return upgrades;
    }

    public @Nullable XUFluidTank getFluidTank() {
        return fluidTank;
    }

    public IEnergyStorage getEnergyStorage() { return energyStorage; }
    public int getEnergyStored() { return energyStorage.getEnergyStored(); }
    public int getEnergyCapacity() { return energyStorage.getMaxEnergyStored(); }
    public int getFuelRemainingTicks() { return fuelRemainingTicks; }
    public int getFuelTotalTicks() { return fuelTotalTicks; }
    public int getEnergyPerTick() { return energyPerTick; }
    public int getSpeedLevel() { return getSpeedLevelInternal(); }

    public MachineGeneratorType getGeneratorType() {
        return getType(getBlockState());
    }

    public void setOwnerFrequency(int freq) {
        this.ownerFrequency = freq;
        setChanged();
        if (level != null && !level.isClientSide && ownerFrequency != 0 && !registered) {
            GpManager.INSTANCE.addSource(this);
            registered = true;
        }
    }

    @Override
    public Component getDisplayName() {
        MachineGeneratorType type = getGeneratorType();
        String key = type != null
                ? "block.extrautils2.machine_generator_" + type.name().toLowerCase()
                : "block.extrautils2.machine_generator";
        return Component.translatable(key);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, net.minecraft.world.entity.player.Player player) {
        return new MachineGeneratorMenu(id, playerInv, this);
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    public RedstoneState getRedstoneState() {
        return redstoneState;
    }

    public void cycleRedstoneState(boolean allowPulse) {
        redstoneState = redstoneState.next(allowPulse);
        redstonePulses = 0;
        setChanged();
    }

    public boolean isGpPowered() {
        if (level == null || level.isClientSide) return false;
        if (ownerFrequency == 0) return false;
        GpFrequency freq = GpManager.INSTANCE.getOrCreateFreq(ownerFrequency);
        return freq.isPowered();
    }

    @Override
    public float getGp() {
        int level = getSpeedLevel();
        if (level <= 0) return Float.NaN;
        return UpgradeType.SPEED.getPowerUse(level);
    }

    @Override
    public int frequency() {
        return ownerFrequency;
    }

    @Override
    public void onPowerChanged(boolean powered) {
    }

    @Override
    public boolean isLoaded() {
        return level != null && !level.isClientSide && !isRemoved();
    }

    @Override
    public @Nullable Level level() {
        return level;
    }

    @Override
    public @Nullable BlockPos getPos() {
        return worldPosition;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (registered) {
            GpManager.INSTANCE.removeSource(this);
            registered = false;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide && ownerFrequency != 0 && !registered) {
            GpManager.INSTANCE.addSource(this);
            registered = true;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("energyStored", energyStorage.getEnergyStored());
        tag.putInt("fuelRemainingTicks", fuelRemainingTicks);
        tag.putInt("fuelTotalEnergy", fuelTotalEnergy);
        tag.putInt("fuelTotalTicks", fuelTotalTicks);
        tag.putInt("energyPerTick", energyPerTick);
        tag.putInt("ownerFreq", ownerFrequency);
        tag.putInt("redstoneState", redstoneState.ordinal());
        tag.putBoolean("redstonePowered", redstonePowered);
        tag.putInt("redstonePulses", redstonePulses);
        tag.put("inventory", inventory.serializeNBT(provider));
        tag.put("upgrades", upgrades.serializeNBT(provider));
        if (fluidTank != null) fluidTank.writeToNBT(provider, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        energyStorage.setEnergy(tag.getInt("energyStored"));
        fuelRemainingTicks = tag.getInt("fuelRemainingTicks");
        fuelTotalEnergy = tag.getInt("fuelTotalEnergy");
        fuelTotalTicks = tag.getInt("fuelTotalTicks");
        energyPerTick = tag.getInt("energyPerTick");
        ownerFrequency = tag.getInt("ownerFreq");
        int rs = tag.getInt("redstoneState");
        RedstoneState[] values = RedstoneState.values();
        redstoneState = rs >= 0 && rs < values.length ? values[rs] : RedstoneState.OPERATE_ALWAYS;
        redstonePowered = tag.getBoolean("redstonePowered");
        redstonePulses = tag.getInt("redstonePulses");
        inventory.deserializeNBT(provider, tag.getCompound("inventory"));
        if (tag.contains("upgrades")) {
            upgrades.deserializeNBT(provider, tag.getCompound("upgrades"));
        }
        if (fluidTank != null) fluidTank.readFromNBT(provider, tag);
    }

    private int getSpeedLevelInternal() {
        return upgrades.getLevel(UpgradeType.SPEED);
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
            if (newPower && redstoneState == RedstoneState.OPERATE_REDSTONE_PULSE) {
                redstonePulses++;
            }
            setChanged();
        }
    }

    private record FuelValues(int totalEnergy, int energyPerTick, int totalTicks) {
        static FuelValues of(int totalEnergy, int energyPerTick) {
            int ticks = totalEnergy / Math.max(1, energyPerTick);
            return new FuelValues(totalEnergy, Math.max(1, energyPerTick), ticks);
        }
    }
}
