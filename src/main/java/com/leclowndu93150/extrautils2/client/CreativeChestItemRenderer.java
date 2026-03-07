package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.block.CreativeChestBlock;
import com.leclowndu93150.extrautils2.blockentity.CreativeChestBlockEntity;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BlockEntityRenderDispatcher dispatcher;
    private final CreativeChestBlockEntity chest =
            new CreativeChestBlockEntity(BlockPos.ZERO, defaultItemState());

    public CreativeChestItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack pose,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        this.chest.setBlockState(defaultItemState());
        this.dispatcher.renderItem(this.chest, pose, buffer, packedLight, packedOverlay);
    }

    private static BlockState defaultItemState() {
        return ModBlocks.CREATIVE_CHEST.get().defaultBlockState().setValue(CreativeChestBlock.FACING, Direction.SOUTH);
    }
}
