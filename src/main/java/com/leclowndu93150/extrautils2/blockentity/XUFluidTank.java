package com.leclowndu93150.extrautils2.blockentity;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class XUFluidTank extends FluidTank {
    private Runnable changeListener = () -> {};

    public XUFluidTank(int capacity) {
        super(capacity);
    }

    public XUFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    public XUFluidTank setListener(Runnable listener) {
        this.changeListener = listener;
        return this;
    }

    @Override
    protected void onContentsChanged() {
        changeListener.run();
    }

    public static class Creative extends XUFluidTank {
        public Creative(int capacity) {
            super(capacity);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (fluid.isEmpty() && !resource.isEmpty() && isFluidValid(resource)) {
                if (action.execute()) {
                    fluid = resource.copyWithAmount(getCapacity());
                    onContentsChanged();
                }
                return resource.getAmount();
            }
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
    }
}
