package com.leclowndu93150.extrautils2.api.power;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IGpSource {
    float getGp();

    int frequency();

    void onPowerChanged(boolean powered);

    boolean isLoaded();

    @Nullable Level level();

    @Nullable BlockPos getPos();

    default String getSourceName() {
        return getClass().getSimpleName();
    }
}
