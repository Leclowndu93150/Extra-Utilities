package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.io.InputStream;

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

    private static final boolean[][][] PIXEL_CACHE = new boolean[WING_TEXTURES.length][][];

    private static boolean[][] loadPixels(ResourceLocation loc) {
        boolean[][] data = new boolean[16][16];
        try {
            Resource res = Minecraft.getInstance().getResourceManager().getResource(loc).orElseThrow();
            try (InputStream stream = res.open();
                 NativeImage img =
                         NativeImage.read(stream)) {
                for (int y = 0; y < Math.min(16, img.getHeight()); y++) {
                    for (int x = 0; x < Math.min(16, img.getWidth()); x++) {
                        int argb = img.getPixelRGBA(x, y);
                        data[y][x] = ((argb >> 24) & 0xFF) > 0;
                    }
                }
            }
        } catch (Exception ignored) {}
        return data;
    }

    private static boolean[][] getPixels(int wingType) {
        if (PIXEL_CACHE[wingType] == null && WING_TEXTURES[wingType] != null) {
            PIXEL_CACHE[wingType] = loadPixels(WING_TEXTURES[wingType]);
        }
        return PIXEL_CACHE[wingType];
    }

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

        boolean[][] pixels = getPixels(wingType);
        if (pixels == null) return;

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
        renderVoxelWing(poseStack, consumer, light, pixels, false);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        renderVoxelWing(poseStack, consumer, light, pixels, true);
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderVoxelWing(PoseStack poseStack, VertexConsumer consumer, int light, boolean[][] pixels, boolean flip) {
        float S = 1f / 16f;

        for (int py = 0; py < 16; py++) {
            for (int px = 0; px < 16; px++) {
                if (!pixels[py][px]) continue;

                float u0 = px / 16f;
                float u1 = (px + 1) / 16f;
                float v0 = py / 16f;
                float v1 = (py + 1) / 16f;

                float y0 = -py * S;
                float y1 = -(py + 1) * S;

                if (flip) {
                    float x0 = px * S;
                    float x1 = (px + 1) * S;
                    renderFace(poseStack, consumer, light, x0, x1, y0, y1, 0, u0, u1, v0, v1);
                } else {
                    float x0 = -(px * S);
                    float x1 = -((px + 1) * S);
                    renderFace(poseStack, consumer, light, x0, x1, y0, y1, 0, u1, u0, v0, v1);
                }
            }
        }
    }

    private void renderFace(PoseStack poseStack, VertexConsumer consumer, int light,
                             float x0, float x1, float y0, float y1, float z,
                             float u0, float u1, float v0, float v1) {
        PoseStack.Pose pose = poseStack.last();
        consumer.addVertex(pose, x0, y0, z).setColor(255, 255, 255, 255).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, x0, y1, z).setColor(255, 255, 255, 255).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, x1, y1, z).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
        consumer.addVertex(pose, x1, y0, z).setColor(255, 255, 255, 255).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 0, 1);
    }
}
