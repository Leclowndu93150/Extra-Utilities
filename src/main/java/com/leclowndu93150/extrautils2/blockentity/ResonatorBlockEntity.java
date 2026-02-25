package com.leclowndu93150.extrautils2.blockentity;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.gui.ResonatorMenu;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.recipe.ResonatorRecipe;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import com.leclowndu93150.extrautils2.registry.ModRecipeTypes;
import com.leclowndu93150.extrautils2.upgrade.UpgradeStackHandler;
import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
import com.leclowndu93150.extrautils2.util.RedstoneState;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class ResonatorBlockEntity extends XUBlockEntity implements MenuProvider, IGpSource {

    private int progress = 0;
    private int currentEnergy = 0;
    private @Nullable ResonatorRecipe currentRecipe = null;

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

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (level == null) return true;
            SingleRecipeInput ri = new SingleRecipeInput(stack);
            return level.getRecipeManager().getRecipeFor(ModRecipeTypes.RESONATOR.get(), ri, level).isPresent();
        }
    };

    private final ItemStackHandler output = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            updateRecipe();
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

    public ResonatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ResonatorBlockEntity tile) {
        if (level.isClientSide) return;

        if (!tile.registered && tile.ownerFrequency != 0) {
            GpManager.INSTANCE.addSource(tile);
            tile.registered = true;
        }

        tile.updateRedstoneState(level, pos);

        if (!tile.isGpPowered() || !tile.canRunByRedstone()) return;
        if (tile.currentRecipe == null) return;

        int speedLevel = tile.getSpeedLevel();
        int increment = 4 * (1 + speedLevel);
        tile.progress += increment;
        tile.setChanged();

        if (tile.progress >= tile.currentEnergy) {
            ItemStack result = tile.currentRecipe.output().copy();
            tile.input.extractItem(0, 1, false);
            ItemStack existing = tile.output.getStackInSlot(0);
            if (existing.isEmpty()) {
                tile.output.setStackInSlot(0, result);
            } else {
                existing.grow(result.getCount());
            }
            tile.progress = 0;
            tile.setChanged();
        }
    }

    private void updateRecipe() {
        if (level == null || level.isClientSide) return;
        ItemStack inputStack = input.getStackInSlot(0);
        if (inputStack.isEmpty()) {
            currentRecipe = null;
            currentEnergy = 0;
            progress = 0;
            return;
        }
        SingleRecipeInput ri = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<ResonatorRecipe>> found = level.getRecipeManager().getRecipeFor(ModRecipeTypes.RESONATOR.get(), ri, level);
        if (found.isPresent()) {
            ResonatorRecipe recipe = found.get().value();
            ItemStack outputStack = output.getStackInSlot(0);
            ItemStack recipeResult = recipe.output();
            if (!outputStack.isEmpty() && (!ItemStack.isSameItemSameComponents(outputStack, recipeResult)
                    || outputStack.getCount() + recipeResult.getCount() > outputStack.getMaxStackSize())) {
                currentRecipe = null;
                currentEnergy = 0;
                progress = 0;
                return;
            }
            currentRecipe = recipe;
            currentEnergy = recipe.energy();
        } else {
            currentRecipe = null;
            currentEnergy = 0;
            progress = 0;
        }
    }

    public ItemStackHandler getInput() { return input; }
    public ItemStackHandler getOutput() { return output; }
    public UpgradeStackHandler getUpgrades() { return upgrades; }
    public int getProgress() { return progress; }
    public int getCurrentEnergy() { return currentEnergy; }
    public int getSpeedLevel() { return upgrades.getLevel(UpgradeType.SPEED); }

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
    public float getGp() {
        int speedLevel = getSpeedLevel();
        float base = progress * 0.01f * (1 + speedLevel);
        if (speedLevel > 0) base += UpgradeType.SPEED.getPowerUse(speedLevel);
        return base;
    }

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
        updateRecipe();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.extrautils2.resonator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new ResonatorMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Input", input.serializeNBT(provider));
        tag.put("Output", output.serializeNBT(provider));
        tag.put("Upgrades", upgrades.serializeNBT(provider));
        tag.putInt("Progress", progress);
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
            if (newPower && redstoneState == RedstoneState.OPERATE_REDSTONE_PULSE) {
                redstonePulses++;
            }
            setChanged();
        }
    }
}
