package com.leclowndu93150.extrautils2.blockentity.machine;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.XUEnergyStorage;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.gui.machine.CrafterMenu;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import com.leclowndu93150.extrautils2.upgrade.UpgradeStackHandler;
import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class CrafterBlockEntity extends XUBlockEntity implements MenuProvider, IGpSource, MachineBlock.IGpMachine, MachineBlock.IDroppableInventory {

    private static final int BASE_COOLDOWN = 20;

    private final XUEnergyStorage energy = new XUEnergyStorage(20000, 80, 0, true, false);

    private int cooldown = 0;
    private int ownerFrequency = 0;
    private boolean registered = false;
    private RedstoneState redstoneState = RedstoneState.OPERATE_ALWAYS;
    private boolean redstonePowered = false;
    private int redstonePulses = 0;
    private boolean containerItemsToOutput = false;

    private final ItemStackHandler ghostSlots = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            recipeCache = null;
            recipeCacheDirty = true;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private final ItemStackHandler input = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    private final ItemStackHandler output = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };

    private final UpgradeStackHandler upgrades = new UpgradeStackHandler(EnumSet.of(UpgradeType.SPEED), () -> {
        setChanged();
        if (ownerFrequency != 0) GpManager.INSTANCE.markSourceDirty(this);
    });

    private @Nullable RecipeHolder<CraftingRecipe> recipeCache = null;
    private boolean recipeCacheDirty = true;

    public CrafterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRAFTER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrafterBlockEntity tile) {
        if (level.isClientSide) return;

        if (!tile.registered && tile.ownerFrequency != 0) {
            GpManager.INSTANCE.addSource(tile);
            tile.registered = true;
        }

        tile.updateRedstoneState(level, pos);

        boolean active = false;
        if (tile.isGpPowered() && tile.canRunByRedstone()) {
            RecipeHolder<CraftingRecipe> recipe = tile.findRecipe(level);
            if (recipe != null && tile.hasRequiredInputs(level, recipe)) {
                int speedFactor = 1 + tile.getSpeedLevel();
                int cost = 20 * speedFactor;
                if (tile.energy.getEnergyStored() >= cost) {
                    tile.energy.setEnergy(tile.energy.getEnergyStored() - cost);
                    tile.cooldown += speedFactor;
                    active = true;

                    if (tile.cooldown >= BASE_COOLDOWN) {
                        tile.cooldown = 0;
                        tile.doCraft(level, recipe);
                        if (tile.redstoneState == RedstoneState.OPERATE_REDSTONE_PULSE && tile.redstonePulses > 0) {
                            tile.redstonePulses--;
                        }
                    }
                    tile.setChanged();
                }
            } else {
                tile.cooldown = 0;
            }
        } else {
            tile.cooldown = 0;
        }

        boolean wasActive = state.getValue(MachineBlock.ACTIVE);
        if (wasActive != active) {
            level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, active), 3);
        }
    }

    private java.util.Map<net.minecraft.world.item.Item, Integer> countNeeded() {
        java.util.Map<net.minecraft.world.item.Item, Integer> needed = new java.util.HashMap<>();
        for (int i = 0; i < 9; i++) {
            ItemStack ghost = ghostSlots.getStackInSlot(i);
            if (!ghost.isEmpty()) {
                needed.merge(ghost.getItem(), 1, Integer::sum);
            }
        }
        return needed;
    }

    private java.util.Map<net.minecraft.world.item.Item, Integer> countAvailable() {
        java.util.Map<net.minecraft.world.item.Item, Integer> available = new java.util.HashMap<>();
        for (int i = 0; i < 9; i++) {
            ItemStack inSlot = input.getStackInSlot(i);
            if (!inSlot.isEmpty()) {
                available.merge(inSlot.getItem(), inSlot.getCount(), Integer::sum);
            }
        }
        return available;
    }

    private boolean hasRequiredInputs(Level level, RecipeHolder<CraftingRecipe> recipe) {
        CraftingContainer testContainer = createCraftingContainer();
        loadGhostInto(testContainer);
        ItemStack result = recipe.value().assemble(testContainer.asCraftInput(), level.registryAccess());
        if (result.isEmpty()) return false;

        java.util.Map<net.minecraft.world.item.Item, Integer> needed = countNeeded();
        java.util.Map<net.minecraft.world.item.Item, Integer> available = countAvailable();
        for (var entry : needed.entrySet()) {
            if (available.getOrDefault(entry.getKey(), 0) < entry.getValue()) return false;
        }

        ItemStack[] outputCopy = new ItemStack[9];
        for (int i = 0; i < 9; i++) outputCopy[i] = output.getStackInSlot(i).copy();

        if (!canInsertInto(outputCopy, result)) return false;

        NonNullList<ItemStack> remainders = recipe.value().getRemainingItems(testContainer.asCraftInput());

        ItemStack[] inputCopy = new ItemStack[9];
        for (int i = 0; i < 9; i++) inputCopy[i] = input.getStackInSlot(i).copy();
        for (var entry : needed.entrySet()) {
            int rem = entry.getValue();
            for (int j = 0; j < 9 && rem > 0; j++) {
                if (!inputCopy[j].isEmpty() && inputCopy[j].is(entry.getKey())) {
                    int take = Math.min(rem, inputCopy[j].getCount());
                    inputCopy[j].shrink(take);
                    if (inputCopy[j].isEmpty()) inputCopy[j] = ItemStack.EMPTY;
                    rem -= take;
                }
            }
        }

        for (int i = 0; i < remainders.size(); i++) {
            ItemStack remainder = remainders.get(i);
            if (remainder.isEmpty()) continue;
            if (containerItemsToOutput) {
                if (!canInsertInto(outputCopy, remainder)) return false;
            } else {
                if (!canInsertInto(inputCopy, remainder)) {
                    if (!canInsertInto(outputCopy, remainder)) return false;
                }
            }
        }

        return true;
    }

    private boolean canInsertInto(ItemStack[] slots, ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < slots.length && !remaining.isEmpty(); i++) {
            if (slots[i].isEmpty()) {
                slots[i] = remaining.copy();
                return true;
            } else if (ItemStack.isSameItemSameComponents(slots[i], remaining)) {
                int space = slots[i].getMaxStackSize() - slots[i].getCount();
                if (space > 0) {
                    int insert = Math.min(space, remaining.getCount());
                    slots[i].grow(insert);
                    remaining.shrink(insert);
                }
            }
        }
        return remaining.isEmpty();
    }

    private void doCraft(Level level, RecipeHolder<CraftingRecipe> recipe) {
        java.util.Map<net.minecraft.world.item.Item, Integer> toTake = countNeeded();

        for (var entry : toTake.entrySet()) {
            int remaining = entry.getValue();
            for (int j = 0; j < 9 && remaining > 0; j++) {
                ItemStack inSlot = input.getStackInSlot(j);
                if (!inSlot.isEmpty() && inSlot.is(entry.getKey())) {
                    int take = Math.min(remaining, inSlot.getCount());
                    input.extractItem(j, take, false);
                    remaining -= take;
                }
            }
            if (remaining > 0) return;
        }

        CraftingContainer ghostContainer = createCraftingContainer();
        loadGhostInto(ghostContainer);
        ItemStack craftResult = recipe.value().assemble(ghostContainer.asCraftInput(), level.registryAccess());
        if (craftResult.isEmpty()) return;
        insertResult(craftResult);

        NonNullList<ItemStack> remainders = recipe.value().getRemainingItems(ghostContainer.asCraftInput());
        for (int i = 0; i < remainders.size(); i++) {
            ItemStack remainder = remainders.get(i);
            if (remainder.isEmpty()) continue;
            if (containerItemsToOutput) {
                insertResult(remainder);
            } else {
                ItemStack leftover = insertIntoInput(remainder);
                if (!leftover.isEmpty()) {
                    insertResult(leftover);
                }
            }
        }
    }

    private @Nullable RecipeHolder<CraftingRecipe> findRecipe(Level level) {
        if (recipeCache != null && !recipeCacheDirty) return recipeCache;

        boolean hasGhost = false;
        for (int i = 0; i < 9; i++) {
            if (!ghostSlots.getStackInSlot(i).isEmpty()) { hasGhost = true; break; }
        }
        if (!hasGhost) { recipeCache = null; recipeCacheDirty = false; return null; }

        CraftingContainer container = createCraftingContainer();
        loadGhostInto(container);
        Optional<RecipeHolder<CraftingRecipe>> found = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container.asCraftInput(), level);
        recipeCache = found.orElse(null);
        recipeCacheDirty = false;
        return recipeCache;
    }

    private CraftingContainer createCraftingContainer() {
        return new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player p, int i) { return ItemStack.EMPTY; }
            @Override
            public boolean stillValid(Player p) { return false; }
        }, 3, 3);
    }

    private void loadGhostInto(CraftingContainer container) {
        for (int i = 0; i < 9; i++) {
            container.setItem(i, ghostSlots.getStackInSlot(i).copy());
        }
    }

    private void insertResult(ItemStack result) {
        ItemStack remaining = result.copy();
        for (int i = 0; i < 9 && !remaining.isEmpty(); i++) {
            ItemStack inSlot = output.getStackInSlot(i);
            if (inSlot.isEmpty()) {
                output.setStackInSlot(i, remaining.copy());
                return;
            } else if (ItemStack.isSameItemSameComponents(inSlot, remaining)) {
                int space = inSlot.getMaxStackSize() - inSlot.getCount();
                if (space > 0) {
                    int insert = Math.min(space, remaining.getCount());
                    ItemStack grown = inSlot.copy();
                    grown.grow(insert);
                    output.setStackInSlot(i, grown);
                    remaining.shrink(insert);
                }
            }
        }
    }

    private ItemStack insertIntoInput(ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < 9 && !remaining.isEmpty(); i++) {
            remaining = input.insertItem(i, remaining, false);
        }
        return remaining;
    }

    public ItemStack getGhostOutput() {
        if (level == null) return ItemStack.EMPTY;
        RecipeHolder<CraftingRecipe> recipe = findRecipe(level);
        if (recipe == null) return ItemStack.EMPTY;
        CraftingContainer container = createCraftingContainer();
        loadGhostInto(container);
        return recipe.value().assemble(container.asCraftInput(), level.registryAccess());
    }

    public ItemStackHandler getGhostSlots() { return ghostSlots; }
    public ItemStackHandler getInput() { return input; }
    public ItemStackHandler getOutput() { return output; }
    public UpgradeStackHandler getUpgrades() { return upgrades; }
    public XUEnergyStorage getEnergyStorage() { return energy; }
    public int getSpeedLevel() { return upgrades.getLevel(UpgradeType.SPEED); }
    public int getCooldown() { return cooldown; }
    public boolean isContainerItemsToOutput() { return containerItemsToOutput; }

    public void toggleContainerMode() {
        containerItemsToOutput = !containerItemsToOutput;
        setChanged();
    }

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
            GpManager.INSTANCE.addSource(this); registered = true;
        }
        recipeCacheDirty = true;
    }

    @Override public Component getDisplayName() { return Component.translatable("block.extrautils2.crafter"); }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CrafterMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("GhostSlots", ghostSlots.serializeNBT(provider));
        tag.put("Input", input.serializeNBT(provider));
        tag.put("Output", output.serializeNBT(provider));
        tag.put("Upgrades", upgrades.serializeNBT(provider));
        tag.putInt("Cooldown", cooldown);
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("OwnerFreq", ownerFrequency);
        tag.putInt("RedstoneState", redstoneState.ordinal());
        tag.putBoolean("RedstonePowered", redstonePowered);
        tag.putInt("RedstonePulses", redstonePulses);
        tag.putBoolean("ContainerToOutput", containerItemsToOutput);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        ghostSlots.deserializeNBT(provider, tag.getCompound("GhostSlots"));
        input.deserializeNBT(provider, tag.getCompound("Input"));
        output.deserializeNBT(provider, tag.getCompound("Output"));
        if (tag.contains("Upgrades")) upgrades.deserializeNBT(provider, tag.getCompound("Upgrades"));
        cooldown = tag.getInt("Cooldown");
        energy.setEnergy(tag.getInt("Energy"));
        ownerFrequency = tag.getInt("OwnerFreq");
        int rs = tag.getInt("RedstoneState");
        RedstoneState[] values = RedstoneState.values();
        redstoneState = rs >= 0 && rs < values.length ? values[rs] : RedstoneState.OPERATE_ALWAYS;
        redstonePowered = tag.getBoolean("RedstonePowered");
        redstonePulses = tag.getInt("RedstonePulses");
        containerItemsToOutput = tag.getBoolean("ContainerToOutput");
        recipeCacheDirty = true;
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
