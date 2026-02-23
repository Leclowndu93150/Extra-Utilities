package com.leclowndu93150.extrautils2.blockentity.generator;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.api.power.IPassiveGp;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import com.leclowndu93150.extrautils2.power.GpManager;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class MachineGeneratorTile extends XUBlockEntity implements IGpSource, IPassiveGp, MenuProvider {

    private final ItemStackHandler inventory;
    private final @Nullable FluidTank fluidTank;

    private int ownerFrequency = 0;
    private boolean registered = false;
    private float currentGp = 0f;

    private int fuelTotalEnergy = 0;
    private int fuelRemaining = 0;
    private float fuelGpRate = 0f;

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
                ? new FluidTank(4000) {
                    @Override
                    protected void onContentsChanged() {
                        setChanged();
                    }
                }
                : null;
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

        MachineGeneratorType type = getType(state);
        if (type == null) return;

        float newGp = tile.processFuel(type);
        if (newGp != tile.currentGp) {
            tile.currentGp = newGp;
            GpManager.INSTANCE.markSourceDirty(tile);
        }

        boolean lit = newGp > 0f;
        if (state.getValue(MachineGeneratorBlock.POWERED) != lit) {
            level.setBlock(pos, state.setValue(MachineGeneratorBlock.POWERED, lit), 3);
        }
    }

    private float processFuel(MachineGeneratorType type) {
        if (fuelRemaining > 0) {
            float rate = fuelGpRate;
            fuelRemaining--;
            if (fuelRemaining == 0) {
                fuelTotalEnergy = 0;
                fuelGpRate = 0f;
                setChanged();
            }
            return rate;
        }

        if (type == MachineGeneratorType.LAVA) {
            return tryConsumeLava();
        }

        if (type == MachineGeneratorType.REDSTONE) {
            return tryConsumeRedstone();
        }

        if (type == MachineGeneratorType.SLIME) {
            return tryConsumeSlime();
        }

        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return 0f;

        MachineGeneratorType.FuelResult result = type.getFuelResult(input);
        if (result == null) return 0f;

        inventory.extractItem(0, 1, false);
        fuelTotalEnergy = result.totalEnergy();
        fuelGpRate = result.gpRate();
        fuelRemaining = (int) (result.totalEnergy() / result.gpRate());
        setChanged();
        return fuelGpRate;
    }

    private float tryConsumeLava() {
        if (fluidTank == null) return 0f;
        FluidStack lava = fluidTank.getFluid();
        if (lava.isEmpty() || lava.getAmount() < 50) return 0f;
        fluidTank.drain(50, IFluidHandler.FluidAction.EXECUTE);
        fuelTotalEnergy = 5000;
        fuelGpRate = 40f;
        fuelRemaining = (int) (5000f / 40f);
        setChanged();
        return fuelGpRate;
    }

    private float tryConsumeRedstone() {
        if (fluidTank == null) return 0f;
        ItemStack dust = inventory.getStackInSlot(0);
        if (!dust.is(net.minecraft.world.item.Items.REDSTONE)) return 0f;
        FluidStack lava = fluidTank.getFluid();
        if (lava.isEmpty() || lava.getAmount() < 50) return 0f;
        inventory.extractItem(0, 1, false);
        fluidTank.drain(50, IFluidHandler.FluidAction.EXECUTE);
        fuelTotalEnergy = 20000;
        fuelGpRate = 160f;
        fuelRemaining = (int) (20000f / 160f);
        setChanged();
        return fuelGpRate;
    }

    private float tryConsumeSlime() {
        ItemStack slimeballs = inventory.getStackInSlot(0);
        ItemStack milk = inventory.getStackInSlot(1);
        if (!slimeballs.is(Items.SLIME_BALL) || slimeballs.getCount() < 4) return 0f;
        if (!milk.is(Items.MILK_BUCKET)) return 0f;
        inventory.extractItem(0, 4, false);
        inventory.setStackInSlot(1, new ItemStack(Items.BUCKET));
        fuelTotalEnergy = 192000;
        fuelGpRate = 400f;
        fuelRemaining = (int) (192000f / 400f);
        setChanged();
        return fuelGpRate;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public @Nullable FluidTank getFluidTank() {
        return fluidTank;
    }

    public int getFuelRemaining() { return fuelRemaining; }
    public int getFuelTotalEnergy() { return fuelTotalEnergy; }
    public float getCurrentGp() { return currentGp; }
    public float getGpRate() { return fuelGpRate; }

    public MachineGeneratorType getGeneratorType() {
        return getType(getBlockState());
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

    public void setOwnerFrequency(int freq) {
        this.ownerFrequency = freq;
        setChanged();
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
    public float getGp() { return currentGp; }

    @Override
    public int frequency() { return ownerFrequency; }

    @Override
    public void onPowerChanged(boolean powered) {}

    @Override
    public boolean isLoaded() {
        return level != null && !level.isClientSide && !isRemoved();
    }

    @Override
    public @Nullable Level level() { return level; }

    @Override
    public @Nullable BlockPos getPos() { return worldPosition; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("ownerFreq", ownerFrequency);
        tag.putFloat("currentGp", currentGp);
        tag.putInt("fuelRemaining", fuelRemaining);
        tag.putInt("fuelTotalEnergy", fuelTotalEnergy);
        tag.putFloat("fuelGpRate", fuelGpRate);
        tag.put("inventory", inventory.serializeNBT(provider));
        if (fluidTank != null) fluidTank.writeToNBT(provider, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        ownerFrequency = tag.getInt("ownerFreq");
        currentGp = tag.getFloat("currentGp");
        fuelRemaining = tag.getInt("fuelRemaining");
        fuelTotalEnergy = tag.getInt("fuelTotalEnergy");
        fuelGpRate = tag.getFloat("fuelGpRate");
        inventory.deserializeNBT(provider, tag.getCompound("inventory"));
        if (fluidTank != null) fluidTank.readFromNBT(provider, tag);
    }
}
