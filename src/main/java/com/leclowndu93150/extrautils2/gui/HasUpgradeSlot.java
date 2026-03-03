package com.leclowndu93150.extrautils2.gui;

public interface HasUpgradeSlot {
    int getUpgradeX();
    int getUpgradeY();

    default int getUpgradeSlotCount() {
        return 1;
    }

    default int getUpgradeX(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException("Upgrade slot index out of range: " + index);
        }
        return getUpgradeX();
    }

    default int getUpgradeY(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException("Upgrade slot index out of range: " + index);
        }
        return getUpgradeY();
    }
}
