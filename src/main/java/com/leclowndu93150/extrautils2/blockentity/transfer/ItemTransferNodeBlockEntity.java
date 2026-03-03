package com.leclowndu93150.extrautils2.blockentity.transfer;

import com.leclowndu93150.extrautils2.api.items.IItemFilter;
import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.block.transfer.TransferHelper;
import com.leclowndu93150.extrautils2.block.transfer.TransferNodeBlock;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.gui.transfer.TransferNodeMenu;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import com.leclowndu93150.extrautils2.upgrade.UpgradeStackHandler;
import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ItemTransferNodeBlockEntity extends XUBlockEntity implements MenuProvider, IGpSource, MachineBlock.IGpMachine, MachineBlock.IDroppableInventory {
    public static final int UPGRADE_SLOTS = 6;

    private final ItemStackHandler buffer = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler filterSlot = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof IItemFilter;
        }
    };

    private int ownerFrequency = 0;
    private boolean registered = false;

    private final UpgradeStackHandler upgrades = new UpgradeStackHandler(
            UPGRADE_SLOTS,
            EnumSet.of(UpgradeType.SPEED, UpgradeType.STACK_SIZE, UpgradeType.MINING), () -> {
        setChanged();
        if (ownerFrequency != 0) GpManager.INSTANCE.markSourceDirty(this);
    });

    private final TransferNodePing ping = new TransferNodePing();
    private int cooldown = 20;

    public ItemTransferNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_TRANSFER_NODE.get(), pos, state);
    }

    public boolean isRetrieval() {
        if (getBlockState().getBlock() instanceof TransferNodeBlock node) {
            return node.isRetrieval();
        }
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ItemTransferNodeBlockEntity tile) {
        if (level.isClientSide) return;

        if (!tile.registered && tile.ownerFrequency != 0) {
            GpManager.INSTANCE.addSource(tile);
            tile.registered = true;
        }

        if (!tile.ping.isInitialized()) {
            Direction facing = state.getValue(TransferNodeBlock.FACING);
            tile.ping.init(pos, facing.getOpposite());
        }

        int speedLevel = tile.upgrades.getLevel(UpgradeType.SPEED);
        tile.cooldown -= 1 + speedLevel;
        if (tile.cooldown > 0) return;
        tile.cooldown += 20;

        if (!tile.isGpPowered()) return;

        if (tile.isRetrieval()) {
            tile.pushBufferToAttached();
            if (tile.buffer.getStackInSlot(0).isEmpty()) {
                tile.tryRetrieveAtPingPos();
            }
            if (!tile.buffer.getStackInSlot(0).isEmpty()) {
                tile.pushBufferToAttached();
            }
            if (tile.buffer.getStackInSlot(0).isEmpty()) {
                tile.ping.resetPosition();
            } else {
                tile.ping.advance(level);
            }
        } else {
            tile.extractIntoBuffer();
            if (!tile.buffer.getStackInSlot(0).isEmpty()) {
                tile.tryDeliverAtPingPos();
            }
            if (tile.buffer.getStackInSlot(0).isEmpty()) {
                tile.ping.resetPosition();
            } else {
                if (!tile.ping.advance(level)) {
                    tile.ping.resetPosition();
                }
            }
        }

        tile.setChanged();
    }

    private Direction getAttachedDirection() {
        return getBlockState().getValue(TransferNodeBlock.FACING);
    }

    private BlockPos getAttachedPos() {
        return worldPosition.relative(getAttachedDirection());
    }

    private void extractIntoBuffer() {
        Direction facing = getAttachedDirection();
        IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, getAttachedPos(), facing.getOpposite());
        if (inv == null) {
            mineWorldItemSource();
            return;
        }

        int maxTransfer = getMaxTransfer();

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack buffered = buffer.getStackInSlot(0);
            if (!buffered.isEmpty() && buffered.getCount() >= buffered.getMaxStackSize()) {
                return;
            }

            ItemStack simulated = inv.extractItem(i, maxTransfer, true);
            if (simulated.isEmpty()) continue;
            if (!matchesFilter(simulated)) continue;
            ItemStack remainder = buffer.insertItem(0, simulated, true);
            int transferable = simulated.getCount() - remainder.getCount();
            if (transferable <= 0) continue;

            ItemStack extracted = inv.extractItem(i, transferable, false);
            if (!extracted.isEmpty()) {
                buffer.insertItem(0, extracted, false);
                return;
            }
        }
    }

    private void mineWorldItemSource() {
        int miningLevel = upgrades.getLevel(UpgradeType.MINING);
        if (miningLevel <= 0) return;

        ItemStack generated = tryGenerateCobblestone(miningLevel);
        if (generated.isEmpty() || !matchesFilter(generated)) return;

        buffer.insertItem(0, generated, false);
    }

    private ItemStack tryGenerateCobblestone(int miningLevel) {
        BlockPos targetPos = getAttachedPos();
        BlockState state = level.getBlockState(targetPos);
        if (!state.is(Blocks.COBBLESTONE)) return ItemStack.EMPTY;
        if (!hasAdjacentSource(targetPos, Fluids.WATER) || !hasAdjacentSource(targetPos, Fluids.LAVA)) return ItemStack.EMPTY;
        return new ItemStack(Blocks.COBBLESTONE.asItem(), miningLevel);
    }

    private boolean hasAdjacentSource(BlockPos targetPos, net.minecraft.world.level.material.Fluid fluid) {
        for (Direction direction : getHorizontalOrthogonalDirections(getAttachedDirection())) {
            FluidState fluidState = level.getFluidState(targetPos.relative(direction));
            if (fluidState.isSource() && fluidState.getType().isSame(fluid)) {
                return true;
            }
        }
        return false;
    }

    private static Direction[] getHorizontalOrthogonalDirections(Direction facing) {
        return switch (facing.getAxis()) {
            case X -> new Direction[]{Direction.NORTH, Direction.SOUTH};
            case Z -> new Direction[]{Direction.WEST, Direction.EAST};
            case Y -> new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
        };
    }

    private void tryDeliverAtPingPos() {
        BlockPos pingPos = ping.getCurrentPos();
        if (pingPos == null) return;
        ItemStack bufferStack = buffer.getStackInSlot(0);
        if (bufferStack.isEmpty()) return;

        for (Direction dir : TransferHelper.getShuffledDirections()) {
            BlockPos neighbor = pingPos.relative(dir);
            if (TransferHelper.isPipe(level, neighbor)) continue;
            if (neighbor.equals(worldPosition)) continue;

            IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, neighbor, dir.getOpposite());
            if (inv == null) continue;

            ItemStack remainder = ItemHandlerHelper.insertItemStacked(inv, bufferStack.copy(), false);
            if (remainder.getCount() < bufferStack.getCount()) {
                if (remainder.isEmpty()) {
                    buffer.setStackInSlot(0, ItemStack.EMPTY);
                } else {
                    buffer.setStackInSlot(0, remainder);
                }
                return;
            }
        }
    }

    private void tryRetrieveAtPingPos() {
        BlockPos pingPos = ping.getCurrentPos();
        if (pingPos == null) return;

        int maxTransfer = getMaxTransfer();

        for (Direction dir : TransferHelper.getShuffledDirections()) {
            BlockPos neighbor = pingPos.relative(dir);
            if (TransferHelper.isPipe(level, neighbor)) continue;
            if (neighbor.equals(worldPosition)) continue;

            IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, neighbor, dir.getOpposite());
            if (inv == null) continue;

            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack inSlot = inv.getStackInSlot(i);
                if (inSlot.isEmpty()) continue;
                if (!matchesFilter(inSlot)) continue;
                ItemStack extracted = inv.extractItem(i, maxTransfer, false);
                if (!extracted.isEmpty()) {
                    buffer.setStackInSlot(0, extracted);
                    return;
                }
            }
        }
    }

    private void pushBufferToAttached() {
        ItemStack bufferStack = buffer.getStackInSlot(0);
        if (bufferStack.isEmpty()) return;
        Direction facing = getAttachedDirection();
        IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, getAttachedPos(), facing.getOpposite());
        if (inv == null) return;

        ItemStack remainder = ItemHandlerHelper.insertItemStacked(inv, bufferStack.copy(), false);
        if (remainder.isEmpty()) {
            buffer.setStackInSlot(0, ItemStack.EMPTY);
        } else {
            buffer.setStackInSlot(0, remainder);
        }
    }

    private int getMaxTransfer() {
        return upgrades.getLevel(UpgradeType.STACK_SIZE) > 0 ? 64 : 1;
    }

    private boolean matchesFilter(ItemStack stack) {
        ItemStack filterStack = filterSlot.getStackInSlot(0);
        if (filterStack.isEmpty()) return true;
        return filterStack.getItem() instanceof IItemFilter filter && filter.matches(filterStack, stack);
    }

    public ItemStackHandler getBuffer() { return buffer; }
    public ItemStackHandler getFilterSlot() { return filterSlot; }
    public UpgradeStackHandler getUpgrades() { return upgrades; }
    public int getCooldown() { return cooldown; }
    public int getSpeedLevel() { return upgrades.getLevel(UpgradeType.SPEED); }
    public BlockPos getCurrentPingPos() { return ping.getCurrentPos(); }

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

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public IItemHandler[] getDroppableInventories() {
        return new IItemHandler[]{buffer, filterSlot, upgrades};
    }

    @Override
    public float getGp() {
        float gp = 0;
        int speedLevel = upgrades.getLevel(UpgradeType.SPEED);
        if (speedLevel > 0) gp += UpgradeType.SPEED.getPowerUse(speedLevel);
        int stackLevel = upgrades.getLevel(UpgradeType.STACK_SIZE);
        if (stackLevel > 0) gp += UpgradeType.STACK_SIZE.getPowerUse(stackLevel);
        return gp;
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
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Transfer Node");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new TransferNodeMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Buffer", buffer.serializeNBT(provider));
        tag.put("Filter", filterSlot.serializeNBT(provider));
        tag.put("Upgrades", upgrades.serializeNBT(provider));
        tag.putInt("Cooldown", cooldown);
        tag.putInt("OwnerFreq", ownerFrequency);
        ping.save(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        buffer.deserializeNBT(provider, tag.getCompound("Buffer"));
        filterSlot.deserializeNBT(provider, tag.getCompound("Filter"));
        if (tag.contains("Upgrades")) upgrades.deserializeNBT(provider, tag.getCompound("Upgrades"));
        cooldown = tag.getInt("Cooldown");
        ownerFrequency = tag.getInt("OwnerFreq");
        ping.load(tag);
    }
}
