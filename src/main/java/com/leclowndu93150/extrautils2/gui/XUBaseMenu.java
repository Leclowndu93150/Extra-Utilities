package com.leclowndu93150.extrautils2.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class XUBaseMenu extends AbstractContainerMenu {
    public static final int INV_SLOT_X_OFFSET = 1;
    public static final int INV_SLOT_Y_OFFSET = 1;

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
}
