package com.leclowndu93150.extrautils2.api.power;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IGpSourceItem {
    @Nullable IGpSource createSource(Player player, ItemStack stack);

    default boolean shouldOverride(IGpSource existing, Player player, ItemStack stack, boolean selected) {
        return false;
    }

    default boolean shouldSustain(IGpSource existing, ItemStack stack) {
        return true;
    }
}
