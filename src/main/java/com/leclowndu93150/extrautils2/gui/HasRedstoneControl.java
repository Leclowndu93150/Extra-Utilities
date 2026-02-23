package com.leclowndu93150.extrautils2.gui;

import com.leclowndu93150.extrautils2.util.RedstoneState;

public interface HasRedstoneControl {
    int getRedstoneX();
    int getRedstoneY();
    int getRedstoneButtonId();
    RedstoneState getRedstoneState();
    boolean hasRedstonePulseMode();
    void cycleRedstone();
}
