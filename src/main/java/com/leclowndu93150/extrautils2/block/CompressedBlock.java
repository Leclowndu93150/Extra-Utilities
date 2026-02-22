package com.leclowndu93150.extrautils2.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CompressedBlock extends Block {
    public final int level;

    public CompressedBlock(int level, BlockBehaviour.Properties props) {
        super(props);
        this.level = level;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        long blocks = Math.round(Math.pow(9.0, level));
        String count = NumberFormat.getIntegerInstance(Locale.US).format(blocks);
        tooltip.add(Component.translatable("tooltip.extrautils2.compressed", count).withStyle(ChatFormatting.GRAY));
    }
}
