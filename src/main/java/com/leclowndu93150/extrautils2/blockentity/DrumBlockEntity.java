package com.leclowndu93150.extrautils2.blockentity;

import com.leclowndu93150.extrautils2.block.DrumBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class DrumBlockEntity extends XUBlockEntity {
    private final FluidTank tank;

    public DrumBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        int cap = 16000;
        boolean creative = false;
        if (state.getBlock() instanceof DrumBlock drum) {
            cap = drum.getCapacity().capacity * 1000;
            creative = drum.getCapacity() == DrumBlock.Capacity.DRUM_CREATIVE;
        }
        if (creative) {
            int capacity = cap;
            this.tank = new FluidTank(capacity) {
                @Override
                public int fill(FluidStack resource, FluidAction action) {
                    return 0;
                }

                @Override
                public FluidStack drain(int maxDrain, FluidAction action) {
                    if (fluid.isEmpty()) return FluidStack.EMPTY;
                    return fluid.copyWithAmount(maxDrain);
                }

                @Override
                public FluidStack drain(FluidStack resource, FluidAction action) {
                    if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) return FluidStack.EMPTY;
                    return resource.copyWithAmount(resource.getAmount());
                }

                @Override
                protected void onContentsChanged() {
                    setChanged();
                    sync();
                }
            };
        } else {
            this.tank = new FluidTank(cap) {
                @Override
                protected void onContentsChanged() {
                    setChanged();
                    sync();
                }
            };
        }
    }

    public FluidTank getTank() {
        return tank;
    }

    public boolean handleCreativeSetFluid(Player player, ItemStack stack) {
        if (!(getBlockState().getBlock() instanceof DrumBlock drum) || drum.getCapacity() != DrumBlock.Capacity.DRUM_CREATIVE) return false;
        if (!tank.isEmpty()) return false;
        if (!player.isCreative()) return false;
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);
        if (handler == null) return false;
        FluidStack drained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (drained.isEmpty()) return true;
        tank.setFluid(drained.copy());
        setChanged();
        sync();
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        tank.writeToNBT(lookupProvider, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        tank.readFromNBT(lookupProvider, tag);
    }
}
