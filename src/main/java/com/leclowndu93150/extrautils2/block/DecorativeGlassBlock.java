package com.leclowndu93150.extrautils2.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class DecorativeGlassBlock extends TransparentBlock {

    public DecorativeGlassBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    public static BlockBehaviour.Properties glassProps() {
        return BlockBehaviour.Properties.of()
                .strength(0.3f)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn((s, g, p, e) -> false)
                .isRedstoneConductor((s, g, p) -> false)
                .isSuffocating((s, g, p) -> false)
                .isViewBlocking((s, g, p) -> false);
    }

    public static class Dark extends DecorativeGlassBlock {
        public Dark(BlockBehaviour.Properties props) {
            super(props);
        }

        @Override
        protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
            return 15;
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.extrautils2.glass.dark").withStyle(ChatFormatting.GRAY));
        }
    }

    public static class Glowstone extends DecorativeGlassBlock {
        public Glowstone(BlockBehaviour.Properties props) {
            super(props);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.extrautils2.glass.glowing").withStyle(ChatFormatting.GRAY));
        }
    }

    public static class Redstone extends DecorativeGlassBlock {
        public Redstone(BlockBehaviour.Properties props) {
            super(props);
        }

        @Override
        protected boolean isSignalSource(BlockState state) {
            return true;
        }

        @Override
        protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
            return 15;
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.extrautils2.glass.redstone").withStyle(ChatFormatting.GRAY));
        }
    }

    public static class Ineffable extends DecorativeGlassBlock {
        private final boolean reversed;
        private final String tooltipKey;

        public Ineffable(BlockBehaviour.Properties props, boolean reversed) {
            super(props);
            this.reversed = reversed;
            this.tooltipKey = reversed ? "tooltip.extrautils2.glass.ethereal.reverse" : "tooltip.extrautils2.glass.ethereal";
        }

        @Override
        protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            if (context instanceof EntityCollisionContext ecc) {
                Entity entity = ecc.getEntity();
                boolean allowPassage = reversed != isNonSneakingPlayer(entity);
                if (allowPassage) {
                    return Shapes.empty();
                }
            }
            return Shapes.block();
        }

        private static boolean isNonSneakingPlayer(Entity entity) {
            return entity instanceof Player && !entity.isShiftKeyDown();
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
        }
    }

    public static class IneffableDark extends Ineffable {
        public IneffableDark(BlockBehaviour.Properties props, boolean reversed) {
            super(props, reversed);
        }

        @Override
        protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
            return 15;
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, ctx, tooltip, flag);
            tooltip.add(Component.translatable("tooltip.extrautils2.glass.dark").withStyle(ChatFormatting.GRAY));
        }
    }
}
