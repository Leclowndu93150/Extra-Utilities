package com.leclowndu93150.extrautils2.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AngelBlockItem extends BlockItem {
    public AngelBlockItem(Block block, Properties props) {
        super(block, props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        Vec3 look = player.getLookAngle();
        Direction dir = Direction.getNearest(look.x, look.y, look.z);

        int x = (int) Math.floor(player.getX());
        int y = (int) Math.floor(player.getY() + player.getEyeHeight());
        int z = (int) Math.floor(player.getZ());

        switch (dir) {
            case DOWN -> y = (int) (Math.floor(player.getBoundingBox().minY) - 1);
            case UP -> y = (int) (Math.ceil(player.getBoundingBox().maxY) + 1);
            case NORTH -> z = (int) (Math.floor(player.getBoundingBox().minZ) - 1);
            case SOUTH -> z = (int) (Math.floor(player.getBoundingBox().maxZ) + 1);
            case WEST -> x = (int) (Math.floor(player.getBoundingBox().minX) - 1);
            case EAST -> x = (int) (Math.floor(player.getBoundingBox().maxX) + 1);
        }

        BlockPos pos = new BlockPos(x, y, z);
        if (!level.getBlockState(pos).canBeReplaced()) {
            return InteractionResultHolder.fail(stack);
        }

        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), dir.getOpposite(), pos, false);
        BlockPlaceContext ctx = new BlockPlaceContext(new UseOnContext(level, player, hand, stack, hitResult));
        InteractionResult result = this.place(ctx);
        if (result.consumesAction()) {
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.fail(stack);
    }
}
