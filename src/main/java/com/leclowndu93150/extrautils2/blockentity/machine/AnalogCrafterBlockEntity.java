package com.leclowndu93150.extrautils2.blockentity.machine;

import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.gui.machine.AnalogCrafterMenu;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import com.leclowndu93150.extrautils2.util.RedstoneState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.Optional;

public class AnalogCrafterBlockEntity extends XUBlockEntity implements MenuProvider, MachineBlock.IDroppableInventory {

    public static final int DISABLED = -1;
    public static final int ANY_FACE = 7;

    private int progress = 0;
    private int maxProgress = 0;
    private RedstoneState redstoneState = RedstoneState.OPERATE_ALWAYS;
    private boolean redstonePowered = false;
    private int redstonePulses = 0;
    private boolean sticky = false;
    private boolean spread = false;
    private final byte[] slotSides = new byte[9];

    private final ItemStackHandler grid = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            recipeCache = null;
            recipeDirty = true;
        }
    };

    private final ItemStackHandler output = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };

    private @Nullable RecipeHolder<CraftingRecipe> recipeCache = null;
    private boolean recipeDirty = true;

    public AnalogCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANALOG_CRAFTER.get(), pos, state);
        for (int i = 0; i < 9; i++) slotSides[i] = ANY_FACE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AnalogCrafterBlockEntity tile) {
        if (level.isClientSide) return;
        if (level.getGameTime() % 4L != 0L) return;

        tile.updateRedstoneState(level, pos);

        if (tile.spread) {
            tile.spreadItems();
        }

        boolean active = false;
        if (tile.canRunByRedstone()) {
            RecipeHolder<CraftingRecipe> recipe = tile.findRecipe(level);
            if (recipe != null) {
                ItemStack outputSlot = tile.output.getStackInSlot(0);
                CraftingContainer container = tile.createCraftingContainer();
                tile.loadGridInto(container);
                ItemStack result = recipe.value().assemble(container.asCraftInput(), level.registryAccess());

                boolean outputFits = result != null && !result.isEmpty() &&
                        (outputSlot.isEmpty() ||
                                (ItemStack.isSameItemSameComponents(outputSlot, result) &&
                                        outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize()));

                if (outputFits && (!tile.sticky || tile.canCraftSticky())) {
                    if (tile.maxProgress == 0) {
                        tile.maxProgress = result.getCount() * 20;
                        tile.progress = 0;
                    }

                    tile.progress += 4;
                    active = true;

                    if (tile.progress >= tile.maxProgress) {
                        tile.doCraft(level, recipe, container, result);
                        tile.progress = 0;
                        tile.maxProgress = 0;
                        if (tile.redstoneState == RedstoneState.OPERATE_REDSTONE_PULSE && tile.redstonePulses > 0) {
                            tile.redstonePulses--;
                        }
                    }
                    tile.setChanged();
                } else {
                    tile.progress = 0;
                    tile.maxProgress = 0;
                }
            } else {
                tile.progress = 0;
                tile.maxProgress = 0;
            }
        } else {
            tile.progress = 0;
            tile.maxProgress = 0;
        }

        boolean wasActive = state.getValue(MachineBlock.ACTIVE);
        if (wasActive != active) {
            level.setBlock(pos, state.setValue(MachineBlock.ACTIVE, active), 3);
        }
    }

    private boolean canCraftSticky() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = grid.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getCount() <= 1 && stack.getMaxStackSize() > 1) {
                return false;
            }
        }
        return true;
    }

    private void doCraft(Level level, RecipeHolder<CraftingRecipe> recipe, CraftingContainer container, ItemStack result) {
        NonNullList<ItemStack> remainders = recipe.value().getRemainingItems(container.asCraftInput());

        for (int i = 0; i < 9; i++) {
            ItemStack inSlot = grid.getStackInSlot(i);
            if (!inSlot.isEmpty()) {
                if (inSlot.getCount() <= 1) {
                    grid.setStackInSlot(i, ItemStack.EMPTY);
                } else {
                    ItemStack shrunk = inSlot.copy();
                    shrunk.shrink(1);
                    grid.setStackInSlot(i, shrunk);
                }
            }
        }

        ItemStack outputStack = output.getStackInSlot(0);
        if (outputStack.isEmpty()) {
            output.setStackInSlot(0, result.copy());
        } else {
            ItemStack grown = outputStack.copy();
            grown.grow(result.getCount());
            output.setStackInSlot(0, grown);
        }

        for (int i = 0; i < remainders.size(); i++) {
            ItemStack remainder = remainders.get(i);
            if (remainder.isEmpty()) continue;
            ItemStack existing = grid.getStackInSlot(i);
            if (existing.isEmpty()) {
                grid.setStackInSlot(i, remainder);
            } else if (ItemStack.isSameItemSameComponents(existing, remainder) &&
                    existing.getCount() + remainder.getCount() <= existing.getMaxStackSize()) {
                ItemStack merged = existing.copy();
                merged.grow(remainder.getCount());
                grid.setStackInSlot(i, merged);
            } else {
                output.insertItem(0, remainder, false);
            }
        }

        recipeCache = null;
        recipeDirty = true;
    }

    private void spreadItems() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = grid.getStackInSlot(i);
            if (stack.isEmpty() || stack.getCount() <= 1) continue;

            for (int j = i + 1; j < 9; j++) {
                ItemStack other = grid.getStackInSlot(j);
                if (other.isEmpty() || !ItemStack.isSameItemSameComponents(stack, other)) continue;
                int total = stack.getCount() + other.getCount();
                int half = total / 2;
                int rest = total - half;
                ItemStack newI = stack.copy();
                newI.setCount(rest);
                grid.setStackInSlot(i, newI);
                ItemStack newJ = other.copy();
                newJ.setCount(half);
                grid.setStackInSlot(j, newJ);
                stack = newI;
            }
        }
    }

    private @Nullable RecipeHolder<CraftingRecipe> findRecipe(Level level) {
        if (recipeCache != null && !recipeDirty) return recipeCache;

        boolean hasItems = false;
        for (int i = 0; i < 9; i++) {
            if (!grid.getStackInSlot(i).isEmpty()) { hasItems = true; break; }
        }
        if (!hasItems) { recipeCache = null; recipeDirty = false; return null; }

        CraftingContainer container = createCraftingContainer();
        loadGridInto(container);
        Optional<RecipeHolder<CraftingRecipe>> found = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container.asCraftInput(), level);
        recipeCache = found.orElse(null);
        recipeDirty = false;
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

    private void loadGridInto(CraftingContainer container) {
        for (int i = 0; i < 9; i++) {
            container.setItem(i, grid.getStackInSlot(i).copy());
        }
    }

    public IItemHandler getSidedHandler(Direction side) {
        return new SidedGridHandler(side);
    }

    public ItemStackHandler getGrid() { return grid; }
    public ItemStackHandler getOutput() { return output; }
    public byte[] getSlotSides() { return slotSides; }
    public int getProgress() { return progress; }
    public int getMaxProgress() { return maxProgress; }
    public boolean isSticky() { return sticky; }
    public boolean isSpread() { return spread; }
    public RedstoneState getRedstoneState() { return redstoneState; }

    public void cycleRedstoneState(boolean allowPulse) {
        redstoneState = redstoneState.next(allowPulse);
        redstonePulses = 0;
        setChanged();
    }

    public void toggleSticky() { sticky = !sticky; setChanged(); }
    public void toggleSpread() { spread = !spread; setChanged(); }

    public void cycleSlotSide(int slot) {
        if (slot < 0 || slot >= 9) return;
        byte current = slotSides[slot];
        slotSides[slot] = switch (current) {
            case ANY_FACE -> DISABLED;
            case DISABLED -> 0;
            case 5 -> ANY_FACE;
            default -> (byte) (current + 1);
        };
        setChanged();
    }

    public void cycleSlotSideReverse(int slot) {
        if (slot < 0 || slot >= 9) return;
        byte current = slotSides[slot];
        slotSides[slot] = switch (current) {
            case ANY_FACE -> 5;
            case DISABLED -> ANY_FACE;
            case 0 -> DISABLED;
            default -> (byte) (current - 1);
        };
        setChanged();
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public IItemHandler[] getDroppableInventories() {
        return new IItemHandler[]{grid, output};
    }

    @Override public Component getDisplayName() { return Component.translatable("block.extrautils2.analog_crafter"); }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new AnalogCrafterMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Grid", grid.serializeNBT(provider));
        tag.put("Output", output.serializeNBT(provider));
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putInt("RedstoneState", redstoneState.ordinal());
        tag.putBoolean("RedstonePowered", redstonePowered);
        tag.putInt("RedstonePulses", redstonePulses);
        tag.putBoolean("Sticky", sticky);
        tag.putBoolean("Spread", spread);
        tag.putByteArray("SlotSides", slotSides.clone());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        grid.deserializeNBT(provider, tag.getCompound("Grid"));
        output.deserializeNBT(provider, tag.getCompound("Output"));
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        int rs = tag.getInt("RedstoneState");
        RedstoneState[] values = RedstoneState.values();
        redstoneState = rs >= 0 && rs < values.length ? values[rs] : RedstoneState.OPERATE_ALWAYS;
        redstonePowered = tag.getBoolean("RedstonePowered");
        redstonePulses = tag.getInt("RedstonePulses");
        sticky = tag.getBoolean("Sticky");
        spread = tag.getBoolean("Spread");
        if (tag.contains("SlotSides")) {
            byte[] loaded = tag.getByteArray("SlotSides");
            System.arraycopy(loaded, 0, slotSides, 0, Math.min(loaded.length, 9));
        }
        recipeDirty = true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        recipeDirty = true;
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

    private class SidedGridHandler implements IItemHandler {
        private final Direction side;
        private final int[] accessibleSlots;
        private final int outputSlotIndex;

        SidedGridHandler(Direction side) {
            this.side = side;
            int count = 0;
            for (int i = 0; i < 9; i++) {
                if (isSlotAccessible(i)) count++;
            }
            accessibleSlots = new int[count];
            int idx = 0;
            for (int i = 0; i < 9; i++) {
                if (isSlotAccessible(i)) accessibleSlots[idx++] = i;
            }
            outputSlotIndex = count;
        }

        private boolean isSlotAccessible(int slot) {
            byte config = slotSides[slot];
            if (config == DISABLED) return false;
            if (config == ANY_FACE) return true;
            return config == side.ordinal();
        }

        @Override
        public int getSlots() {
            return accessibleSlots.length + 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot == outputSlotIndex) return output.getStackInSlot(0);
            if (slot < 0 || slot >= accessibleSlots.length) return ItemStack.EMPTY;
            return grid.getStackInSlot(accessibleSlots[slot]);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == outputSlotIndex) return stack;
            if (slot < 0 || slot >= accessibleSlots.length) return stack;
            int mapped = accessibleSlots[slot];
            ItemStack existing = grid.getStackInSlot(mapped);
            if (existing.isEmpty()) return stack;
            if (!ItemStack.isSameItemSameComponents(existing, stack)) return stack;
            return grid.insertItem(mapped, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == outputSlotIndex) return output.extractItem(0, amount, simulate);
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == outputSlotIndex) return output.getSlotLimit(0);
            if (slot < 0 || slot >= accessibleSlots.length) return 0;
            return grid.getSlotLimit(accessibleSlots[slot]);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == outputSlotIndex) return false;
            if (slot < 0 || slot >= accessibleSlots.length) return false;
            ItemStack existing = grid.getStackInSlot(accessibleSlots[slot]);
            if (existing.isEmpty()) return false;
            return ItemStack.isSameItemSameComponents(existing, stack);
        }
    }
}
