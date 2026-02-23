package com.leclowndu93150.extrautils2.item;

import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeItem extends Item {
    private final UpgradeType type;
    private final int maxStack;
    private final boolean foil;

    public UpgradeItem(UpgradeType type, int maxStack, boolean foil) {
        super(new Item.Properties().stacksTo(maxStack));
        this.type = type;
        this.maxStack = maxStack;
        this.foil = foil;
    }

    public UpgradeType getType() {
        return type;
    }

    public int getMaxStack() {
        return maxStack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return foil || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        int maxLevel = maxStack;
        tooltip.add(Component.translatable("tooltip.extrautils2.upgrade.max", maxLevel).withStyle(ChatFormatting.GRAY));
        if (type.power > 0.0f) {
            if (maxLevel == 1) {
                tooltip.add(Component.translatable("tooltip.extrautils2.upgrade.power.single", type.getPowerUse(1)).withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.extrautils2.upgrade.power.level1", type.getPowerUse(1)).withStyle(ChatFormatting.GRAY));
                int level = Math.min(stack.getCount(), maxLevel);
                if (level > 1) {
                    tooltip.add(Component.translatable("tooltip.extrautils2.upgrade.power.leveln", level, type.getPowerUse(level))
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }
}
