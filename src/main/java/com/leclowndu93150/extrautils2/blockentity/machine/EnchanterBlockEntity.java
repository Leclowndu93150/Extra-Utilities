package com.leclowndu93150.extrautils2.blockentity.machine;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.XUEnergyStorage;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.gui.machine.EnchanterMenu;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.recipe.EnchanterRecipe;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import com.leclowndu93150.extrautils2.registry.ModRecipeTypes;
import com.leclowndu93150.extrautils2.upgrade.UpgradeStackHandler;
import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class EnchanterBlockEntity extends XUBlockEntity implements MenuProvider, IGpSource, MachineBlock.IGpMachine, MachineBlock.IDroppableInventory {

    private final XUEnergyStorage energy = new XUEnergyStorage(100000, 1000, 0, true, false);

    private int progress = 0;
    private int totalTime = 0;
    private int energyPerTick = 2;
    private int bookshelfPower = 0;
    private int bookshelfCheckTimer = 0;

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
            return true;
        }
    };

    private final ItemStackHandler catalyst = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            updateRecipe();
        }
    };

    private final ItemStackHandler output = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };

    private final UpgradeStackHandler upgrades = new UpgradeStackHandler(EnumSet.of(UpgradeType.SPEED), () -> {
        setChanged();
        if (ownerFrequency != 0) GpManager.INSTANCE.markSourceDirty(this);
    });

    private @Nullable EnchanterRecipe currentRecipe = null;

    public EnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MACHINE_ENCHANTER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnchanterBlockEntity tile) {
        if (level.isClientSide) return;

        if (!tile.registered && tile.ownerFrequency != 0) {
            GpManager.INSTANCE.addSource(tile);
            tile.registered = true;
        }

        tile.updateRedstoneState(level, pos);

        boolean active = false;
        if (tile.isGpPowered() && tile.canRunByRedstone() && tile.currentRecipe != null
                && tile.output.getStackInSlot(0).isEmpty()) {
            if (--tile.bookshelfCheckTimer <= 0) {
                tile.bookshelfPower = countBookshelfPower(level, pos);
                tile.bookshelfCheckTimer = 40;
            }
            if (tile.bookshelfPower >= 15) {
                int speedFactor = 1 + tile.getSpeedLevel();
                int cost = tile.energyPerTick * speedFactor;
                if (tile.energy.getEnergyStored() >= cost) {
                    tile.energy.setEnergy(tile.energy.getEnergyStored() - cost);
                    tile.progress += speedFactor;
                    active = true;
                    tile.setChanged();

                    if (tile.progress >= tile.totalTime) {
                        if (tile.currentRecipe.isTransformation()) {
                            int inCount = tile.currentRecipe.inputCount();
                            tile.input.extractItem(0, inCount, false);
                            tile.catalyst.extractItem(0, 1, false);
                            ItemStack out = tile.currentRecipe.result().get().copy();
                            out.setCount(tile.currentRecipe.outputCount());
                            tile.output.setStackInSlot(0, out);
                        } else {
                            boolean lowest = tile.currentRecipe.isLowest();
                            ItemStack itemToEnchant = tile.input.extractItem(0, 1, false);
                            tile.catalyst.extractItem(0, 1, false);
                            ItemStack enchanted = applyEnchantments(level, itemToEnchant, lowest);
                            tile.output.setStackInSlot(0, enchanted);
                        }
                        tile.progress = 0;
                        if (tile.redstoneState == RedstoneState.OPERATE_REDSTONE_PULSE && tile.redstonePulses > 0) {
                            tile.redstonePulses--;
                        }
                    }
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

    private static int countBookshelfPower(Level level, BlockPos pos) {
        float power = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                if (!isPassable(level, pos.offset(dz, 0, dx)) || !isPassable(level, pos.offset(dz, 1, dx))) continue;
                power += enchantPower(level, pos.offset(dz * 2, 0, dx * 2));
                power += enchantPower(level, pos.offset(dz * 2, 1, dx * 2));
                if (dz != 0 && dx != 0) {
                    power += enchantPower(level, pos.offset(dz * 2, 0, dx));
                    power += enchantPower(level, pos.offset(dz * 2, 1, dx));
                    power += enchantPower(level, pos.offset(dz, 0, dx * 2));
                    power += enchantPower(level, pos.offset(dz, 1, dx * 2));
                }
                if (power >= 15) return 15;
            }
        }
        return (int) power;
    }

    private static float enchantPower(Level level, BlockPos p) {
        return level.getBlockState(p).getEnchantPowerBonus(level, p);
    }

    private static boolean isPassable(Level level, BlockPos pos) {
        return !level.getBlockState(pos).isCollisionShapeFullBlock(level, pos);
    }

    private static ItemStack applyEnchantments(Level level, ItemStack stack, boolean lowest) {
        boolean isBook = stack.is(Items.BOOK);
        if (isBook) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        RandomSource random = level.random;
        ItemStack target = stack;

        List<Holder.Reference<Enchantment>> applicable = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .holders()
                .filter(h -> !h.is(EnchantmentTags.TREASURE))
                .filter(h -> isBook || h.value().canEnchant(target))
                .toList();

        if (applicable.isEmpty()) return stack;

        ArrayList<Holder<Enchantment>> pool = new ArrayList<>(applicable);
        int enchantability = stack.getItem().getEnchantmentValue(stack);
        if (enchantability <= 0) enchantability = 1;

        int level_ = 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);

        Holder<Enchantment> picked = pool.get(random.nextInt(pool.size()));
        int lvl = lowest ? picked.value().getMinLevel() : picked.value().getMaxLevel();
        stack.enchant(picked, lvl);

        while (random.nextInt(50) <= level_) {
            ItemStack finalStack = stack;
            pool.removeIf(h -> {
                for (var existing : EnchantmentHelper.getEnchantmentsForCrafting(finalStack).keySet()) {
                    if (!Enchantment.areCompatible(existing, h)) return true;
                }
                return false;
            });
            if (pool.isEmpty()) break;

            picked = pool.get(random.nextInt(pool.size()));
            lvl = lowest ? picked.value().getMinLevel() : picked.value().getMaxLevel();
            stack.enchant(picked, lvl);
            level_ /= 2;
        }

        return stack;
    }

    private void updateRecipe() {
        if (level == null || level.isClientSide) return;
        ItemStack inputStack = input.getStackInSlot(0);
        ItemStack catalystStack = catalyst.getStackInSlot(0);
        if (inputStack.isEmpty() || catalystStack.isEmpty()) {
            currentRecipe = null;
            totalTime = 0;
            return;
        }
        if (!output.getStackInSlot(0).isEmpty()) {
            currentRecipe = null;
            totalTime = 0;
            return;
        }
        EnchanterRecipe.EnchanterInput ri = new EnchanterRecipe.EnchanterInput(inputStack, catalystStack);
        Optional<RecipeHolder<EnchanterRecipe>> found = level.getRecipeManager().getRecipeFor(ModRecipeTypes.ENCHANTER.get(), ri, level);
        if (found.isPresent()) {
            EnchanterRecipe recipe = found.get().value();
            currentRecipe = recipe;
            totalTime = recipe.processingTime();
            energyPerTick = recipe.energy() / recipe.processingTime();
        } else {
            currentRecipe = null;
            totalTime = 0;
        }
    }

    public ItemStackHandler getInput() { return input; }
    public ItemStackHandler getCatalyst() { return catalyst; }
    public ItemStackHandler getOutput() { return output; }
    public UpgradeStackHandler getUpgrades() { return upgrades; }
    public XUEnergyStorage getEnergyStorage() { return energy; }
    public int getProgress() { return progress; }
    public int getTotalTime() { return totalTime; }
    public int getEnergyStored() { return energy.getEnergyStored(); }
    public int getEnergyCapacity() { return energy.getMaxEnergyStored(); }
    public int getSpeedLevel() { return upgrades.getLevel(UpgradeType.SPEED); }
    public int getBookshelfPower() { return bookshelfPower; }

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
    public void cycleRedstoneState(boolean allowPulse) { redstoneState = redstoneState.next(allowPulse); redstonePulses = 0; setChanged(); }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public IItemHandler[] getDroppableInventories() {
        return new IItemHandler[]{input, catalyst, output, upgrades};
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
            GpManager.INSTANCE.addSource(this); registered = true;
        }
        updateRecipe();
    }

    @Override public Component getDisplayName() { return Component.translatable("block.extrautils2.machine_enchanter"); }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new EnchanterMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Input", input.serializeNBT(provider));
        tag.put("Catalyst", catalyst.serializeNBT(provider));
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
        catalyst.deserializeNBT(provider, tag.getCompound("Catalyst"));
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
