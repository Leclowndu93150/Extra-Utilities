package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.CreativeChestBlock;
import com.leclowndu93150.extrautils2.blockentity.CreativeChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CreativeChestRenderer implements BlockEntityRenderer<CreativeChestBlockEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/entity/creative_chest.png");

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public CreativeChestRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart model = ctx.bakeLayer(ModelLayers.CHEST);
        this.bottom = model.getChild("bottom");
        this.lid = model.getChild("lid");
        this.lock = model.getChild("lock");
    }

    @Override
    public void render(CreativeChestBlockEntity tile, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        pose.pushPose();
        float yRot = tile.getBlockState().getValue(CreativeChestBlock.FACING).toYRot();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(-yRot));
        pose.translate(-0.5F, -0.5F, -0.5F);

        float openness = tile.getOpenNess(partialTick);
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;

        this.lid.xRot = -(openness * ((float) Math.PI / 2F));
        this.lock.xRot = this.lid.xRot;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        this.lid.render(pose, consumer, packedLight, packedOverlay);
        this.lock.render(pose, consumer, packedLight, packedOverlay);
        this.bottom.render(pose, consumer, packedLight, packedOverlay);
        pose.popPose();
    }
}
