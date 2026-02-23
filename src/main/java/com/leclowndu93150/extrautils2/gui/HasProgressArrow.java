package com.leclowndu93150.extrautils2.gui;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface HasProgressArrow {
    int getArrowX();
    int getArrowY();
    float getArrowProgress();
    boolean isArrowOverloaded();
    List<Component> getArrowTooltip();
    List<Component> getArrowErrorTooltip();
}
