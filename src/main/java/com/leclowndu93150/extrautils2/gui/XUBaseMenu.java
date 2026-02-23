package com.leclowndu93150.extrautils2.gui;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import com.leclowndu93150.extrautils2.upgrade.UpgradeStackHandler;

public abstract class XUBaseMenu extends AbstractContainerMenu {
    public static final int INV_SLOT_X_OFFSET = 1;
    public static final int INV_SLOT_Y_OFFSET = 1;
    protected static final int MENU_SLOT_X_OFFSET = 1;
    protected static final int MENU_SLOT_Y_OFFSET = 1;

    protected XUBaseMenu(MenuType<?> type, int id) {
        super(type, id);
    }

    protected void addPlayerSlots(Inventory inv, int x, int y) {
        int baseX = x + INV_SLOT_X_OFFSET;
        int baseY = y + INV_SLOT_Y_OFFSET;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, baseX + col * 18, baseY + 14 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, baseX + col * 18, baseY + 14 + 58));
        }
    }

    protected void addUpgradeSlot(UpgradeStackHandler upgrades, int x, int y) {
        addSlot(new UpgradeSlot(upgrades, 0, menuSlotX(x), menuSlotY(y)));
    }

    protected int addUpgradeSlotAndGetIndex(UpgradeStackHandler upgrades, int x, int y) {
        int index = slots.size();
        addUpgradeSlot(upgrades, x, y);
        return index;
    }

    protected int menuSlotX(int x) {
        return x + MENU_SLOT_X_OFFSET;
    }

    protected int menuSlotY(int y) {
        return y + MENU_SLOT_Y_OFFSET;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this instanceof HasRedstoneControl rs && id == rs.getRedstoneButtonId()) {
            rs.cycleRedstone();
            return true;
        }
        if (this instanceof HasFluidBar fluid && id == fluid.getFluidBarButtonId()) {
            IFluidHandler tank = fluid.getFluidHandler();
            if (tank == null) return false;
            ItemStack carried = getCarried();
            if (carried.isEmpty()) return false;
            var handlerOpt = FluidUtil.getFluidHandler(carried);
            if (handlerOpt.isEmpty()) return false;
            IFluidHandlerItem itemHandler = handlerOpt.get();
            FluidStack transferred = FluidUtil.tryFluidTransfer(tank, itemHandler, Integer.MAX_VALUE, true);
            boolean filledTank = !transferred.isEmpty();
            if (!filledTank) {
                transferred = FluidUtil.tryFluidTransfer(itemHandler, tank, Integer.MAX_VALUE, true);
            }
            if (!transferred.isEmpty()) {
                setCarried(itemHandler.getContainer());
                fluid.onFluidChanged();
                SoundEvent sound = transferred.getFluidType().getSound(transferred,
                        filledTank ? SoundActions.BUCKET_EMPTY : SoundActions.BUCKET_FILL);
                if (sound != null) {
                    player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                            sound, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return true;
        }
        return super.clickMenuButton(player, id);
    }
}
