package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class WingsRenderer implements ICurioRenderer {

    private static final ResourceLocation[] WING_TEXTURES = {
        null,
        ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/wing_feather.png"),
        ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/wing_feather.png"),
        ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/wing_butterfly.png"),
        ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/wing_demon.png"),
        ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/wing_golden.png"),
        ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "textures/block/wing_bat.png"),
    };

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource bufferSource,
            int light, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {

        LivingEntity entity = slotContext.entity();
        int wingType = AngelRingItem.clientWings.getOrDefault(entity.getUUID(), 0);
        if (wingType <= 0 || wingType >= WING_TEXTURES.length) return;

        ResourceLocation texture = WING_TEXTURES[wingType];
        if (texture == null) return;

        M model = renderLayerParent.getModel();
        if (!(model instanceof HumanoidModel<?> humanoid)) return;

        boolean flying = !entity.onGround() && AngelRingItem.clientWings.containsKey(entity.getUUID());
        float angle = (float)(1.0 + Math.cos(ageInTicks / 4.0f)) * (flying ? 20f : 2f) + 25f;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));

        poseStack.pushPose();
        humanoid.body.translateAndRotate(poseStack);
        poseStack.translate(0.0, -0.3125 + 0.9, +0.110);
        if (entity.isCrouching()) poseStack.translate(0.0, 0.125, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
        renderWingQuad(poseStack, consumer, light, false);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        renderWingQuad(poseStack, consumer, light, true);
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderWingQuad(PoseStack poseStack, VertexConsumer consumer, int light, boolean flip) {
        PoseStack.Pose pose = poseStack.last();
        float x0 = flip ? 0f : -1f;
        float x1 = flip ? 1f : 0f;
        float u0 = flip ? 0f : 1f;
        float u1 = flip ? 1f : 0f;

        consumer.addVertex(pose, x0, -0.3125f, 0).setColor(255, 255, 255, 255).setUv(u0, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, x0,  0.6875f, 0).setColor(255, 255, 255, 255).setUv(u0, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, x1,  0.6875f, 0).setColor(255, 255, 255, 255).setUv(u1, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, x1, -0.3125f, 0).setColor(255, 255, 255, 255).setUv(u1, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
    }
}
