package com.leclowndu93150.extrautils2.blockentity.transfer;

import com.leclowndu93150.extrautils2.api.fluids.IFluidFilter;
import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.block.transfer.TransferHelper;
import com.leclowndu93150.extrautils2.block.transfer.TransferNodeBlock;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.gui.transfer.FluidTransferNodeMenu;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FluidTransferNodeBlockEntity extends XUBlockEntity implements MenuProvider, IGpSource, MachineBlock.IGpMachine, MachineBlock.IDroppableInventory {
    public static final int UPGRADE_SLOTS = 6;

    private final FluidTank buffer = new FluidTank(FluidType.BUCKET_VOLUME) {
        @Override
        protected void onContentsChanged() {
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
            return stack.getItem() instanceof IFluidFilter;
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

    public FluidTransferNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_TRANSFER_NODE.get(), pos, state);
    }

    public boolean isRetrieval() {
        if (getBlockState().getBlock() instanceof TransferNodeBlock node) {
            return node.isRetrieval();
        }
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluidTransferNodeBlockEntity tile) {
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
            if (tile.buffer.isEmpty()) {
                tile.tryRetrieveAtPingPos();
            }
            if (!tile.buffer.isEmpty()) {
                tile.pushBufferToAttached();
            }
            if (tile.buffer.isEmpty()) {
                tile.ping.resetPosition();
            } else {
                tile.ping.advance(level);
            }
        } else {
            tile.extractIntoBuffer();
            if (!tile.buffer.isEmpty()) {
                tile.tryDeliverAtPingPos();
            }
            if (tile.buffer.isEmpty()) {
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

    private int getMaxTransfer() {
        return upgrades.getLevel(UpgradeType.STACK_SIZE) > 0 ? 12_800 : 200;
    }

    private void extractIntoBuffer() {
        Direction facing = getAttachedDirection();
        IFluidHandler source = level.getCapability(Capabilities.FluidHandler.BLOCK, getAttachedPos(), facing.getOpposite());
        if (source == null) {
            mineWorldFluidSource();
            return;
        }

        FluidStack simulated = source.drain(getMaxTransfer(), IFluidHandler.FluidAction.SIMULATE);
        if (simulated.isEmpty() || !matchesFilter(simulated)) return;

        int fillable = buffer.fill(simulated, IFluidHandler.FluidAction.SIMULATE);
        if (fillable <= 0) return;

        FluidStack drained = source.drain(fillable, IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty() && matchesFilter(drained)) {
            buffer.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private void mineWorldFluidSource() {
        int miningLevel = upgrades.getLevel(UpgradeType.MINING);
        if (miningLevel <= 0) return;

        FluidStack current = buffer.getFluid();
        if (!current.isEmpty() && (!current.getFluid().isSame(Fluids.WATER) || current.getAmount() >= buffer.getCapacity())) {
            return;
        }

        BlockPos targetPos = getAttachedPos();
        var fluidState = level.getFluidState(targetPos);
        if (!fluidState.isSource() || !fluidState.getType().isSame(Fluids.WATER)) return;

        int adjacentSources = 0;
        for (Direction direction : getHorizontalOrthogonalDirections(getAttachedDirection())) {
            var adjacentFluid = level.getFluidState(targetPos.relative(direction));
            if (adjacentFluid.isSource() && adjacentFluid.getType().isSame(Fluids.WATER)) {
                adjacentSources++;
                if (adjacentSources >= 2) break;
            }
        }

        if (adjacentSources >= 2 && level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION)) {
            FluidStack generated = new FluidStack(Fluids.WATER, 200);
            if (matchesFilter(generated)) {
                buffer.fill(generated, IFluidHandler.FluidAction.EXECUTE);
            }
        }
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
        FluidStack bufferFluid = buffer.getFluid();
        if (bufferFluid.isEmpty()) return;

        for (Direction dir : TransferHelper.getShuffledDirections()) {
            BlockPos neighbor = pingPos.relative(dir);
            if (TransferHelper.isPipe(level, neighbor)) continue;
            if (neighbor.equals(worldPosition)) continue;

            IFluidHandler target = level.getCapability(Capabilities.FluidHandler.BLOCK, neighbor, dir.getOpposite());
            if (target == null) continue;

            int filled = target.fill(bufferFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0) {
                buffer.drain(filled, IFluidHandler.FluidAction.EXECUTE);
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

            IFluidHandler source = level.getCapability(Capabilities.FluidHandler.BLOCK, neighbor, dir.getOpposite());
            if (source == null) continue;

            FluidStack simulated = source.drain(maxTransfer, IFluidHandler.FluidAction.SIMULATE);
            if (simulated.isEmpty() || !matchesFilter(simulated)) continue;

            FluidStack drained = source.drain(maxTransfer, IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty() && matchesFilter(drained)) {
                buffer.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                return;
            }
        }
    }

    private void pushBufferToAttached() {
        if (buffer.isEmpty()) return;
        Direction facing = getAttachedDirection();
        IFluidHandler target = level.getCapability(Capabilities.FluidHandler.BLOCK, getAttachedPos(), facing.getOpposite());
        if (target == null) return;

        FluidStack bufferFluid = buffer.getFluid();
        int filled = target.fill(bufferFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
        if (filled > 0) {
            buffer.drain(filled, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public FluidTank getBuffer() { return buffer; }
    public ItemStackHandler getFilterSlot() { return filterSlot; }
    public UpgradeStackHandler getUpgrades() { return upgrades; }
    public int getCooldown() { return cooldown; }
    public BlockPos getCurrentPingPos() { return ping.getCurrentPos(); }

    private boolean matchesFilter(FluidStack stack) {
        ItemStack filterStack = filterSlot.getStackInSlot(0);
        if (filterStack.isEmpty()) return true;
        return filterStack.getItem() instanceof IFluidFilter filter && filter.matches(filterStack, stack);
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

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public IItemHandler[] getDroppableInventories() {
        return new IItemHandler[]{filterSlot, upgrades};
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
        return new FluidTransferNodeMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Buffer", buffer.writeToNBT(provider, new CompoundTag()));
        tag.put("Filter", filterSlot.serializeNBT(provider));
        tag.put("Upgrades", upgrades.serializeNBT(provider));
        tag.putInt("Cooldown", cooldown);
        tag.putInt("OwnerFreq", ownerFrequency);
        ping.save(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        buffer.readFromNBT(provider, tag.getCompound("Buffer"));
        if (tag.contains("Filter")) filterSlot.deserializeNBT(provider, tag.getCompound("Filter"));
        if (tag.contains("Upgrades")) upgrades.deserializeNBT(provider, tag.getCompound("Upgrades"));
        cooldown = tag.getInt("Cooldown");
        ownerFrequency = tag.getInt("OwnerFreq");
        ping.load(tag);
    }
}
